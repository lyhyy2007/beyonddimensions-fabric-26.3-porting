package com.wintercogs.beyonddimensions.client.util;

import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;

/** 客户端 tooltip 标志（原为 NeoForge 的 {@code ClientTooltipFlag}）。 */
public class BDTooltipFlag implements TooltipFlag
{
    private final boolean advanced;
    private final boolean creative;

    public BDTooltipFlag(boolean advanced, boolean creative)
    {
        this.advanced = advanced;
        this.creative = creative;
    }

    public static BDTooltipFlag of(@Nullable TooltipFlag flag)
    {
        return new BDTooltipFlag(flag != null && flag.isAdvanced(), flag != null && flag.isCreative());
    }

    public static BDTooltipFlag of(boolean advanced)
    {
        return new BDTooltipFlag(advanced, false);
    }

    @Override
    public boolean isAdvanced()
    {
        return advanced;
    }

    @Override
    public boolean isCreative()
    {
        return creative;
    }
}