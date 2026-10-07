package com.wintercogs.beyonddimensions.api.capability;

import com.wintercogs.beyonddimensions.api.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.Nullable;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * 移植垫片内部实现：把 NeoForge 事务桥接到 Fabric Transfer API 事务。
 * <p>
 * 每个 NeoForge 事务对应一个 Fabric 外层事务；NeoForge 事务提交时提交 Fabric 事务，
 * 否则在其关闭时自动回滚。这样对外部容器的「模拟操作」语义得以保留。
 * <p>
 * 单个适配器实例内部使用（游戏逻辑单线程访问）。
 */
final class TransactionBridge
{
    private final Map<com.wintercogs.beyonddimensions.api.transfer.transaction.Transaction,
            net.fabricmc.fabric.api.transfer.v1.transaction.Transaction> bridged = new IdentityHashMap<>();

    @Nullable
    net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext toFabric(@Nullable TransactionContext context)
    {
        if (context == null)
        {
            return null;
        }
        com.wintercogs.beyonddimensions.api.transfer.transaction.Transaction neo = context.getOpenTransaction();
        if (neo == null)
        {
            return null;
        }

        return bridged.computeIfAbsent(neo, key -> {
            net.fabricmc.fabric.api.transfer.v1.transaction.Transaction fabric =
                    net.fabricmc.fabric.api.transfer.v1.transaction.Transaction.openOuter();
            key.addCloseCallback(this, (closed, wasCommitted) -> {
                net.fabricmc.fabric.api.transfer.v1.transaction.Transaction open = bridged.remove(closed);
                if (open != null)
                {
                    if (wasCommitted)
                    {
                        open.commit();
                    }
                    open.close();
                }
            });
            return fabric;
        });
    }
}