package com.wintercogs.beyonddimensions.network.packet.c2s;

import com.wintercogs.beyonddimensions.api.network.BDNetContext;

import com.wintercogs.beyonddimensions.BeyondDimensions;
import com.wintercogs.beyonddimensions.api.dimensionnet.DimensionsNet;
import com.wintercogs.beyonddimensions.api.dimensionnet.UnifiedStorage;
import com.wintercogs.beyonddimensions.api.storage.key.IStackKey;
import com.wintercogs.beyonddimensions.api.storage.key.KeyAmount;
import com.wintercogs.beyonddimensions.api.storage.key.impl.ItemStackKey;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;


public record PickBlockFromNetPacket(ItemStack targetStack) implements CustomPacketPayload
{
    public static final Type<PickBlockFromNetPacket> TYPE =
            new Type<>(BeyondDimensions.makeId("pick_block_from_net_packet"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PickBlockFromNetPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ItemStack.OPTIONAL_STREAM_CODEC,
                    PickBlockFromNetPacket::targetStack,
                    PickBlockFromNetPacket::new
            );

    private void handleInClient(final BDNetContext context)
    {

    }

    private void handleInServer(final BDNetContext context)
    {
        Player player = context.player();
        if (!player.getMainHandItem().isEmpty()) return;
        DimensionsNet net = DimensionsNet.getNetFromPlayer(player);
        if (net == null) return;
        UnifiedStorage storage = net.getUnifiedStorage();

        IStackKey<?> target = null;
        for (KeyAmount stack : storage.getStorage())
        {
            if (stack.key() instanceof ItemStackKey itemStackKey)
            {
                if (itemStackKey.getSource() == this.targetStack().getItem())
                {
                    target = itemStackKey;
                    break;
                }
            }
        }

        if (target != null && player.getMainHandItem().isEmpty())
        {
            ItemStack extract = (ItemStack) storage.extract(target, target.getVanillaMaxStackSize(), false, false).toStack();
            player.setItemInHand(InteractionHand.MAIN_HAND, extract);
        }
    }

    public static void handleServer(final PickBlockFromNetPacket packet, final BDNetContext context)
    {
        if (packet == null)
        {
            return;
        }
        context.enqueueWork(() -> packet.handleInServer(context));
    }

    public static void handleClient(final PickBlockFromNetPacket packet, final BDNetContext context)
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
