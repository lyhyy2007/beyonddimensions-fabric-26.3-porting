package com.wintercogs.beyonddimensions.api.fluid;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * 移植垫片：NeoForge 的「流体 + 数量 (+ 组件)」。
 * <p>
 * Fabric 没有等价类型，这里自建一个最小可用实现，语义对齐上游：
 * 不可变要素是 {@code fluid + components}，{@code amount} 可变。
 */
public class FluidStack
{
    public static final FluidStack EMPTY = new FluidStack(Fluids.EMPTY, 0, DataComponentPatch.EMPTY);

    private final Holder<Fluid> holder;
    private final DataComponentPatch patch;
    private int amount;

    public FluidStack(Fluid fluid, int amount)
    {
        this(fluid, amount, DataComponentPatch.EMPTY);
    }

    public FluidStack(Fluid fluid, int amount, DataComponentPatch patch)
    {
        this(fluid == null ? Fluids.EMPTY.builtInRegistryHolder() : fluid.builtInRegistryHolder(), amount, patch);
    }

    public FluidStack(Holder<Fluid> holder, int amount)
    {
        this(holder, amount, DataComponentPatch.EMPTY);
    }

    public FluidStack(Holder<Fluid> holder, int amount, DataComponentPatch patch)
    {
        this.holder = (holder == null) ? Fluids.EMPTY.builtInRegistryHolder() : holder;
        this.amount = Math.max(0, amount);
        this.patch = (patch == null) ? DataComponentPatch.EMPTY : patch;
    }

    public FluidStack(Supplier<? extends Fluid> fluid, int amount)
    {
        this(fluid == null ? Fluids.EMPTY : fluid.get(), amount, DataComponentPatch.EMPTY);
    }

    public FluidStack(FluidStack other)
    {
        this(other.holder, other.amount, other.patch);
    }

    public static FluidStack of(Fluid fluid, int amount)
    {
        return amount <= 0 || fluid == null || fluid == Fluids.EMPTY ? EMPTY : new FluidStack(fluid, amount);
    }

    public Fluid getFluid()
    {
        return holder.value();
    }

    public Holder<Fluid> getFluidHolder()
    {
        return holder;
    }

    public int getAmount()
    {
        return amount;
    }

    public void setAmount(int amount)
    {
        this.amount = Math.max(0, amount);
    }

    public void grow(int delta)
    {
        setAmount(this.amount + delta);
    }

    public void shrink(int delta)
    {
        setAmount(this.amount - delta);
    }

    public boolean isEmpty()
    {
        return amount <= 0 || getFluid() == Fluids.EMPTY;
    }

    public DataComponentPatch getComponentsPatch()
    {
        return patch;
    }

    /** 上游提供的展示名：取该流体对应桶物品的名称，无桶时退回流体注册名。 */
    public net.minecraft.network.chat.Component getHoverName()
    {
        if (isEmpty())
        {
            return net.minecraft.network.chat.Component.empty();
        }
        Item bucket = getFluid().getBucket();
        if (bucket != Items.AIR)
        {
            return new ItemStack(bucket).getHoverName();
        }
        return net.minecraft.network.chat.Component.literal(String.valueOf(
                net.minecraft.core.registries.BuiltInRegistries.FLUID.getKey(getFluid())));
    }

    public FluidStack copy()
    {
        return isEmpty() ? EMPTY : new FluidStack(holder, amount, patch);
    }

    public FluidStack copyWithAmount(int newAmount)
    {
        return isEmpty() ? EMPTY : new FluidStack(holder, newAmount, patch);
    }

    public boolean is(Fluid fluid)
    {
        return getFluid() == fluid;
    }

    public boolean is(FluidStack other)
    {
        return other != null && !isEmpty() && !other.isEmpty() && getFluid() == other.getFluid()
                && patch.equals(other.patch);
    }

    /** 与 NeoForge 一致：只比较「流体 + 组件」，不比数量。 */
    public boolean isComponentsPatchEqual(@Nullable FluidStack other)
    {
        return other != null && getFluid() == other.getFluid() && patch.equals(other.patch);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof FluidStack other)) return false;
        return amount == other.amount && getFluid() == other.getFluid() && patch.equals(other.patch);
    }

    @Override
    public int hashCode()
    {
        return 31 * (31 * getFluid().hashCode() + patch.hashCode()) + amount;
    }

    @Override
    public String toString()
    {
        return isEmpty() ? "FluidStack.EMPTY" : amount + "x" + getFluid();
    }
}