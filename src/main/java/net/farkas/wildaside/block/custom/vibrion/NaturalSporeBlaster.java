package net.farkas.wildaside.block.custom.vibrion;

import net.farkas.wildaside.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.jetbrains.annotations.Nullable;

public class NaturalSporeBlaster extends RotatedPillarBlock {
    BlockState spore_air = ModBlocks.SPORE_AIR.get().defaultBlockState();

    public NaturalSporeBlaster(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction) {
        return true;
    }

    @Override
    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pNeighborBlock, BlockPos pNeighborPos, boolean pMovedByPiston) {
        if (!pLevel.isClientSide) {
            pLevel.scheduleTick(pPos, this, 20);
        }
        super.neighborChanged(pState, pLevel, pPos, pNeighborBlock, pNeighborPos, pMovedByPiston);
    }

    @Override
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pMovedByPiston) {
        if (!pLevel.isClientSide) {
            pLevel.scheduleTick(pPos, this, 20);
        }
        super.onPlace(pState, pLevel, pPos, pOldState, pMovedByPiston);
    }

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        int x = pLevel.getBlockState(pPos).getValue(AXIS).equals(Direction.Axis.X) ? 1 : 0;
        int y = pLevel.getBlockState(pPos).getValue(AXIS).equals(Direction.Axis.Y) ? 1 : 0;
        int z = pLevel.getBlockState(pPos).getValue(AXIS).equals(Direction.Axis.Z) ? 1 : 0;

        spawnSporAir(pLevel, pPos, pRandom, x, y, z);
        spawnSporAir(pLevel, pPos, pRandom, -x, -y, -z);
        pLevel.scheduleTick(pPos, this, 20);
        super.tick(pState, pLevel, pPos, pRandom);
    }

    public void spawnSporAir(ServerLevel level, BlockPos pos, RandomSource random, int x, int y, int z) {
        int power = random.nextIntBetweenInclusive(1, 15);

        for (int i = 1; i <= power; i++) {
            var position = pos.offset(x * i, y * i, z * i);
            var nextBlock = level.getBlockState(position);

            if (nextBlock.isCollisionShapeFullBlock(level, position)) return;
            if (nextBlock.isAir()) level.setBlockAndUpdate(position, spore_air);
        }
    }
}
