package com.wintercogs.beyonddimensions.fabric;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.function.Consumer;

/**
 * 打开界面时把额外数据（BlockPos / InteractionHand 等）随「打开界面」包一起发给客户端。
 * <p>
 * 上游直接用 {@code ServerPlayer#openMenu(provider, pos)} 与
 * {@code openMenu(provider, Consumer<FriendlyByteBuf>)} 两个 NeoForge 补丁方法，
 * Fabric 侧改用 {@link ExtendedMenuProvider}，数据和语义保持一致。
 */
public final class BDMenuOpener
{
    private BDMenuOpener() {}

    public static void open(Player player, MenuProvider provider)
    {
        open(player, provider, buf -> {});
    }

    public static void open(Player player, MenuProvider provider, BlockPos pos)
    {
        open(player, provider, buf -> buf.writeBlockPos(pos));
    }

    public static void open(Player player, MenuProvider provider, Consumer<FriendlyByteBuf> writer)
    {
        
        if (!(player instanceof ServerPlayer serverPlayer))
        {
            return;
        }
FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        writer.accept(buffer);
        byte[] data = new byte[buffer.readableBytes()];
        buffer.readBytes(data);

        serverPlayer.openMenu(new ExtendedMenuProvider<byte[]>()
        {
            @Override
            public byte[] getScreenOpeningData(ServerPlayer ignored)
            {
                return data;
            }

            @Override
            public Component getDisplayName()
            {
                return provider.getDisplayName();
            }

            @Override
            public AbstractContainerMenu createMenu(int syncId, Inventory inventory, Player p)
            {
                return provider.createMenu(syncId, inventory, p);
            }
        });
    }
}