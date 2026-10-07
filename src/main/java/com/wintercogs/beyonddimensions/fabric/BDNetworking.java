package com.wintercogs.beyonddimensions.fabric;

import com.wintercogs.beyonddimensions.api.network.BDNetContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

/**
 * 模组自有网络层（Fabric 原生）：注册载荷 + 服务端发包。
 * <p>
 * 双向载荷同时注册到 {@code serverboundPlay} 与 {@code clientboundPlay}，
 * 并按端分别挂接收器（客户端接收器只在物理客户端加载）。
 */
public final class BDNetworking
{
    private BDNetworking() {}

    @FunctionalInterface
    public interface PayloadHandler<T>
    {
        void handle(T payload, BDNetContext context);
    }

    public static <T extends CustomPacketPayload> void playBidirectional(
            CustomPacketPayload.Type<T> type,
            StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
            PayloadHandler<T> serverHandler,
            PayloadHandler<T> clientHandler)
    {
        PayloadTypeRegistry.serverboundPlay().register(type, codec);
        PayloadTypeRegistry.clientboundPlay().register(type, codec);

        ServerPlayNetworking.registerGlobalReceiver(type,
                (payload, context) -> serverHandler.handle(payload, BDNetContext.of(context.player())));

        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
        {
            BDNetworkingClient.registerReceiver(type, clientHandler);
        }
    }

    /** 服务端 -> 单个玩家。 */
    public static void sendToPlayer(ServerPlayer player, CustomPacketPayload payload)
    {
        if (player != null && player.connection != null)
        {
            ServerPlayNetworking.send(player, payload);
        }
    }
}