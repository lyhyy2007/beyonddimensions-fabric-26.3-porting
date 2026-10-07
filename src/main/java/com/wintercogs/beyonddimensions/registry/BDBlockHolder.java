package com.wintercogs.beyonddimensions.registry;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

/** 方块句柄：实现 {@link ItemLike}，可直接放入创造栏与作为方块物品来源。 */
public class BDBlockHolder<T extends Block> extends BDHolder<T> implements ItemLike
{
    public BDBlockHolder(Identifier id)
    {
        super(id);
    }

    @Override
    public Item asItem()
    {
        return get().asItem();
    }
}