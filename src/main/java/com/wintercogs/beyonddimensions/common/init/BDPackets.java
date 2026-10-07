package com.wintercogs.beyonddimensions.common.init;

import com.wintercogs.beyonddimensions.fabric.BDNetworking;

import com.wintercogs.beyonddimensions.api.ids.BDConstants;
import com.wintercogs.beyonddimensions.network.packet.both.QuickDataTagPacket;
import com.wintercogs.beyonddimensions.network.packet.both.SetSlotDirectlyPacket;
import com.wintercogs.beyonddimensions.network.packet.c2s.*;
import com.wintercogs.beyonddimensions.network.packet.s2c.DisorderedSlotGroupSyncPacket;
import com.wintercogs.beyonddimensions.network.packet.s2c.OrderedStackTypedSlotPacket;
import com.wintercogs.beyonddimensions.network.packet.s2c.PlayerPermissionInfoPacket;
public class BDPackets
{
    public static void register()
    {
        BDNetworking.playBidirectional(OpenNetGuiPacket.TYPE, OpenNetGuiPacket.STREAM_CODEC, OpenNetGuiPacket::handleServer, OpenNetGuiPacket::handleClient);
        BDNetworking.playBidirectional(CallSeverClickPacket.TYPE, CallSeverClickPacket.STREAM_CODEC, CallSeverClickPacket::handleServer, CallSeverClickPacket::handleClient);
        BDNetworking.playBidirectional(PlayerPermissionInfoPacket.TYPE, PlayerPermissionInfoPacket.STREAM_CODEC, PlayerPermissionInfoPacket::handleServer, PlayerPermissionInfoPacket::handleClient);
        BDNetworking.playBidirectional(NetControlActionPacket.TYPE, NetControlActionPacket.STREAM_CODEC, NetControlActionPacket::handleServer, NetControlActionPacket::handleClient);
        BDNetworking.playBidirectional(RecipeFillC2SPacket.TYPE, RecipeFillC2SPacket.STREAM_CODEC, RecipeFillC2SPacket::handleServer, RecipeFillC2SPacket::handleClient);
        BDNetworking.playBidirectional(ClickTransferCraftButtonPacket.TYPE, ClickTransferCraftButtonPacket.STREAM_CODEC, ClickTransferCraftButtonPacket::handleServer, ClickTransferCraftButtonPacket::handleClient);
        BDNetworking.playBidirectional(BatchTransferPacket.TYPE, BatchTransferPacket.STREAM_CODEC, BatchTransferPacket::handleServer, BatchTransferPacket::handleClient);
        BDNetworking.playBidirectional(PickBlockFromNetPacket.TYPE, PickBlockFromNetPacket.STREAM_CODEC, PickBlockFromNetPacket::handleServer, PickBlockFromNetPacket::handleClient);
        BDNetworking.playBidirectional(PutHandItemToNetPacket.TYPE, PutHandItemToNetPacket.STREAM_CODEC, PutHandItemToNetPacket::handleServer, PutHandItemToNetPacket::handleClient);
        BDNetworking.playBidirectional(OrderedStackTypedSlotPacket.TYPE, OrderedStackTypedSlotPacket.STREAM_CODEC, OrderedStackTypedSlotPacket::handleServer, OrderedStackTypedSlotPacket::handleClient);
        BDNetworking.playBidirectional(SetSlotDirectlyPacket.TYPE, SetSlotDirectlyPacket.STREAM_CODEC, SetSlotDirectlyPacket::handleServer, SetSlotDirectlyPacket::handleClient);
        BDNetworking.playBidirectional(DisorderedSlotGroupSyncPacket.TYPE, DisorderedSlotGroupSyncPacket.STREAM_CODEC, DisorderedSlotGroupSyncPacket::handleServer, DisorderedSlotGroupSyncPacket::handleClient);
        BDNetworking.playBidirectional(QuickDataTagPacket.TYPE, QuickDataTagPacket.STREAM_CODEC, QuickDataTagPacket::handleServer, QuickDataTagPacket::handleClient);
        BDNetworking.playBidirectional(ToggleMagnetPacket.TYPE, ToggleMagnetPacket.STREAM_CODEC, ToggleMagnetPacket::handleServer, ToggleMagnetPacket::handleClient);
        BDNetworking.playBidirectional(OpenMagnetGuiPacket.TYPE, OpenMagnetGuiPacket.STREAM_CODEC, OpenMagnetGuiPacket::handleServer, OpenMagnetGuiPacket::handleClient);
        BDNetworking.playBidirectional(OpenPrimaryNetSwitcherPacket.TYPE, OpenPrimaryNetSwitcherPacket.STREAM_CODEC, OpenPrimaryNetSwitcherPacket::handleServer, OpenPrimaryNetSwitcherPacket::handleClient);
        BDNetworking.playBidirectional(PrimaryNetSwitchActionPacket.TYPE, PrimaryNetSwitchActionPacket.STREAM_CODEC, PrimaryNetSwitchActionPacket::handleServer, PrimaryNetSwitchActionPacket::handleClient);
    }
}
