package com.wintercogs.beyonddimensions.api.transfer;

import com.wintercogs.beyonddimensions.api.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.NotNull;

/**
 * 移植垫片：NeoForge 26.x 的 {@code ResourceHandler}。
 * <p>
 * 返回约定与上游一致：{@code insert} 返回「实际插入量」，{@code extract} 返回「实际抽取量」。
 */
public interface ResourceHandler<T extends Resource>
{
    int size();

    @NotNull T getResource(int index);

    long getAmountAsLong(int index);

    default int getAmountAsInt(int index)
    {
        return (int) Math.min(getAmountAsLong(index), Integer.MAX_VALUE);
    }

    long getCapacityAsLong(int index, @NotNull T resource);

    default int getCapacityAsInt(int index, @NotNull T resource)
    {
        return (int) Math.min(getCapacityAsLong(index, resource), Integer.MAX_VALUE);
    }

    boolean isValid(int index, @NotNull T resource);

    int insert(int index, @NotNull T resource, int amount, @NotNull TransactionContext transaction);

    int extract(int index, @NotNull T resource, int amount, @NotNull TransactionContext transaction);

    /** 全容器插入；返回实际插入量。 */
    default int insert(@NotNull T resource, int amount, @NotNull TransactionContext transaction)
    {
        int inserted = 0;
        int slots = size();
        for (int i = 0; i < slots && inserted < amount; i++)
        {
            inserted += insert(i, resource, amount - inserted, transaction);
        }
        return inserted;
    }

    /** 全容器抽取；返回实际抽取量。 */
    default int extract(@NotNull T resource, int amount, @NotNull TransactionContext transaction)
    {
        int extracted = 0;
        int slots = size();
        for (int i = 0; i < slots && extracted < amount; i++)
        {
            extracted += extract(i, resource, amount - extracted, transaction);
        }
        return extracted;
    }
}