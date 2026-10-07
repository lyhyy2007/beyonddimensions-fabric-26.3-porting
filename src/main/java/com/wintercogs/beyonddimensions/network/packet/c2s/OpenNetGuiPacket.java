package com.wintercogs.beyonddimensions.network.packet.c2s;

import com.wintercogs.beyonddimensions.api.network.BDNetContext;

import com.wintercogs.beyonddimensions.fabric.BDMenuOpener;

import com.wintercogs.beyonddimensions.BeyondDimensions;
import com.wintercogs.beyonddimensions.api.dimensionnet.DimensionsNet;
import com.wintercogs.beyonddimensions.client.gui.NetMenuType;
import com.wintercogs.beyonddimensions.common.component.ItemStackContents;
import com.wintercogs.beyonddimensions.common.init.BDDataComponents;
import com.wintercogs.beyonddimensions.common.item.NetTerminalItem;
import com.wintercogs.beyonddimensions.common.menu.DimensionsCraftMenu;
import com.wintercogs.beyonddimensions.common.menu.DimensionsNetMenu;
import com.wintercogs.beyonddimensions.integration.ModPresence;
import com.wintercogs.beyonddimensions.integration.OtherModIds;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.NonNullList;
import net.minecraft.network.Utf8String;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;


public record OpenNetGuiPacket(String uuid, NetMenuType target) implements CustomPacketPayload
{
    public static final Type<OpenNetGuiPacket> TYPE =
            new Type<>(BeyondDimensions.makeId("open_net_gui_packet"));

    public static final StreamCodec<ByteBuf, OpenNetGuiPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8,
                    OpenNetGuiPacket::uuid,
                    new StreamCodec<ByteBuf, NetMenuType>()
                    {
                        @Override
                        public void encode(ByteBuf buf, NetMenuType netMenuType)
                        {
                            Utf8String.write(buf, netMenuType.toString(), 32000);
                        }

                        @Override
                        public NetMenuType decode(ByteBuf buf)
                        {
                            return NetMenuType.valueOf(Utf8String.read(buf, 32000));
                        }
                    },
                    OpenNetGuiPacket::target,
                    OpenNetGuiPacket::new
            );

    private void handleInClient(final BDNetContext context)
    {

    }

    private void handleInServer(final BDNetContext context)
    {
        //获取玩家上下文
        Player player = context.player();

        DimensionsNet net = DimensionsNet.getNetFromPlayer(player);
        if (net != null)
        {
            NetMenuType targetMenu = this.target();
            if (targetMenu == NetMenuType.NET_CRAFT_MENU)
            {
                BDMenuOpener.open(player, new SimpleMenuProvider(
                        (containerId, playerInventory, _player) -> new DimensionsCraftMenu(DimensionsCraftMenu.Dimensions_Craft_Menu, containerId, playerInventory, net.getUnifiedStorage(), null, null),
                        Component.translatable("menu.title.beyonddimensions.dimensionnetmenu")
                ));
            }
            else if (targetMenu == NetMenuType.NET_MENU)
            {
                BDMenuOpener.open(player, new SimpleMenuProvider(
                        (containerId, playerInventory, _player) -> new DimensionsNetMenu(DimensionsNetMenu.Dimensions_Net_Menu, containerId, playerInventory, net.getUnifiedStorage()),
                        Component.translatable("menu.title.beyonddimensions.dimensionnetmenu")
                ));
            }
            else if (targetMenu == NetMenuType.NET_CRAFT_TERMINAL)
            {
                ItemStack terminalStack = null;
                if (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof NetTerminalItem)
                    terminalStack = player.getItemInHand(InteractionHand.MAIN_HAND);
                else if (player.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof NetTerminalItem)
                    terminalStack = player.getItemInHand(InteractionHand.OFF_HAND);
                else
                {
                    for (ItemStack itemStack : player.getInventory().getNonEquipmentItems())
                    {
                        if (itemStack.getItem() instanceof NetTerminalItem)
                        {
                            terminalStack = itemStack;
                            break;
                        }

                    }
                }

                if (terminalStack != null)
                {
                    if (terminalStack.get(BDDataComponents.CRAFT_SLOTS.get()) == null)
                        terminalStack.set(BDDataComponents.CRAFT_SLOTS.get(), new ItemStackContents(NonNullList.withSize(9, ItemStack.EMPTY)));

                    NetTerminalItem.contextMap.put(player, new NetTerminalItem.MenuTriggerContext(InteractionHand.MAIN_HAND, terminalStack));
                    BDMenuOpener.open(player, (NetTerminalItem) terminalStack.getItem());
                }
            }
        }
    }

    public static void handleServer(final OpenNetGuiPacket packet, final BDNetContext context)
    {
        if (packet == null)
        {
            return;
        }
        context.enqueueWork(() -> packet.handleInServer(context));
    }

    public static void handleClient(final OpenNetGuiPacket packet, final BDNetContext context)
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
