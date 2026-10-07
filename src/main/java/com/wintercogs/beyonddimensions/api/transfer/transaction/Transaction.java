package com.wintercogs.beyonddimensions.api.transfer.transaction;

import java.util.ArrayList;
import java.util.List;

/**
 * 移植垫片：NeoForge 26.x 的事务。
 * <p>
 * 行为与上游（以及它借鉴的 Fabric Transfer API）一致：
 * <ul>
 *     <li>{@code openRoot()} 开一个根事务，{@code try-with-resources} 结束时若未 {@code commit()} 则回滚；</li>
 *     <li>{@link SnapshotJournal} 在 {@code updateSnapshots} 时登记快照，事务关闭时未提交就 {@code revertToSnapshot}。</li>
 * </ul>
 */
public final class Transaction implements TransactionContext, AutoCloseable
{
    @FunctionalInterface
    public interface CloseCallback
    {
        void onClose(Transaction transaction, boolean wasCommitted);
    }

    private record Entry(Object key, CloseCallback callback) {}

    private final Transaction outer;
    private final List<Entry> closeCallbacks = new ArrayList<>();
    private final List<CloseCallback> outerCloseCallbacks = new ArrayList<>();
    private boolean open = true;
    private boolean committed = false;

    private Transaction(Transaction outer)
    {
        this.outer = outer;
    }

    public static Transaction openRoot()
    {
        return new Transaction(null);
    }

    public static Transaction openNested(TransactionContext context)
    {
        Transaction outer = context == null ? null : context.getOpenTransaction();
        if (outer == null || !outer.open)
        {
            throw new IllegalStateException("Cannot open a nested transaction: the outer transaction is not open");
        }
        return new Transaction(outer);
    }

    @Override
    public Transaction getOpenTransaction()
    {
        return this;
    }

    @Override
    public void addCloseCallback(Object key, CloseCallback callback)
    {
        closeCallbacks.add(new Entry(key, callback));
    }

    /** 在外层事务关闭后才执行的回调；没有外层时立即登记到本事务。 */
    public void addOuterCloseCallback(CloseCallback callback)
    {
        if (outer != null)
        {
            outer.addCloseCallback(this, callback);
        }
        else
        {
            outerCloseCallbacks.add(callback);
        }
    }

    public boolean isOpen()
    {
        return open;
    }

    public boolean wasCommitted()
    {
        return committed;
    }

    public void commit()
    {
        if (!open)
        {
            throw new IllegalStateException("Cannot commit a transaction that is already closed");
        }
        committed = true;
    }

    @Override
    public void close()
    {
        if (!open)
        {
            return;
        }
        open = false;

        // 后注册的先回滚，保证嵌套快照顺序正确
        for (int i = closeCallbacks.size() - 1; i >= 0; i--)
        {
            closeCallbacks.get(i).callback().onClose(this, committed);
        }
        for (CloseCallback callback : outerCloseCallbacks)
        {
            callback.onClose(this, committed);
        }
    }
}