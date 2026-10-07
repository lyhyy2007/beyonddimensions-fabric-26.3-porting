package com.wintercogs.beyonddimensions.fabric;

import com.wintercogs.beyonddimensions.api.network.BDNetContext;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * 模组自有网络层（仅物理客户端加载）：客户端接收器 + 客户端发包。
 * <p>
 * 只在 {@link BDNetworking#playBidirectional} 已确认处于客户端时被引用，服务端不会加载本类。
 */
public final class BDNetworkingClient
{
    private BDNetworkingClient() {}

    public static <T extends CustomPacketPayload> void registerReceiver(CustomPacketPayload.Type<T> type,
                                                                       BDNetworking.PayloadHandler<T> handler)
    {
        ClientPlayNetworking.registerGlobalReceiver(type,
                (payload, context) -> handler.handle(payload, BDNetContext.of(context.player())));
    }

    /** 客户端 -> 服务端。 */
    public static void sendToServer(CustomPacketPayload payload)
    {
        ClientPlayNetworking.send(payload);
    }
}