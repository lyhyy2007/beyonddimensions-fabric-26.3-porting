package com.wintercogs.beyonddimensions.util;

import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

/**
 * 当前合成玩家（原为 NeoForge 的 {@code CommonHooks.setCraftingPlayer}）。
 * <p>
 * 原版在 Fabric 侧没有这个线程局部量，这里保留模组自有的等价实现。
 */
public final class CraftingPlayerHooks
{
    private CraftingPlayerHooks() {}

    private static final ThreadLocal<Player> CRAFTING_PLAYER = new ThreadLocal<>();

    public static void setCraftingPlayer(@Nullable Player player)
    {
        if (player == null)
        {
            CRAFTING_PLAYER.remove();
        }
        else
        {
            CRAFTING_PLAYER.set(player);
        }
    }

    @Nullable
    public static Player getCraftingPlayer()
    {
        return CRAFTING_PLAYER.get();
    }
}