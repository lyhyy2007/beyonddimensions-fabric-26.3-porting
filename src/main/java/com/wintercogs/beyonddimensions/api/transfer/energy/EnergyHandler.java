package com.wintercogs.beyonddimensions.api.transfer.energy;

import com.wintercogs.beyonddimensions.api.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.NotNull;

/**
 * 移植垫片：NeoForge 26.x 的能量处理器。
 * <p>
 * 返回约定同 {@code ResourceHandler}：insert 返回插入量，extract 返回抽取量。
 */
public interface EnergyHandler
{
    long getAmountAsLong();

    long getCapacityAsLong();

    int insert(int amount, @NotNull TransactionContext transaction);

    int extract(int amount, @NotNull TransactionContext transaction);
}