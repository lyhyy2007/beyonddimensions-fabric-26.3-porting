package com.wintercogs.beyonddimensions.util;

import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;

/** 当前服务端实例（由 Fabric 生命周期回调写入）。 */
public final class BDServerHooks
{
    private BDServerHooks() {}

    private static volatile MinecraftServer currentServer;

    public static void setCurrentServer(@Nullable MinecraftServer server)
    {
        currentServer = server;
    }

    @Nullable
    public static MinecraftServer getCurrentServer()
    {
        return currentServer;
    }
}