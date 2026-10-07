package com.wintercogs.beyonddimensions.api.transfer.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import com.wintercogs.beyonddimensions.api.transfer.Resource;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 移植垫片：NeoForge 26.x 的 {@code ItemResource}（物品 + 组件，不含数量）。
 * <p>
 * 内部按「数量 1 的 ItemStack」做实例缓存，保证同一资源拿到同一个对象（上游同样是单例语义）。
 */
public final class ItemResource extends Resource
{
    private static final Map<ItemStack, ItemResource> CACHE = new ConcurrentHashMap<>();

    public static final ItemResource EMPTY = new ItemResource(ItemStack.EMPTY);

    private final ItemStack stack;

    private ItemResource(ItemStack stack)
    {
        this.stack = stack;
    }

    public static @NotNull ItemResource of(ItemStack stack)
    {
        if (stack == null || stack.isEmpty())
        {
            return EMPTY;
        }
        if (stack.getCount() == 1)
        {
            return CACHE.computeIfAbsent(stack, ItemResource::new);
        }
        return CACHE.computeIfAbsent(stack.copyWithCount(1), ItemResource::new);
    }

    @Override
    public boolean isEmpty()
    {
        return this == EMPTY || stack.isEmpty();
    }

    /** 该资源的原型堆叠（数量 1）；EMPTY 返回 {@link ItemStack#EMPTY}。 */
    public ItemStack toStack()
    {
        return stack;
    }

    public ItemStack toStack(int amount)
    {
        return isEmpty() ? ItemStack.EMPTY : stack.copyWithCount(amount);
    }

    public Item getItem()
    {
        return stack.getItem();
    }

    public int getMaxStackSize()
    {
        return isEmpty() ? 0 : stack.getMaxStackSize();
    }

    /** 资源身份匹配：物品与组件一致即可，不比数量。 */
    public boolean matches(ItemStack other)
    {
        if (isEmpty() || other == null || other.isEmpty())
        {
            return false;
        }
        return ItemStack.isSameItemSameComponents(stack, other);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof ItemResource other)) return false;
        return ItemStack.isSameItemSameComponents(stack, other.stack);
    }

    @Override
    public int hashCode()
    {
        return stack.isEmpty() ? 0 : 31 * Item.getId(stack.getItem()) + stack.getComponents().hashCode();
    }

    @Override
    public String toString()
    {
        return isEmpty() ? "ItemResource.EMPTY" : stack.getItem().toString();
    }
}