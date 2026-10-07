package com.wintercogs.beyonddimensions.api.transfer.energy;

import com.wintercogs.beyonddimensions.api.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.NotNull;

/** 移植垫片：恒空能量处理器。 */
public final class EmptyEnergyHandler implements EnergyHandler
{
    public static final EmptyEnergyHandler INSTANCE = new EmptyEnergyHandler();

    private EmptyEnergyHandler() {}

    @Override
    public long getAmountAsLong()
    {
        return 0L;
    }

    @Override
    public long getCapacityAsLong()
    {
        return 0L;
    }

    @Override
    public int insert(int amount, @NotNull TransactionContext transaction)
    {
        return 0;
    }

    @Override
    public int extract(int amount, @NotNull TransactionContext transaction)
    {
        return 0;
    }
}