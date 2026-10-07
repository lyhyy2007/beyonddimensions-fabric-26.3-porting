package com.wintercogs.beyonddimensions.registry;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

/** 物品句柄：实现 {@link ItemLike}，可直接放入创造栏与 ItemStack。 */
public class BDItemHolder<T extends Item> extends BDHolder<T> implements ItemLike
{
    public BDItemHolder(Identifier id)
    {
        super(id);
    }

    @Override
    public Item asItem()
    {
        return get();
    }
}