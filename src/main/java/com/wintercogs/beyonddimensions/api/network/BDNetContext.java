package com.wintercogs.beyonddimensions.api.network;

import net.minecraft.world.entity.player.Player;

/**
 * 模组自有的数据包处理上下文（替代之前垫片的 {@code IPayloadContext}）。
 * <p>
 * Fabric 的收发回调本身就在主线程执行，因此 {@link #enqueueWork(Runnable)} 直接运行。
 */
public interface BDNetContext
{
    Player player();

    default void enqueueWork(Runnable runnable)
    {
        runnable.run();
    }

    static BDNetContext of(Player player)
    {
        return () -> player;
    }
}