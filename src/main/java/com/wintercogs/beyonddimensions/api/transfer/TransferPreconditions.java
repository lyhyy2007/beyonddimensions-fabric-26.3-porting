package com.wintercogs.beyonddimensions.api.transfer;

/** 移植垫片：参数校验，沿用上游的抛错语义。 */
public final class TransferPreconditions
{
    private TransferPreconditions() {}

    public static void checkNonEmpty(Resource resource)
    {
        if (resource == null || resource.isEmpty())
        {
            throw new IllegalArgumentException("Expected a non-empty resource");
        }
    }

    public static void checkNonNegative(int amount)
    {
        if (amount < 0)
        {
            throw new IllegalArgumentException("Amount must be non-negative, got " + amount);
        }
    }

    public static void checkNonNegative(long amount)
    {
        if (amount < 0L)
        {
            throw new IllegalArgumentException("Amount must be non-negative, got " + amount);
        }
    }

    public static void checkNonEmptyNonNegative(Resource resource, int amount)
    {
        checkNonEmpty(resource);
        checkNonNegative(amount);
    }

    public static void checkNonEmptyNonNegative(Resource resource, long amount)
    {
        checkNonEmpty(resource);
        checkNonNegative(amount);
    }
}