package com.wintercogs.beyonddimensions.api.capability;

import com.wintercogs.beyonddimensions.api.transfer.ResourceHandler;
import com.wintercogs.beyonddimensions.api.transfer.access.ItemAccess;
import com.wintercogs.beyonddimensions.api.transfer.fluid.FluidResource;
import com.wintercogs.beyonddimensions.api.transfer.item.ItemResource;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

/**
 * 模组自有能力查询入口（Fabric 原生版）。
 * <p>
 * 解析顺序：模组自身登记的能力（Fabric {@code BlockApiLookup}/{@code ItemApiLookup}）
 * → Fabric 官方 {@code ItemStorage}/{@code FluidStorage}（跨模组互操作）。
 */
public final class BDCapabilityAccess
{
    private BDCapabilityAccess() {}

    /**
     * 上游用于在方块状态变化后让能力缓存失效。
     * Fabric 侧能力是即时查询，没有需要失效的缓存，故为空操作。
     */
    public static void invalidateCapabilities(Level level, BlockPos pos)
    {
        // no-op
    }

    /** 同上，无参版本。 */
    public static void invalidateCapabilities()
    {
        // no-op
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static <T> T getBlock(BlockApiLookup<T, Direction> lookup, Level level, BlockPos pos,
                                 @Nullable Direction side)
    {
        if (level == null || pos == null || lookup == null)
        {
            return null;
        }

        T found = lookup.find(level, pos, side);
        if (found != null)
        {
            return found;
        }

        if (lookup == BDCapabilities.Item.BLOCK)
        {
            Storage<ItemVariant> storage = ItemStorage.SIDED.find(level, pos, side);
            return storage == null ? null : (T) new ItemStorageAdapter(storage);
        }
        if (lookup == BDCapabilities.Fluid.BLOCK)
        {
            Storage<FluidVariant> storage = FluidStorage.SIDED.find(level, pos, side);
            return storage == null ? null : (T) new FluidStorageAdapter(storage);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static <T> T getBlock(BlockApiLookup<T, Direction> lookup, BlockEntity be, @Nullable Direction side)
    {
        if (be == null)
        {
            return null;
        }
        T found = lookup.find(be.getLevel(), be.getBlockPos(), side);
        if (found != null)
        {
            return found;
        }
        Level level = be.getLevel();
        return level == null ? null : getBlock(lookup, level, be.getBlockPos(), side);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static <T> T getItem(ItemApiLookup<T, ItemAccess> lookup, ItemStack stack, ItemAccess access)
    {
        if (stack == null || stack.isEmpty() || access == null || lookup == null)
        {
            return null;
        }

        T found = lookup.find(stack, access);
        if (found != null)
        {
            return found;
        }

        if (lookup == BDCapabilities.Item.ITEM)
        {
            Storage<ItemVariant> storage = ItemStorage.ITEM.find(stack, access.fabricContext());
            return storage == null ? null : (T) new ItemStorageAdapter(storage);
        }
        if (lookup == BDCapabilities.Fluid.ITEM)
        {
            Storage<FluidVariant> storage = FluidStorage.ITEM.find(stack, access.fabricContext());
            return storage == null ? null : (T) new FluidStorageAdapter(storage);
        }
        return null;
    }

    /**
     * 实体物品能力。
     * <p>
     * Fabric 没有「实体物品能力」这一 lookup；玩家背包用 {@code PlayerInventoryStorage} 顶替，
     * 这是原版玩家容器在 Fabric 侧的标准视图。
     */
    @Nullable
    public static ResourceHandler<ItemResource> entityItemHandler(Entity entity, boolean automation)
    {
        if (!(entity instanceof Player player))
        {
            return null;
        }
        try
        {
            return new ItemStorageAdapter(PlayerInventoryStorage.of(player));
        }
        catch (Throwable ignored)
        {
            return null;
        }
    }
}
