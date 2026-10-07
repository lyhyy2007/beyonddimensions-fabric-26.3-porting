package com.wintercogs.beyonddimensions.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTab;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** 创造模式标签页列表（注册顺序）。 */
public final class BDCreativeTabs
{
    private BDCreativeTabs() {}

    public static List<CreativeModeTab> getSortedCreativeModeTabs()
    {
        List<CreativeModeTab> tabs = new ArrayList<>(BuiltInRegistries.CREATIVE_MODE_TAB.stream().toList());
        tabs.sort(Comparator.comparingInt(tab -> BuiltInRegistries.CREATIVE_MODE_TAB.getId(tab)));
        return tabs;
    }
}