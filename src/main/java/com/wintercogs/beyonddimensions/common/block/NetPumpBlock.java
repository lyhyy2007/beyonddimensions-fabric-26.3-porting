package com.wintercogs.beyonddimensions.common.block;

import com.wintercogs.beyonddimensions.fabric.BDMenuOpener;

import com.wintercogs.beyonddimensions.common.block.entity.NetPumpBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class NetPumpBlock extends BaseMachineBlock
{
    public NetPumpBlock(Properties properties)
    {
        super(properties.noOcclusion());
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState)
    {
        return new NetPumpBlockEntity(blockPos, blockState);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult)
    {
        super.useWithoutItem(state, level, pos, player, hitResult);
        if (!level.isClientSide() && !player.isShiftKeyDown())
        {
            NetPumpBlockEntity blockEntity = (NetPumpBlockEntity) level.getBlockEntity(pos);
            BDMenuOpener.open(player, blockEntity, pos);
        }
        return InteractionResult.SUCCESS;
    }    public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor)
    {
        if (level.getBlockEntity(pos) instanceof NetPumpBlockEntity blockEntity)
        {
            blockEntity.setNeedsCapabilityUpdate();
        }
    }
}
