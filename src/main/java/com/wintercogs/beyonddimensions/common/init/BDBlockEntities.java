package com.wintercogs.beyonddimensions.common.init;

import com.wintercogs.beyonddimensions.registry.BDHolder;
import com.wintercogs.beyonddimensions.registry.BDRegistry;


import java.util.Set;

import com.wintercogs.beyonddimensions.api.ids.BDBlockIds;
import com.wintercogs.beyonddimensions.api.ids.BDConstants;
import com.wintercogs.beyonddimensions.common.block.entity.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;

public class BDBlockEntities
{    public static final BDHolder<BlockEntityType<NetInterfaceBlockEntity>> NET_INTERFACE_BLOCK_ENTITY =
            BDRegistry.defer(BDRegistry.PHASE_BLOCK_ENTITY, net.minecraft.core.registries.BuiltInRegistries.BLOCK_ENTITY_TYPE,
                    BDBlockIds.NET_INTERFACE,
                    () -> new BlockEntityType<>(NetInterfaceBlockEntity::new, Set.of(BDBlocks.NET_INTERFACE.get()))
            );

    public static final BDHolder<BlockEntityType<NetPathwayBlockEntity>> NET_PATHWAY_BLOCK_ENTITY =
            BDRegistry.defer(BDRegistry.PHASE_BLOCK_ENTITY, net.minecraft.core.registries.BuiltInRegistries.BLOCK_ENTITY_TYPE,
                    BDBlockIds.NET_PATHWAY,
                    () -> new BlockEntityType<>(NetPathwayBlockEntity::new, Set.of(BDBlocks.NET_PATHWAY.get()))
            );


    public static final BDHolder<BlockEntityType<NetEnergyPathwayBlockEntity>> NET_ENERGY_PATHWAY_BLOCK_ENTITY =
            BDRegistry.defer(BDRegistry.PHASE_BLOCK_ENTITY, net.minecraft.core.registries.BuiltInRegistries.BLOCK_ENTITY_TYPE,
                    BDBlockIds.NET_ENERGY_PATHWAY,
                    () -> new BlockEntityType<>(NetEnergyPathwayBlockEntity::new, Set.of(BDBlocks.NET_ENERGY_PATHWAY.get()))
            );

    public static final BDHolder<BlockEntityType<NetTerminalBlockEntity>> NET_TERMINAL_BLOCK_ENTITY =
            BDRegistry.defer(BDRegistry.PHASE_BLOCK_ENTITY, net.minecraft.core.registries.BuiltInRegistries.BLOCK_ENTITY_TYPE,
                    BDBlockIds.NET_TERMINAL_BLOCK,
                    () -> new BlockEntityType<>(NetTerminalBlockEntity::new, Set.of(BDBlocks.NET_TERMINAL_BLOCK.get()))
            );

    public static final BDHolder<BlockEntityType<NetPumpBlockEntity>> NET_PUMP_BLOCK_ENTITY =
            BDRegistry.defer(BDRegistry.PHASE_BLOCK_ENTITY, net.minecraft.core.registries.BuiltInRegistries.BLOCK_ENTITY_TYPE,
                    BDBlockIds.NET_PUMP_BLOCK,
                    () -> new BlockEntityType<>(NetPumpBlockEntity::new, Set.of(BDBlocks.NET_PUMP_BLOCK.get()))
            );

    public static final BDHolder<BlockEntityType<NetHopperBlockEntity>> NET_HOPPER_BLOCK_ENTITY =
            BDRegistry.defer(BDRegistry.PHASE_BLOCK_ENTITY, net.minecraft.core.registries.BuiltInRegistries.BLOCK_ENTITY_TYPE,
                    BDBlockIds.NET_HOPPER_BLOCK,
                    () -> new BlockEntityType<>(NetHopperBlockEntity::new, Set.of(BDBlocks.NET_HOPPER_BLOCK.get()))
            );

    public static final BDHolder<BlockEntityType<NetFurnaceBlockEntity>> NET_FURNACE_BLOCK_ENTITY =
            BDRegistry.defer(BDRegistry.PHASE_BLOCK_ENTITY, net.minecraft.core.registries.BuiltInRegistries.BLOCK_ENTITY_TYPE,
                    BDBlockIds.NET_FURNACE_BLOCK,
                    () -> new BlockEntityType<>(NetFurnaceBlockEntity::new, Set.of(BDBlocks.NET_FURNACE_BLOCK.get()))
            );

    public static void register()
    {
        // 注册由 DeferredRegistry.flush() 统一执行（Fabric 原生侧不再需要事件总线）
    }
}
