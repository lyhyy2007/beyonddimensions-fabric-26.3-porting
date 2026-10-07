package com.wintercogs.beyonddimensions.network.packet.c2s;

import com.wintercogs.beyonddimensions.api.network.BDNetContext;

import com.wintercogs.beyonddimensions.fabric.BDMenuOpener;

import com.wintercogs.beyonddimensions.BeyondDimensions;
import com.wintercogs.beyonddimensions.api.dimensionnet.DimensionsNet;
import com.wintercogs.beyonddimensions.common.init.BDItems;
import com.wintercogs.beyonddimensions.common.menu.NetMagnetMenu;
import com.wintercogs.beyonddimensions.util.InventoryHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;


public record OpenMagnetGuiPacket() implements CustomPacketPayload
{
    public static final Type<OpenMagnetGuiPacket> TYPE = new Type<>(BeyondDimensions.makeId("open_magnet_gui_packet"));

    public static final StreamCodec<RegistryFriendlyByteBuf, OpenMagnetGuiPacket> STREAM_CODEC = new StreamCodec<>()
    {
        @Override
        public void encode(RegistryFriendlyByteBuf byteBuf, OpenMagnetGuiPacket packet)
        {
        }

        @Override
        public OpenMagnetGuiPacket decode(RegistryFriendlyByteBuf byteBuf)
        {
            return new OpenMagnetGuiPacket();
        }
    };

    private void handleInClient(final BDNetContext context)
    {
    }

    private void handleInServer(final BDNetContext context)
    {
        Player player = context.player();
        DimensionsNet net = DimensionsNet.getNetFromPlayer(player);

        if (net == null)
        {
            return;
        }

        ItemStack itemStack = InventoryHelper.findItemInPlayerInventory(player, BDItems.NET_MAGNET_ITEM.get());
        if (itemStack == null)
        {
            return;
        }

        BDMenuOpener.open(player, new SimpleMenuProvider(
                (containerId, inventory, ignoredPlayer) -> new NetMagnetMenu(containerId, inventory, itemStack),
                Component.translatable("menu.title.beyonddimensions.magnet_menu")
        ));
    }

    public static void handleServer(final OpenMagnetGuiPacket packet, final BDNetContext context)
    {
        if (packet == null)
        {
            return;
        }
        context.enqueueWork(() -> packet.handleInServer(context));
    }

    public static void handleClient(final OpenMagnetGuiPacket packet, final BDNetContext context)
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
