package net.farkas.wildaside.block.custom;

import net.farkas.wildaside.block.ModBlocks;
import net.farkas.wildaside.particle.ModParticles;
import net.farkas.wildaside.util.HickoryColour;
import net.farkas.wildaside.util.ParticleUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;

public class HickoryLeavesBlock extends LeavesBlock {
    public HickoryLeavesBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        super.animateTick(pState, pLevel, pPos, pRandom);
        if (pLevel.getBlockState(pPos.below()).isFaceSturdy(pLevel, pPos.below(), Direction.UP)) return;

        SimpleParticleType particle = ModParticles.HICKORY_LEAF_PARTICLE.get();

        if (pRandom.nextFloat() < 0.025) {
            ParticleUtils.spawnHickoryParticles(pLevel, pPos, pRandom, particle);
        }
    }

    @Override
    public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        if (pRandom.nextFloat() < 0.02) {
            spawnNewFallenLeaves(pLevel, pPos, pRandom);
        }
        super.randomTick(pState, pLevel, pPos, pRandom);
    }

    private void spawnNewFallenLeaves(Level level, BlockPos pPos, RandomSource random) {
        if (level.isClientSide) return;

        int x = pPos.getX() + random.nextIntBetweenInclusive(-2, 2);
        int z = pPos.getZ() + random.nextIntBetweenInclusive(-2, 2);


        int groundY = -100;
        for (int y = pPos.getY(); y >= -64; y--) {
            BlockPos pos = new BlockPos(x, y, z);
            if (level.getBlockState(pos).isFaceSturdy(level, pos, Direction.UP)) {
                BlockState aboveState = level.getBlockState(pos.above());
                if (aboveState.isAir() || aboveState.getBlock() instanceof FallenHickoryLeavesBlock) {
                    groundY = y;
                    break;
                }
                break;
            }
        }

        if (groundY > -64) {
            BlockPos target = new BlockPos(x, groundY + 1, z);
            BlockState state = level.getBlockState(target);
            Direction direction;
            int count = 1;

            if (state.getBlock() instanceof FallenHickoryLeavesBlock) {
                if (state.getValue(FallenHickoryLeavesBlock.COLOUR) != HickoryColour.HICKORY) return;
                count = Math.min(level.getBlockState(target).getValue(FallenHickoryLeavesBlock.COUNT) + 1, 3);
                direction = level.getBlockState(target).getValue(FallenHickoryLeavesBlock.FACING);
            } else {
                direction = Direction.Plane.HORIZONTAL.getRandomDirection(random);
            }

            BlockState leafSt = ModBlocks.FALLEN_HICKORY_LEAVES.get()
                    .defaultBlockState()
                    .setValue(FallenHickoryLeavesBlock.COUNT, count)
                    .setValue(FallenHickoryLeavesBlock.COLOUR, HickoryColour.HICKORY)
                    .setValue(FallenHickoryLeavesBlock.FACING, direction);

            level.setBlock(target, leafSt, 3);
        }
    }

    @Override
    public boolean isRandomlyTicking(BlockState pState) {
        return true;
    }
}
