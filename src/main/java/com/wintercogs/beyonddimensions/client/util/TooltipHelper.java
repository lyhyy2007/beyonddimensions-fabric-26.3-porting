package com.wintercogs.beyonddimensions.client.util;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** 客户端 tooltip 组装（原为 NeoForge 的 {@code ClientHooks.gatherTooltipComponents}）。 */
public final class TooltipHelper
{
    private TooltipHelper() {}

    public static List<ClientTooltipComponent> gatherTooltipComponents(ItemStack stack,
                                                                       List<? extends FormattedText> textElements,
                                                                       Optional<TooltipComponent> tooltipImage,
                                                                       int mouseX, int screenWidth, int screenHeight,
                                                                       Font font)
    {
        List<ClientTooltipComponent> components = new ArrayList<>();
        if (tooltipImage != null && tooltipImage.isPresent())
        {
            components.add(ClientTooltipComponent.create(tooltipImage.get()));
        }
        for (FormattedText text : textElements)
        {
            components.add(ClientTooltipComponent.create(net.minecraft.locale.Language.getInstance().getVisualOrder(text)));
        }
        return components;
    }
}