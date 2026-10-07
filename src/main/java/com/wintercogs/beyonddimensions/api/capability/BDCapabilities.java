package com.wintercogs.beyonddimensions.api.capability;

import com.wintercogs.beyonddimensions.api.ids.BDConstants;
import com.wintercogs.beyonddimensions.api.transfer.ResourceHandler;
import com.wintercogs.beyonddimensions.api.transfer.access.ItemAccess;
import com.wintercogs.beyonddimensions.api.transfer.energy.EnergyHandler;
import com.wintercogs.beyonddimensions.api.transfer.fluid.FluidResource;
import com.wintercogs.beyonddimensions.api.transfer.item.ItemResource;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.BiFunction;

/**
 * 模组自有能力表（Fabric 原生版）。
 * <p>
 * 原先是 NeoForge 的 {@code Capabilities} + {@code RegisterCapabilitiesEvent}；
 * 现在直接使用 Fabric 官方的 {@link BlockApiLookup} / {@link ItemApiLookup} 作为容器，
 * 键值取自 Fabric lookup 注册表，因此不再需要自建能力注册中心。
 * <p>
 * 实体能力：Fabric 没有实体 lookup，玩家背包由 {@link ItemStorageAdapter} 包装
 * {@code PlayerInventoryStorage} 直接提供。
 */
public final class BDCapabilities
{
    private BDCapabilities() {}

    private static Identifier id(String path)
    {
        return Identifier.fromNamespaceAndPath(BDConstants.MODID, path);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <T> BlockApiLookup<T, Direction> blockLookup(String path, Class<?> clazz)
    {
        return BlockApiLookup.get(id(path), (Class) clazz, Direction.class);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <T> ItemApiLookup<T, ItemAccess> itemLookup(String path, Class<?> clazz)
    {
        return ItemApiLookup.get(id(path), (Class) clazz, ItemAccess.class);
    }

    public static final class Item
    {
        private Item() {}

        public static final BlockApiLookup<ResourceHandler<ItemResource>, Direction> BLOCK =
                blockLookup("item_handler/block", ResourceHandler.class);
        public static final ItemApiLookup<ResourceHandler<ItemResource>, ItemAccess> ITEM =
                itemLookup("item_handler/item", ResourceHandler.class);

        /** 实体物品能力（无方向）。 */
        public static ResourceHandler<ItemResource> ENTITY(Entity entity)
        {
            return BDCapabilityAccess.entityItemHandler(entity, false);
        }

        /** 实体物品能力（自动化视图）。 */
        public static ResourceHandler<ItemResource> ENTITY_AUTOMATION(Entity entity, Direction side)
        {
            return BDCapabilityAccess.entityItemHandler(entity, true);
        }
    }

    public static final class Fluid
    {
        private Fluid() {}

        public static final BlockApiLookup<ResourceHandler<FluidResource>, Direction> BLOCK =
                blockLookup("fluid_handler/block", ResourceHandler.class);
        public static final ItemApiLookup<ResourceHandler<FluidResource>, ItemAccess> ITEM =
                itemLookup("fluid_handler/item", ResourceHandler.class);

        public static ResourceHandler<FluidResource> ENTITY(Entity entity)
        {
            return null;
        }
    }

    public static final class Energy
    {
        private Energy() {}

        public static final BlockApiLookup<EnergyHandler, Direction> BLOCK =
                blockLookup("energy_handler/block", EnergyHandler.class);
        public static final ItemApiLookup<EnergyHandler, ItemAccess> ITEM =
                itemLookup("energy_handler/item", EnergyHandler.class);

        public static EnergyHandler ENTITY(Entity entity)
        {
            return null;
        }
    }

    /** 把提供者登记到方块能力表（供各 BlockEntity 调用）。 */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T extends BlockEntity> void registerBlockEntity(BlockApiLookup<?, Direction> lookup,
                                                                   BlockEntityType<T> type,
                                                                   BiFunction<? super T, Direction, ?> provider)
    {
        ((BlockApiLookup) lookup).registerForBlockEntity((BiFunction) provider, type);
    }

    /** 把提供者登记到物品能力表。 */
    public static <T> void registerItem(ItemApiLookup<T, ItemAccess> lookup,
                                        BiFunction<ItemStack, ItemAccess, T> provider,
                                        net.minecraft.world.item.Item... items)
    {
        lookup.registerForItems(provider::apply, items);
    }
}