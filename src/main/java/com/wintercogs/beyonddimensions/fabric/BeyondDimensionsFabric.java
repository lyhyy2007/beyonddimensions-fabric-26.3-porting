package com.wintercogs.beyonddimensions.fabric;

import com.wintercogs.beyonddimensions.BeyondDimensions;
import com.wintercogs.beyonddimensions.api.dimensionnet.NetRegistryIndex;
import com.wintercogs.beyonddimensions.api.dimensionnet.PlayerNetIndex;
import com.wintercogs.beyonddimensions.common.block.entity.NetEnergyPathwayBlockEntity;
import com.wintercogs.beyonddimensions.common.block.entity.NetFurnaceBlockEntity;
import com.wintercogs.beyonddimensions.common.block.entity.NetInterfaceBlockEntity;
import com.wintercogs.beyonddimensions.common.block.entity.NetPathwayBlockEntity;
import com.wintercogs.beyonddimensions.common.command.ServerCommands;
import com.wintercogs.beyonddimensions.common.init.BDPackets;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import com.wintercogs.beyonddimensions.util.BDServerHooks;

/**
 * Fabric 主入口点（原生版）。
 * <p>
 * 不再有事件总线：注册/初始化按上游阶段顺序直接调用，游戏侧钩子直接挂在 Fabric 回调上。
 * 目前仍复用的垫片仅剩「能力 / 数据包 / 服务端钩子」三处，后续继续原生化。
 */
public class BeyondDimensionsFabric implements ModInitializer
{
    @Override
    public void onInitialize()
    {
        // 配置 -> 注册 -> flush -> 通用初始化
        BeyondDimensions.init();
        NetInterfaceBlockEntity.registerCapability();
        NetPathwayBlockEntity.registerCapability();
        NetEnergyPathwayBlockEntity.registerCapability();
        NetFurnaceBlockEntity.registerCapability();

        // 数据包
        BDPackets.register();

        // 服务端生命周期
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            BDServerHooks.setCurrentServer(server);
            BeyondDimensions.onServerStarting(server);
        });

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            BDServerHooks.setCurrentServer(server);
            NetRegistryIndex.onServerStarted(server);
            PlayerNetIndex.onServerStarted(server);
        });

        ServerLifecycleEvents.SERVER_STOPPED.register(server -> BDServerHooks.setCurrentServer(null));

        // 命令
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                ServerCommands.onRegisterCommands(dispatcher));
    }
}
