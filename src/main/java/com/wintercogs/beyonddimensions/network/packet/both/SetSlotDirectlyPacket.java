package com.wintercogs.beyonddimensions.network.packet.both;

import com.wintercogs.beyonddimensions.api.network.BDNetContext;

import com.wintercogs.beyonddimensions.BeyondDimensions;
import com.wintercogs.beyonddimensions.api.storage.key.KeyAmount;
import com.wintercogs.beyonddimensions.common.menu.widget.slot.AbstractStackTypedSlot;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;


// 此记录的处理会调用对应slot的setStackDirectly
// 不会有数据校验
// 因此，请仅在绝对需要setStackDirectly再重写实现（如标记槽位）
public record SetSlotDirectlyPacket(int slotId, KeyAmount stack) implements CustomPacketPayload
{
    public static final Type<SetSlotDirectlyPacket> TYPE =
            new Type<>(BeyondDimensions.makeId("set_slot_directly_packet"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SetSlotDirectlyPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT,
                    SetSlotDirectlyPacket::slotId,
                    KeyAmount.STREAM_CODEC,
                    SetSlotDirectlyPacket::stack,
                    SetSlotDirectlyPacket::new
            );

    private void handleInClient(final BDNetContext context)
    {
        Player player = context.player();
        if (player.containerMenu instanceof AbstractContainerMenu menu)
        {
            if (menu.slots.get(this.slotId()) instanceof AbstractStackTypedSlot slot)
            {
                slot.setStackDirectly(this.stack().key(), this.stack().amount());
            }
        }
    }

    private void handleInServer(final BDNetContext context)
    {
        Player player = context.player();
        if (player.containerMenu instanceof AbstractContainerMenu menu)
        {
            if (menu.slots.get(this.slotId()) instanceof AbstractStackTypedSlot slot)
            {
                slot.setStackDirectly(this.stack().key(), this.stack().amount());
            }
        }
    }

    public static void handleServer(final SetSlotDirectlyPacket packet, final BDNetContext context)
    {
        if (packet == null)
        {
            return;
        }
        context.enqueueWork(() -> packet.handleInServer(context));
    }

    public static void handleClient(final SetSlotDirectlyPacket packet, final BDNetContext context)
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
