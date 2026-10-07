package com.wintercogs.beyonddimensions.api.transfer.access;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * 移植垫片：NeoForge 的「物品能力访问上下文」。
 * <p>
 * 直接承载 Fabric 的 {@link ContainerItemContext}，用于把 {@code ItemCapability} 查询转发到 Fabric 的物品能力查找。
 */
public interface ItemAccess
{
    ContainerItemContext fabricContext();

    @Nullable
    ItemStack getStack();

    static ItemAccess forPlayerCursor(Player player, AbstractContainerMenu menu)
    {
        return of(ContainerItemContext.ofPlayerCursor(player, menu), menu.getCarried());
    }

    static ItemAccess forStack(ItemStack stack)
    {
        if (stack == null || stack.isEmpty())
        {
            return of(ContainerItemContext.withConstant(ItemStack.EMPTY), ItemStack.EMPTY);
        }
        return of(ContainerItemContext.withConstant(stack), stack);
    }

    private static ItemAccess of(ContainerItemContext context, ItemStack stack)
    {
        return new ItemAccess()
        {
            @Override
            public ContainerItemContext fabricContext()
            {
                return context;
            }

            @Override
            public ItemStack getStack()
            {
                return stack;
            }
        };
    }
}