package com.wintercogs.beyonddimensions.client.init;

import com.wintercogs.beyonddimensions.api.ids.BDConstants;
import com.wintercogs.beyonddimensions.common.init.BDFluids;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.material.Fluid;

import java.util.ArrayList;
import java.util.List;

/**
 * 客户端流体模型登记表（仅客户端加载）。
 * <p>
 * 原来由 NeoForge 的 {@code RegisterFluidModelsEvent} 承载；Fabric 原生版改为模组自有登记表，
 * 由 {@code FluidStateModelSetMixin} 在 {@code FluidStateModelSet.bake} 的返回处读取并补入模型。
 */
public final class BDFluidModels
{
    private BDFluidModels() {}

    public record Entry(FluidModel.Unbaked model, Fluid fluid) {}

    private static final List<Entry> ENTRIES = new ArrayList<>();

    public static List<Entry> entries()
    {
        return ENTRIES;
    }

    /** 为全部已注册流体构建并登记模型。 */
    public static void registerAll()
    {
        for (BDFluids.FluidEntry e : BDFluids.ALL)
        {
            final Identifier still = Identifier.fromNamespaceAndPath(BDConstants.MODID, "block/" + e.name() + "_still");
            final Identifier flow = Identifier.fromNamespaceAndPath(BDConstants.MODID, "block/" + e.name() + "_flow");
            final int tint = e.argbTint();

            ENTRIES.add(new Entry(new FluidModel.Unbaked(
                    new Material(still),
                    new Material(flow),
                    null,
                    state -> tint
            ), e.source().get()));
        }
    }
}