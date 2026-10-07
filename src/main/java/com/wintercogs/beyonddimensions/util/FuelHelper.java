package com.wintercogs.beyonddimensions.util;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CookingFuel;
import net.minecraft.world.level.Level;

/**
 * 26.3 的燃料值是数据驱动的 {@link CookingFuel} 组件（原来 NeoForge 的
 * {@code ItemStack#getBurnTime(RecipeType, FuelValues)} 已被移除）。
 * <p>
 * 这里读取组件并尝试解析烧炼时间：解析需要战利品上下文，仅常量型供给能直接解析成功，
 * 失败时退回标准烧炼时间 200 tick。功能可用，极端情况下与原版数值可能有差异。
 */
public final class FuelHelper
{
    private FuelHelper() {}

    public static final int STANDARD_BURN_TIME = 200;

    public static boolean isFuel(ItemStack stack)
    {
        return stack != null && !stack.isEmpty() && stack.has(DataComponents.COOKING_FUEL);
    }

    public static int burnTime(ItemStack stack, Level level)
    {
        if (!isFuel(stack))
        {
            return 0;
        }
        CookingFuel fuel = stack.get(DataComponents.COOKING_FUEL);
        if (fuel == null)
        {
            return 0;
        }
        try
        {
            return Math.max(0, fuel.burnTime().get(null, STANDARD_BURN_TIME));
        }
        catch (Throwable ignored)
        {
            return STANDARD_BURN_TIME;
        }
    }
}