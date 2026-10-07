package com.wintercogs.beyonddimensions.common.init;

import com.wintercogs.beyonddimensions.registry.BDBlockHolder;
import com.wintercogs.beyonddimensions.registry.BDRegistry;


import com.wintercogs.beyonddimensions.api.ids.BDBlockIds;
import com.wintercogs.beyonddimensions.api.ids.BDConstants;
import com.wintercogs.beyonddimensions.common.block.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class BDBlocks
{
    public static final BDBlockHolder<Block> NET_CONTROL = registerBlock(BDBlockIds.NET_CONTROL,
            NetControlBlock::new,
            BlockBehaviour.Properties.of().strength(4f));

    public static final BDBlockHolder<Block> NET_INTERFACE = registerBlock(BDBlockIds.NET_INTERFACE,
            NetInterfaceBlock::new,
            BlockBehaviour.Properties.of().strength(2f));

    public static final BDBlockHolder<Block> NET_PATHWAY = registerBlock(BDBlockIds.NET_PATHWAY,
            NetPathwayBlock::new,
            BlockBehaviour.Properties.of().strength(2f));

    public static final BDBlockHolder<Block> NET_ENERGY_PATHWAY = registerBlock(BDBlockIds.NET_ENERGY_PATHWAY,
            NetEnergyPathwayBlock::new,
            BlockBehaviour.Properties.of().strength(2f));

    public static final BDBlockHolder<Block> NET_TERMINAL_BLOCK = registerBlock(BDBlockIds.NET_TERMINAL_BLOCK,
            NetTerminalBlock::new,
            BlockBehaviour.Properties.of().strength(2f));

    public static final BDBlockHolder<Block> NET_PUMP_BLOCK = registerBlock(BDBlockIds.NET_PUMP_BLOCK,
            NetPumpBlock::new,
            BlockBehaviour.Properties.of().strength(2f));

    public static final BDBlockHolder<Block> NET_HOPPER_BLOCK = registerBlock(BDBlockIds.NET_HOPPER_BLOCK,
            NetHopperBlock::new,
            BlockBehaviour.Properties.of().strength(2f));

    public static final BDBlockHolder<Block> NET_FURNACE_BLOCK = registerBlock(BDBlockIds.NET_FURNACE_BLOCK,
            NetFurnaceBlock::new,
            BlockBehaviour.Properties.of().strength(2f));

    // 合成材料-维度链接框架
    public static final BDBlockHolder<Block> DIMENSIONAL_CONNECT_BLOCK = registerBlock(BDBlockIds.DIMENSIONAL_CONNECT_BLOCK,
            Block::new,
            BlockBehaviour.Properties.of().strength(2f));


    private static <T extends Block> BDBlockHolder<T> registerBlock(String name, Function<BlockBehaviour.Properties, T> blockFactory, BlockBehaviour.Properties properties)
    {
        BDBlockHolder<T> toReturn = BDRegistry.deferBlock(name, blockFactory, () -> properties);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, BDBlockHolder<T> block)
    {
        BDRegistry.deferBlockItem(name, block);
    }

    public static void register()
    {
        // 注册由 DeferredRegistry.flush() 统一执行（Fabric 原生侧不再需要事件总线）
    }
}
