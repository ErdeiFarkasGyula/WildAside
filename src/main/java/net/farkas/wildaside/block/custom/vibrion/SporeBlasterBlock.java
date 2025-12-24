package net.farkas.wildaside.block.custom.vibrion;

import net.farkas.wildaside.block.entity.custom.SporeBlasterBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jetbrains.annotations.Nullable;

public class SporeBlasterBlock extends DirectionalBlock implements EntityBlock {
    public SporeBlasterBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
    }

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, @Nullable Direction side) {
        return true;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(SporeBlasterBlock.FACING, pContext.getNearestLookingDirection().getOpposite());
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, net.minecraft.world.level.block.Block neighbor, BlockPos neighborPos, boolean moved) {
        if (!world.isClientSide) world.scheduleTick(pos, this, 2);
        super.neighborChanged(state, world, pos, neighbor, neighborPos, moved);
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean moved) {
        if (!world.isClientSide) world.scheduleTick(pos, this, 2);
        super.onPlace(state, world, pos, oldState, moved);
    }

    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource rand) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof SporeBlasterBlockEntity sbe) {
            sbe.tickServer();
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SporeBlasterBlockEntity(pos, state);
    }
}
