package com.wintercogs.beyonddimensions.api.transfer;

import com.wintercogs.beyonddimensions.api.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.NotNull;

/** 移植垫片：把多个 {@link ResourceHandler} 视作一个连续槽位序列。 */
public class CombinedResourceHandler<T extends Resource> implements ResourceHandler<T>
{
    private final ResourceHandler<T>[] handlers;
    private final int[] offsets;
    private final int totalSize;

    @SafeVarargs
    public CombinedResourceHandler(ResourceHandler<T>... handlers)
    {
        this.handlers = handlers.clone();
        this.offsets = new int[this.handlers.length];
        int sum = 0;
        for (int i = 0; i < this.handlers.length; i++)
        {
            offsets[i] = sum;
            sum += this.handlers[i].size();
        }
        this.totalSize = sum;
    }

    private int handlerIndex(int index)
    {
        for (int i = handlers.length - 1; i >= 0; i--)
        {
            if (index >= offsets[i])
            {
                return i;
            }
        }
        return 0;
    }

    @Override
    public int size()
    {
        return totalSize;
    }

    @Override
    public @NotNull T getResource(int index)
    {
        int h = handlerIndex(index);
        return handlers[h].getResource(index - offsets[h]);
    }

    @Override
    public long getAmountAsLong(int index)
    {
        int h = handlerIndex(index);
        return handlers[h].getAmountAsLong(index - offsets[h]);
    }

    @Override
    public long getCapacityAsLong(int index, @NotNull T resource)
    {
        int h = handlerIndex(index);
        return handlers[h].getCapacityAsLong(index - offsets[h], resource);
    }

    @Override
    public boolean isValid(int index, @NotNull T resource)
    {
        int h = handlerIndex(index);
        return handlers[h].isValid(index - offsets[h], resource);
    }

    @Override
    public int insert(int index, @NotNull T resource, int amount, @NotNull TransactionContext transaction)
    {
        int h = handlerIndex(index);
        return handlers[h].insert(index - offsets[h], resource, amount, transaction);
    }

    @Override
    public int extract(int index, @NotNull T resource, int amount, @NotNull TransactionContext transaction)
    {
        int h = handlerIndex(index);
        return handlers[h].extract(index - offsets[h], resource, amount, transaction);
    }
}