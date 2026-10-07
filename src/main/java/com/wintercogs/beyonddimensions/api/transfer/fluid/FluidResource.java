package com.wintercogs.beyonddimensions.api.transfer.fluid;

import net.minecraft.world.level.material.Fluid;
import com.wintercogs.beyonddimensions.api.fluid.FluidStack;
import com.wintercogs.beyonddimensions.api.transfer.Resource;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** 移植垫片：NeoForge 26.x 的 {@code FluidResource}（流体 + 组件，不含数量）。 */
public final class FluidResource extends Resource
{
    private static final Map<FluidStack, FluidResource> CACHE = new ConcurrentHashMap<>();

    public static final FluidResource EMPTY = new FluidResource(FluidStack.EMPTY);

    private final FluidStack stack;

    private FluidResource(FluidStack stack)
    {
        this.stack = stack;
    }

    public static @NotNull FluidResource of(FluidStack stack)
    {
        if (stack == null || stack.isEmpty())
        {
            return EMPTY;
        }
        FluidStack prototype = stack.getAmount() == 1 ? stack : stack.copyWithAmount(1);
        return CACHE.computeIfAbsent(prototype, FluidResource::new);
    }

    @Override
    public boolean isEmpty()
    {
        return this == EMPTY || stack.isEmpty();
    }

    public FluidStack toStack()
    {
        return stack;
    }

    public FluidStack toStack(int amount)
    {
        return isEmpty() ? FluidStack.EMPTY : stack.copyWithAmount(amount);
    }

    public Fluid getFluid()
    {
        return stack.getFluid();
    }

    public int getMaxStackSize()
    {
        return 64_000;
    }

    public boolean matches(FluidStack other)
    {
        if (isEmpty() || other == null || other.isEmpty())
        {
            return false;
        }
        return stack.isComponentsPatchEqual(other);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof FluidResource other)) return false;
        return stack.isComponentsPatchEqual(other.stack);
    }

    @Override
    public int hashCode()
    {
        return stack.isEmpty() ? 0 : 31 * stack.getFluid().hashCode() + stack.getComponentsPatch().hashCode();
    }

    @Override
    public String toString()
    {
        return isEmpty() ? "FluidResource.EMPTY" : stack.getFluid().toString();
    }
}