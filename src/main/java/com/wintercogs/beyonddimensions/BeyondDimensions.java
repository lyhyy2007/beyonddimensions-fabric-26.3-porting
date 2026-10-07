package com.wintercogs.beyonddimensions;

import com.mojang.logging.LogUtils;
import com.wintercogs.beyonddimensions.api.capability.helper.CapabilityHelper;
import com.wintercogs.beyonddimensions.api.capability.helper.ordered.EnergyStackTypedHandler;
import com.wintercogs.beyonddimensions.api.capability.helper.ordered.FluidStackTypedHandler;
import com.wintercogs.beyonddimensions.api.capability.helper.ordered.ItemStackTypedHandler;
import com.wintercogs.beyonddimensions.api.capability.helper.unordered.EnergyUnifiedStorageHandler;
import com.wintercogs.beyonddimensions.api.capability.helper.unordered.FluidUnifiedStorageHandler;
import com.wintercogs.beyonddimensions.api.capability.helper.unordered.ItemUnifiedStorageHandler;
import com.wintercogs.beyonddimensions.api.capability.helper.wrapper.EnergyHandlerWrapper;
import com.wintercogs.beyonddimensions.api.capability.helper.wrapper.FluidHandlerWrapper;
import com.wintercogs.beyonddimensions.api.capability.helper.wrapper.ItemHandlerWrapper;
import com.wintercogs.beyonddimensions.api.capability.helper.wrapper.StackHandlerWrapperHelper;
import com.wintercogs.beyonddimensions.api.ids.BDConstants;
import com.wintercogs.beyonddimensions.api.storage.key.StackKeyRegistry;
import com.wintercogs.beyonddimensions.api.storage.key.impl.EmptyStackKey;
import com.wintercogs.beyonddimensions.api.storage.key.impl.EnergyStackKey;
import com.wintercogs.beyonddimensions.api.storage.key.impl.FluidStackKey;
import com.wintercogs.beyonddimensions.api.storage.key.impl.ItemStackKey;
import com.wintercogs.beyonddimensions.common.init.BDBlockEntities;
import com.wintercogs.beyonddimensions.common.init.BDBlocks;
import com.wintercogs.beyonddimensions.common.init.BDCreativeModeTabs;
import com.wintercogs.beyonddimensions.common.init.BDDataComponents;
import com.wintercogs.beyonddimensions.common.init.BDFluids;
import com.wintercogs.beyonddimensions.common.init.BDItems;
import com.wintercogs.beyonddimensions.common.init.BDMenus;
import com.wintercogs.beyonddimensions.integration.IntegrationManager;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import com.wintercogs.beyonddimensions.api.capability.BDCapabilities;
import com.wintercogs.beyonddimensions.registry.BDRegistry;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

/**
 * 模组主逻辑（Fabric 原生版）。
 * <p>
 * 原先由 NeoForge 通过 {@code @Mod} 构造器 + 事件总线驱动；现在改为由 Fabric 主入口点显式调用
 * {@link #init()}，步骤顺序与上游一致：配置 → 排队注册 → 统一 flush → 通用初始化。
 */
public class BeyondDimensions
{
    public static final Logger LOGGER = LogUtils.getLogger();

    /** 由 {@code BeyondDimensionsFabric#onInitialize} 调用。 */
    public static void init()
    {
        Config.register();

        // 注册菜单
        BDMenus.register();
        // 注册创造模式菜单
        BDCreativeModeTabs.register();
        // 注册物品组件
        BDDataComponents.register();
        // 注册物品
        BDItems.register();
        // 注册方块
        BDBlocks.register();
        // 注册流体
        BDFluids.register();
        // 注册方块实体
        BDBlockEntities.register();

        // 两阶段注册：先按依赖顺序实例化，再写入注册表
        BDRegistry.flush();

        // 通用初始化（堆叠类型 / 能力映射 / 包装表）
        commonSetup();

        // 分发集成模块（当前为空实现）
        IntegrationManager.bootstrapCommon();
    }

    private static void commonSetup()
    {
        // 注册堆叠类型，使得网络能够存储相关堆叠
        StackKeyRegistry.registerType(EmptyStackKey.INSTANCE); // 全空堆叠，用于避免使用null
        StackKeyRegistry.registerType(ItemStackKey.EMPTY);
        StackKeyRegistry.registerType(FluidStackKey.EMPTY);
        StackKeyRegistry.registerType(EnergyStackKey.INSTANCE);

        // 注册方块能力类型，用于动态为方块注册能力
        CapabilityHelper.BlockCapabilityMap.put(ItemStackKey.ID, BDCapabilities.Item.BLOCK);
        CapabilityHelper.BlockCapabilityMap.put(FluidStackKey.ID, BDCapabilities.Fluid.BLOCK);
        CapabilityHelper.BlockCapabilityMap.put(EnergyStackKey.ID, BDCapabilities.Energy.BLOCK);
        // 注册物品能力，用于动态操作
        CapabilityHelper.ItemCapabilityMap.put(ItemStackKey.ID, BDCapabilities.Item.ITEM);
        CapabilityHelper.ItemCapabilityMap.put(FluidStackKey.ID, BDCapabilities.Fluid.ITEM);
        CapabilityHelper.ItemCapabilityMap.put(EnergyStackKey.ID, BDCapabilities.Energy.ITEM);

        // 注册网络能力，使得网络通道能暴露对应存储能力 注:能量存储无需注册，单独实现
        CapabilityHelper.registerUSHandler(ItemStackKey.EMPTY, ItemUnifiedStorageHandler::new);
        CapabilityHelper.registerUSHandler(FluidStackKey.EMPTY, FluidUnifiedStorageHandler::new);
        CapabilityHelper.registerUSHandler(EnergyStackKey.INSTANCE, EnergyUnifiedStorageHandler::new);

        // 注册存储分化包装
        CapabilityHelper.registerStackTypedHandler(ItemStackKey.EMPTY, ItemStackTypedHandler::new);
        CapabilityHelper.registerStackTypedHandler(FluidStackKey.EMPTY, FluidStackTypedHandler::new);
        CapabilityHelper.registerStackTypedHandler(EnergyStackKey.INSTANCE, EnergyStackTypedHandler::new);

        // 注册堆叠处理包装，用于动态包装来自其他模组的handler (如原版的IItemHandler)
        StackHandlerWrapperHelper.stackWrappers.put(ItemStackKey.ID, ItemHandlerWrapper::new);
        StackHandlerWrapperHelper.stackWrappers.put(FluidStackKey.ID, FluidHandlerWrapper::new);
        StackHandlerWrapperHelper.stackWrappers.put(EnergyStackKey.ID, EnergyHandlerWrapper::new);
    }

    /** 由 Fabric 的 SERVER_STARTING 回调直接调用。 */
    public static void onServerStarting(MinecraftServer server)
    {
        LOGGER.info("维度网络初始化完成(服务端)");
    }

    public static Identifier makeId(@NotNull String path)
    {
        return Identifier.fromNamespaceAndPath(BDConstants.MODID, path);
    }
}
