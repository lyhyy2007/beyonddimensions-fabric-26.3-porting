package com.wintercogs.beyonddimensions.api.transfer.transaction;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * 移植垫片：NeoForge 26.x 的快照日志。
 * <p>
 * 子类在「即将修改自身状态」之前调用 {@link #updateSnapshots(TransactionContext)}，
 * 事务未提交而关闭时会自动回滚到当时的快照。
 */
public abstract class SnapshotJournal<T>
{
    private final Map<Transaction, T> snapshots = new IdentityHashMap<>();

    protected abstract T createSnapshot();

    protected abstract void revertToSnapshot(T snapshot);

    protected void updateSnapshots(TransactionContext transaction)
    {
        if (transaction == null)
        {
            return;
        }
        Transaction open = transaction.getOpenTransaction();
        if (open == null)
        {
            return;
        }

        snapshots.computeIfAbsent(open, tx -> {
            tx.addCloseCallback(this, (closed, wasCommitted) -> {
                T snapshot = snapshots.remove(closed);
                if (snapshot != null && !wasCommitted)
                {
                    revertToSnapshot(snapshot);
                }
            });
            return createSnapshot();
        });
    }
}