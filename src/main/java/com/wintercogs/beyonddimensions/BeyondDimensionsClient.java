package com.wintercogs.beyonddimensions;

import com.wintercogs.beyonddimensions.client.init.BDFluidModels;
import com.wintercogs.beyonddimensions.client.init.BDScreens;
import com.wintercogs.beyonddimensions.client.init.BDShortKeys;
import com.wintercogs.beyonddimensions.integration.IntegrationManager;

/**
 * 模组客户端逻辑（Fabric 原生版）。
 * <p>
 * 原先由 NeoForge 的 {@code @Mod(dist = CLIENT)} 构造器 + {@code FMLClientSetupEvent} 驱动；
 * 现在由 Fabric 客户端入口点显式调用 {@link #initClient()}。
 */
public class BeyondDimensionsClient
{
    /** 由 {@code BeyondDimensionsFabricClient#onInitializeClient} 调用。 */
    public static void initClient()
    {
        // 菜单界面
        BDScreens.registerScreens();
        // 按键映射
        BDShortKeys.registerKeys();
        // 自定义流体模型（供 FluidStateModelSetMixin 读取）
        BDFluidModels.registerAll();

        IntegrationManager.bootstrapClient();

        BeyondDimensions.LOGGER.info("维度网络初始化完成(客户端)");
    }
}
