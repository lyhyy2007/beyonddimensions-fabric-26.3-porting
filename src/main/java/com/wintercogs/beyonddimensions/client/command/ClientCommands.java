package com.wintercogs.beyonddimensions.client.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.wintercogs.beyonddimensions.api.ids.BDConstants;
import com.wintercogs.beyonddimensions.util.TooltipHelper;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.network.chat.Component;
import net.fabricmc.api.EnvType;

/**
 * 客户端调试命令。
 * <p>
 * 26.3 的 Fabric 客户端命令源是 {@link FabricClientCommandSource}（不是 NeoForge 的
 * {@code CommandSourceStack}），因此这里额外提供 Fabric 侧注册入口。
 */
public final class ClientCommands
{

    /** Fabric 客户端命令注册入口。 */
    public static void registerFabric(CommandDispatcher<FabricClientCommandSource> dispatcher)
    {
        dispatcher.register(
                net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal("bdtools")
                        .then(net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal("searchCache")
                                .then(net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal("clear")
                                        .executes(context -> {
                                            TooltipHelper.clearCache();
                                            context.getSource().sendFeedback(
                                                    Component.literal("Tooltip cache cleared."));
                                            return Command.SINGLE_SUCCESS;
                                        }))));
    }
}
