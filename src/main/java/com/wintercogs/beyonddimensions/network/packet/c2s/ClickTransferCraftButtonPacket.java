package com.wintercogs.beyonddimensions.network.packet.c2s;

import com.wintercogs.beyonddimensions.api.network.BDNetContext;

import com.wintercogs.beyonddimensions.BeyondDimensions;
import com.wintercogs.beyonddimensions.common.menu.DimensionsCraftMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;


public record ClickTransferCraftButtonPacket(boolean toStorage) implements CustomPacketPayload
{
    public static final Type<ClickTransferCraftButtonPacket> TYPE =
            new Type<>(BeyondDimensions.makeId("click_transfer_craft_button_packet"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClickTransferCraftButtonPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL,
                    ClickTransferCraftButtonPacket::toStorage,
                    ClickTransferCraftButtonPacket::new
            );

    private void handleInClient(final BDNetContext context)
    {

    }

    private void handleInServer(final BDNetContext context)
    {
        Player player = context.player();

        if (player.containerMenu instanceof DimensionsCraftMenu menu)
        {
            menu.cleanCraftSlots(this.toStorage());
        }
    }

    public static void handleServer(final ClickTransferCraftButtonPacket packet, final BDNetContext context)
    {
        if (packet == null)
        {
            return;
        }
        context.enqueueWork(() -> packet.handleInServer(context));
    }

    public static void handleClient(final ClickTransferCraftButtonPacket packet, final BDNetContext context)
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
