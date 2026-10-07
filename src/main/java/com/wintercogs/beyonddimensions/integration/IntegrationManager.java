package com.wintercogs.beyonddimensions.integration;

import net.minecraft.world.item.CreativeModeTab;

/**
 * 集成模块调度器（Fabric 26.3 移植版）。
 * <p>
 * 上游的 AE2 / RS / Botania / Ars / Mek / Curios / JEI / KubeJS 等集成模块依赖各自模组的
 * NeoForge 专有 API，本次移植按「Fabric 核心版」范围整体延后（原件保留在
 * {@code port/_deferred/integration-module/}）。这里保留同名入口，行为为空操作，
 * 以免调用方需要改动。
 */
public final class IntegrationManager
{
    private IntegrationManager()
    {
    }

    public static void bootstrapCommon()
    {
        // 集成模块延后：无模块可引导
    }

    public static void bootstrapClient()
    {
        // 集成模块延后：无模块可引导
    }

    public static void onItemCreativeTabCollect(CreativeModeTab.ItemDisplayParameters displayParameters,
                                                CreativeModeTab.Output output)
    {
        // 集成模块延后：无额外交付物
    }

    public static void onBlockCreativeTabCollect(CreativeModeTab.ItemDisplayParameters displayParameters,
                                                 CreativeModeTab.Output output)
    {
        // 集成模块延后：无额外交付物
    }
}