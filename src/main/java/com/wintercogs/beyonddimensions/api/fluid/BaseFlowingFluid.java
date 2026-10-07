package com.wintercogs.beyonddimensions.api.fluid;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.function.Supplier;

/**
 * 移植垫片：NeoForge 的 {@code BaseFlowingFluid}。
 * <p>
 * 26.x 原版 {@code FlowingFluid} 不再自带 {@code Source}/{@code Flowing} 内部类，
 * 这里按原版水/岩浆的写法补回上游那两个内部类的语义。
 */
public abstract class BaseFlowingFluid extends FlowingFluid
{
    protected final Properties properties;

    protected BaseFlowingFluid(Properties properties)
    {
        this.properties = properties;
    }

    public Properties getProperties()
    {
        return properties;
    }

    @Override
    public Fluid getFlowing()
    {
        return properties.flowing == null ? Fluids.EMPTY : properties.flowing.get();
    }

    @Override
    public Fluid getSource()
    {
        return properties.source == null ? Fluids.EMPTY : properties.source.get();
    }

    @Override
    public Item getBucket()
    {
        return properties.bucket == null ? Items.AIR : properties.bucket.get();
    }

    @Override
    protected boolean canBeReplacedWith(FluidState state, BlockGetter level, BlockPos pos, Fluid fluid, Direction direction)
    {
        return direction == Direction.DOWN && fluid != getFlowing() && fluid != getSource();
    }

    @Override
    public int getTickDelay(LevelReader level)
    {
        return Math.max(1, properties.tickRate);
    }

    @Override
    protected float getExplosionResistance()
    {
        return properties.explosionResistance;
    }

    @Override
    protected BlockState createLegacyBlock(FluidState state)
    {
        if (properties.block == null)
        {
            return net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();
        }
        return properties.block.get().defaultBlockState()
                .setValue(LiquidBlock.LEVEL, getLegacyLevel(state));
    }

    @Override
    public VoxelShape getShape(FluidState state, BlockGetter level, BlockPos pos)
    {
        return state.getAmount() >= 8 ? Shapes.block() : Shapes.empty();
    }

    /** 流体属性表（上游同名类的子集）。 */
    public static class Properties
    {
        private final Supplier<? extends FluidType> fluidType;
        private final Supplier<? extends Fluid> source;
        private final Supplier<? extends Fluid> flowing;
        private Supplier<? extends Block> block;
        private Supplier<? extends Item> bucket;
        private int slopeFindDistance = 4;
        private int levelDecreasePerBlock = 1;
        private int tickRate = 5;
        private float explosionResistance = 100.0F;

        public Properties(Supplier<? extends FluidType> fluidType,
                          Supplier<? extends Fluid> source,
                          Supplier<? extends Fluid> flowing)
        {
            this.fluidType = fluidType;
            this.source = source;
            this.flowing = flowing;
        }

        public Properties block(Supplier<? extends Block> block)
        {
            this.block = block;
            return this;
        }

        public Properties bucket(Supplier<? extends Item> bucket)
        {
            this.bucket = bucket;
            return this;
        }

        public Properties slopeFindDistance(int value)
        {
            this.slopeFindDistance = value;
            return this;
        }

        public Properties levelDecreasePerBlock(int value)
        {
            this.levelDecreasePerBlock = value;
            return this;
        }

        public Properties tickRate(int value)
        {
            this.tickRate = value;
            return this;
        }

        public Properties explosionResistance(float value)
        {
            this.explosionResistance = value;
            return this;
        }

        public FluidType getFluidType()
        {
            return fluidType == null ? null : fluidType.get();
        }
    }

    /** 源方块流体。 */
    public static class Source extends BaseFlowingFluid
    {
        public Source(Properties properties)
        {
            super(properties);
        }

        @Override
        public int getAmount(FluidState state)
        {
            return 8;
        }

        @Override
        public boolean isSource(FluidState state)
        {
            return true;
        }

        @Override
        protected boolean canConvertToSource(ServerLevel level)
        {
            return false;
        }

        @Override
        protected void beforeDestroyingBlock(LevelAccessor level, BlockPos pos, BlockState state)
        {
            // 与上游默认实现一致：不额外掉落
        }

        @Override
        protected int getSlopeFindDistance(LevelReader level)
        {
            return properties.slopeFindDistance;
        }

        @Override
        protected int getDropOff(LevelReader level)
        {
            return properties.levelDecreasePerBlock;
        }
    }

    /** 流动流体。 */
    public static class Flowing extends BaseFlowingFluid
    {
        public Flowing(Properties properties)
        {
            super(properties);
        }

        @Override
        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder)
        {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }

        @Override
        public int getAmount(FluidState state)
        {
            return state.getValue(LEVEL);
        }

        @Override
        public boolean isSource(FluidState state)
        {
            return false;
        }

        @Override
        protected boolean canConvertToSource(ServerLevel level)
        {
            return false;
        }

        @Override
        protected void beforeDestroyingBlock(LevelAccessor level, BlockPos pos, BlockState state)
        {
            // 同上
        }

        @Override
        protected int getSlopeFindDistance(LevelReader level)
        {
            return properties.slopeFindDistance;
        }

        @Override
        protected int getDropOff(LevelReader level)
        {
            return properties.levelDecreasePerBlock;
        }
    }
}