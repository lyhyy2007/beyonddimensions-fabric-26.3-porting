package com.wintercogs.beyonddimensions.common.init;

import com.wintercogs.beyonddimensions.registry.BDBlockHolder;
import com.wintercogs.beyonddimensions.registry.BDHolder;
import com.wintercogs.beyonddimensions.registry.BDItemHolder;
import com.wintercogs.beyonddimensions.registry.BDRegistry;
import net.minecraft.core.registries.BuiltInRegistries;

import com.wintercogs.beyonddimensions.api.ids.BDConstants;
import com.wintercogs.beyonddimensions.common.fluid.XpFluid;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Fluid;
import net.fabricmc.api.EnvType;
import com.wintercogs.beyonddimensions.api.fluid.BaseFlowingFluid;
import com.wintercogs.beyonddimensions.api.fluid.FluidType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
public class BDFluids
{
    public static final List<FluidEntry> ALL = new ArrayList<>();

    public static final FluidEntry XP_FLUID = registerFluid(
            "xp_fluid",
            FluidType.Properties.create()
                    .lightLevel(10)
                    .density(800)
                    .viscosity(1500),
            0xFFFFFFFF, // tint是乘法，传入白色，保持原有纹理
            10,
            XpFluid.Source::new,
            XpFluid.Flowing::new
    );

    // 可传入具体的 Source/Flowing 类构造器
    public static <S extends BaseFlowingFluid.Source, F extends BaseFlowingFluid.Flowing>
    FluidEntry registerFluid(
            String name,
            FluidType.Properties typeProps,
            int argbTint,
            int lightlevel,
            Function<BaseFlowingFluid.Properties, S> sourceCtor,
            Function<BaseFlowingFluid.Properties, F> flowingCtor
    )
    {
        // 1) FluidType：原版没有「流体类型」注册表，直接持有实例
        FluidType type = new FluidType(typeProps);

        // 2) 解决循环依赖：propsRef 先占位，之后回填
        final BaseFlowingFluid.Properties[] propsRef = new BaseFlowingFluid.Properties[1];

        // 3) 流体（源/流动）：必须先于方块实例化
        BDHolder<net.minecraft.world.level.material.FlowingFluid> source = BDRegistry.<net.minecraft.world.level.material.FlowingFluid>defer(BDRegistry.PHASE_FLUID,
                BuiltInRegistries.FLUID, name, () -> sourceCtor.apply(propsRef[0]));
        BDHolder<net.minecraft.world.level.material.FlowingFluid> flowing = BDRegistry.<net.minecraft.world.level.material.FlowingFluid>defer(BDRegistry.PHASE_FLUID,
                BuiltInRegistries.FLUID, "flowing_" + name, () -> flowingCtor.apply(propsRef[0]));

        // 4) 方块 + 桶
        BDBlockHolder<LiquidBlock> block = BDRegistry.deferBlock(name,
                props -> new LiquidBlock(source.get(), props),
                () -> BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).lightLevel(s -> lightlevel));

        BDItemHolder<Item> bucket = BDRegistry.deferItem(name + "_bucket",
                props -> new BucketItem(source.get(), props.craftRemainder(Items.BUCKET).stacksTo(1)));

        // 5) 回填 BaseFlowingFluid.Properties
        propsRef[0] = new BaseFlowingFluid.Properties(() -> type, source, flowing)
                .bucket(bucket)
                .block(block)
                .slopeFindDistance(4)
                .levelDecreasePerBlock(2);

        FluidEntry entry = new FluidEntry(name, type, source, flowing, block, bucket, argbTint);
        ALL.add(entry);
        return entry;
    }

    // 使用基础的 BaseFlowingFluid
    public static FluidEntry registerSimpleFluid(String name, FluidType.Properties typeProps, int argbTint, int lightlevel)
    {
        return registerFluid(
                name, typeProps, argbTint, lightlevel,
                BaseFlowingFluid.Source::new,
                BaseFlowingFluid.Flowing::new
        );
    }

    public static void register()
    {
        // 注册由 DeferredRegistry.flush() 统一执行（Fabric 原生侧不再需要事件总线）
    }

    public record FluidEntry(
            String name,
            FluidType type,
            BDHolder<net.minecraft.world.level.material.FlowingFluid> source,
            BDHolder<net.minecraft.world.level.material.FlowingFluid> flowing,
            BDBlockHolder<LiquidBlock> block,
            BDItemHolder<Item> bucket,
            int argbTint
    )
    {
    }
}
