package com.wintercogs.beyonddimensions.network.packet.c2s;

import com.wintercogs.beyonddimensions.api.network.BDNetContext;

import com.wintercogs.beyonddimensions.BeyondDimensions;
import com.wintercogs.beyonddimensions.api.storage.key.IStackKey;
import com.wintercogs.beyonddimensions.common.menu.DimensionsCraftMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public record RecipeFillC2SPacket(List<IStackKey<?>> keys, List<Long> amount) implements CustomPacketPayload
{
    public static final Type<RecipeFillC2SPacket> TYPE =
            new Type<>(BeyondDimensions.makeId("recipe_fill_c2s_packet"));

    public static final StreamCodec<RegistryFriendlyByteBuf, RecipeFillC2SPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.collection(
                            ArrayList::new,
                            IStackKey.STREAM_CODEC
                    ),
                    RecipeFillC2SPacket::keys,
                    ByteBufCodecs.collection(
                            ArrayList::new,
                            ByteBufCodecs.VAR_LONG
                    ),
                    RecipeFillC2SPacket::amount,
                    RecipeFillC2SPacket::new
            );

    private void handleInClient(final BDNetContext context)
    {

    }

    private void handleInServer(final BDNetContext context)
    {
        Player player = context.player();

        if (player.containerMenu instanceof DimensionsCraftMenu menu)
        {
            menu.transferRecipe(this.keys(), this.amount());
        }
    }

    public static void handleServer(final RecipeFillC2SPacket packet, final BDNetContext context)
    {
        if (packet == null)
        {
            return;
        }
        context.enqueueWork(() -> packet.handleInServer(context));
    }

    public static void handleClient(final RecipeFillC2SPacket packet, final BDNetContext context)
    {
        if (packet == null)
        {
            return;
        }
        context.enqueueWork(() -> packet.handleInClient(context));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}
