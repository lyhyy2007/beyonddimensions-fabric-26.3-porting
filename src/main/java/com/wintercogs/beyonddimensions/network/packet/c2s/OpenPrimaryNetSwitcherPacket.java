package com.wintercogs.beyonddimensions.network.packet.c2s;

import com.wintercogs.beyonddimensions.api.network.BDNetContext;

import com.wintercogs.beyonddimensions.fabric.BDMenuOpener;

import com.wintercogs.beyonddimensions.BeyondDimensions;
import com.wintercogs.beyonddimensions.common.menu.PrimaryNetSwitcherMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;


public record OpenPrimaryNetSwitcherPacket() implements CustomPacketPayload
{
    public static final Type<OpenPrimaryNetSwitcherPacket> TYPE = new Type<>(BeyondDimensions.makeId("open_primary_net_switcher_packet"));

    public static final StreamCodec<RegistryFriendlyByteBuf, OpenPrimaryNetSwitcherPacket> STREAM_CODEC = new StreamCodec<>()
    {
        @Override
        public void encode(RegistryFriendlyByteBuf byteBuf, OpenPrimaryNetSwitcherPacket packet)
        {
        }

        @Override
        public OpenPrimaryNetSwitcherPacket decode(RegistryFriendlyByteBuf byteBuf)
        {
            return new OpenPrimaryNetSwitcherPacket();
        }
    };

    private void handleInClient(final BDNetContext context)
    {
    }

    private void handleInServer(final BDNetContext context)
    {
        Player player = context.player();
        BDMenuOpener.open(player, new SimpleMenuProvider(
                (containerId, playerInventory, ignoredPlayer) -> new PrimaryNetSwitcherMenu(containerId, playerInventory),
                Component.translatable("menu.title.beyonddimensions.primary_net_switcher")
        ));
    }

    public static void handleServer(final OpenPrimaryNetSwitcherPacket packet, final BDNetContext context)
    {
        if (packet == null)
        {
            return;
        }
        context.enqueueWork(() -> packet.handleInServer(context));
    }

    public static void handleClient(final OpenPrimaryNetSwitcherPacket packet, final BDNetContext context)
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
}
