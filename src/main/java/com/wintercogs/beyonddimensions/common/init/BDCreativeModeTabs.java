package com.wintercogs.beyonddimensions.common.init;

import com.wintercogs.beyonddimensions.registry.BDHolder;
import com.wintercogs.beyonddimensions.registry.BDRegistry;

import com.wintercogs.beyonddimensions.api.ids.BDConstants;
import com.wintercogs.beyonddimensions.integration.IntegrationManager;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class BDCreativeModeTabs
{
    public static final BDHolder<CreativeModeTab> BEYOND_DIMENSIONS_ITEMS_TAB = BDRegistry.defer(BDRegistry.PHASE_TAB, net.minecraft.core.registries.BuiltInRegistries.CREATIVE_MODE_TAB,
            "beyond_dimensions_items_tab",
            () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
                    .icon(() -> new ItemStack(BDItems.NET_CREATER.get()))
                    .title(Component.translatable("creativetab.beyonddimensions.items"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(BDItems.NET_CREATER);
                        output.accept(BDItems.NET_MEMBER_INVITER);
                        output.accept(BDItems.NET_MANAGER_INVITER);
                        output.accept(BDItems.UNSTABLE_SPACE_TIME_FRAGMENT);
                        output.accept(BDItems.STABLE_SPACE_TIME_FRAGMENT);
                        output.accept(BDItems.SPACE_TIME_STABLE_FRAME);
                        output.accept(BDItems.SHATTERED_SPACE_TIME_CRYSTALLIZATION);
                        output.accept(BDItems.SPACE_TIME_BAR);
                        output.accept(BDItems.TEST_ITEM_GENERATE);
                        output.accept(BDItems.NET_TERMINAL_ITEM);
                        output.accept(BDItems.NET_GIFTER);
                        output.accept(BDItems.NET_DESTROYER);
                        output.accept(BDItems.MATTER_COMPRESS_BALL);
                        output.accept(BDItems.NET_MAGNET_ITEM);
                        output.accept(BDItems.NET_FEEDER_ITEM);
                        output.accept(BDItems.NET_RESTOCKER_ITEM);
                        output.accept(BDItems.XP_EXCHANGE_ITEM);

                        for (BDFluids.FluidEntry e : BDFluids.ALL)
                        { //注册所有桶
                            output.accept(e.bucket().get());
                        }

                        IntegrationManager.onItemCreativeTabCollect(itemDisplayParameters, output);
                    })
                    .build());

    public static final BDHolder<CreativeModeTab> BEYOND_DIMENSIONS_BLOCKS_TAB = BDRegistry.defer(BDRegistry.PHASE_TAB, net.minecraft.core.registries.BuiltInRegistries.CREATIVE_MODE_TAB,
            "beyond_dimensions_blocks_tab",
            () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
                    .icon(() -> new ItemStack(BDBlocks.NET_CONTROL))
                    .title(Component.translatable("creativetab.beyonddimensions.blocks"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(BDBlocks.NET_CONTROL);
                        output.accept(BDBlocks.NET_INTERFACE);
                        output.accept(BDBlocks.NET_PATHWAY);
                        output.accept(BDBlocks.NET_ENERGY_PATHWAY);
                        output.accept(BDBlocks.NET_TERMINAL_BLOCK);
                        output.accept(BDBlocks.NET_PUMP_BLOCK);
                        output.accept(BDBlocks.NET_HOPPER_BLOCK);
                        output.accept(BDBlocks.NET_FURNACE_BLOCK);
                        output.accept(BDBlocks.DIMENSIONAL_CONNECT_BLOCK);

                        IntegrationManager.onBlockCreativeTabCollect(itemDisplayParameters, output);
                    })
                    .build());


    public static void register()
    {
        // 注册由 DeferredRegistry.flush() 统一执行（Fabric 原生侧不再需要事件总线）
    }
}
