package net.farkas.wildaside.block.custom;

import net.farkas.wildaside.block.ModBlocks;
import net.farkas.wildaside.util.ParticleUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.IPlantable;

import java.util.List;

public class SubstiliumSoilBlock extends DropExperienceBlock implements BonemealableBlock {
    public SubstiliumSoilBlock(Properties pProperties, IntProvider pXpRange) {
        super(pProperties, pXpRange);
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        ParticleUtils.spawnSubstiliumSoilParticles(pLevel, pPos, pRandom);
        super.animateTick(pState, pLevel, pPos, pRandom);
    }

    @Override
    public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, IPlantable plantable) {
        return true;
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader pLevel, BlockPos pPos, BlockState pState, boolean pIsClient) {
        return pLevel.getBlockState(pPos.above()).isAir();
    }

    @Override
    public boolean isBonemealSuccess(Level pLevel, RandomSource pRandom, BlockPos pPos, BlockState pState) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel pLevel, RandomSource pRandom, BlockPos pPos, BlockState pState) {
        if (pLevel.isClientSide()) return;

        BlockPos blockpos = pPos.above();
        BlockState blockstate = ModBlocks.SUBSTILIUM_SOIL.get().defaultBlockState();

        List<BlockState> blocks = List.of(
                ModBlocks.SUBSTILIUM_SPROUTS.get().defaultBlockState(),
                ModBlocks.SUBSTILIUM_SPROUTS.get().defaultBlockState(),
                ModBlocks.VIBRION_GROWTH.get().defaultBlockState(),
                ModBlocks.VIBRION_GROWTH.get().defaultBlockState(),
                ModBlocks.VIBRION_SPOREHOLDER.get().defaultBlockState()
        );

        label49:
        for (int i = 0; i < 128; ++i) {
            BlockPos blockpos1 = blockpos;

            for (int j = 0; j < i / 16; ++j) {
                blockpos1 = blockpos1.offset(pRandom.nextInt(3) - 1, (pRandom.nextInt(3) - 1) * pRandom.nextInt(3) / 2, pRandom.nextInt(3) - 1);
                if (!pLevel.getBlockState(blockpos1.below()).is(this) || pLevel.getBlockState(blockpos1).isCollisionShapeFullBlock(pLevel, blockpos1)) {
                    continue label49;
                }
            }

            BlockState blockstate1 = pLevel.getBlockState(blockpos1);
            if (blockstate1.is(blockstate.getBlock()) && pRandom.nextInt(10) == 0) {
                ((BonemealableBlock)blockstate.getBlock()).performBonemeal(pLevel, pRandom, blockpos1, blockstate1);
            }

            if (blockstate1.isAir()) {
                pLevel.setBlock(blockpos1, blocks.get(pRandom.nextInt(blocks.size())), 2);
            }
        }
    }
}
