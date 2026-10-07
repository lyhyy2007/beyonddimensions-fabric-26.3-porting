package com.wintercogs.beyonddimensions.network.packet.c2s;

import com.wintercogs.beyonddimensions.api.network.BDNetContext;

import com.wintercogs.beyonddimensions.BeyondDimensions;
import com.wintercogs.beyonddimensions.api.dimensionnet.NetControlAction;
import com.wintercogs.beyonddimensions.common.menu.NetControlMenu;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.Utf8String;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;


import java.util.UUID;

public record NetControlActionPacket(UUID receiver, NetControlAction action) implements CustomPacketPayload
{
    public static final StreamCodec<ByteBuf, NetControlAction> NET_CONTROL_ACTION_STREAM_CODEC = netControlActionStreamCodec();

    public static final Type<NetControlActionPacket> TYPE =
            new Type<>(BeyondDimensions.makeId("net_control_action_packet"));

    public static final StreamCodec<ByteBuf, NetControlActionPacket> STREAM_CODEC =
            StreamCodec.composite(
                    UUIDUtil.STREAM_CODEC,
                    NetControlActionPacket::receiver,
                    NET_CONTROL_ACTION_STREAM_CODEC,
                    NetControlActionPacket::action,
                    NetControlActionPacket::new
            );

    private void handleInClient(final BDNetContext context)
    {

    }

    private void handleInServer(final BDNetContext context)
    {
        Player player = context.player();
        NetControlMenu menu;
        if (!(player.containerMenu instanceof NetControlMenu))
        {
            return; // 当服务器接受到包时，如果玩家打开的不是DimensionsNetMenu，不予理会
        }
        menu = (NetControlMenu) player.containerMenu;
        menu.handlePlayerAction(this.receiver(), this.action());
    }

    public static void handleServer(final NetControlActionPacket packet, final BDNetContext context)
    {
        if (packet == null)
        {
            return;
        }
        context.enqueueWork(() -> packet.handleInServer(context));
    }

    public static void handleClient(final NetControlActionPacket packet, final BDNetContext context)
    {
        if (packet == null)
        {
            return;
        }
        context.enqueueWork(() -> packet.handleInClient(context));
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    static StreamCodec<ByteBuf, NetControlAction> netControlActionStreamCodec()
    {
        return new StreamCodec<ByteBuf, NetControlAction>()
        {
            public NetControlAction decode(ByteBuf buf)
            {
                return NetControlAction.valueOf(Utf8String.read(buf, 32000));
            }

            public void encode(ByteBuf buf, NetControlAction permissionlevel)
            {
                Utf8String.write(buf, permissionlevel.toString(), 32000);
            }
        };
    }

}
