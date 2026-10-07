package com.wintercogs.beyonddimensions.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/** 移植垫片：事件钩子。 */
public final class EventHooks
{
    private EventHooks() {}

    /** 上游在物品被拾取后触发事件；Fabric 侧无对应事件，空实现。 */
    public static void fireItemPickupPost(net.minecraft.world.entity.item.ItemEntity entity,
                                          net.minecraft.world.entity.player.Player player,
                                          ItemStack stack)
    {
        // no-op
    }

    /** 上游该钩子会触发 ItemUseFinish 事件；Fabric 侧无对应事件，直接返回结果堆叠。 */
    public static ItemStack onItemUseFinish(LivingEntity entity, ItemStack stack, int duration,
                                            @Nullable ItemStack result)
    {
        return result == null ? stack : result;
    }
}