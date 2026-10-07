package com.wintercogs.beyonddimensions.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

/** 模组使用的通用流体标签（原为 NeoForge 的 {@code Tags.Fluids}）。 */
public final class BDTags
{
    private BDTags() {}

    private static TagKey<Fluid> fluid(String path)
    {
        return TagKey.create(Registries.FLUID, Identifier.fromNamespaceAndPath("c", path));
    }

    public static final class Fluids
    {
        private Fluids() {}

        public static final TagKey<Fluid> EXPERIENCE = fluid("experience");
        public static final TagKey<Fluid> WATER = fluid("water");
        public static final TagKey<Fluid> LAVA = fluid("lava");
        public static final TagKey<Fluid> MILK = fluid("milk");
    }
}