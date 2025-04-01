package net.farkas.wildaside.block.custom;

import net.farkas.wildaside.block.ModBlocks;
import net.farkas.wildaside.particle.ModParticles;
import net.farkas.wildaside.worldgen.ModPlacedFeatures;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.IPlantable;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class SubstiliumSoil extends Block implements BonemealableBlock {
    public SubstiliumSoil(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pMovedByPiston) {
        super.onPlace(pState, pLevel, pPos, pOldState, pMovedByPiston);
    }

    @Override
    public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
        super.onNeighborChange(state, level, pos, neighbor);
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        if (!pLevel.getBlockState(pPos.above()).isCollisionShapeFullBlock(pLevel, pPos.above())) {
            for (int i = 0; i < Mth.nextInt(pRandom, 1, 3); i++) {
                pLevel.addParticle(ModParticles.SUBSTILIUM_PARTICLE.get(), (pPos.getX() + pRandom.nextFloat()), (pPos.getY() + 1), (pPos.getZ() + pRandom.nextFloat()),
                        0, (Mth.nextInt(pRandom, 7, 13) / 1000), 0);
            }
        }
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
        BlockPos blockpos = pPos.above();
        BlockState blockstate = ModBlocks.SUBSTILIUM_SOIL.get().defaultBlockState();

        List<BlockState> blocks = List.of(
                ModBlocks.SUBSTILIUM_SPROUTS.get().defaultBlockState(),
                ModBlocks.SUBSTILIUM_SPROUTS.get().defaultBlockState(),
                ModBlocks.SUBSTILIUM_SPROUTS.get().defaultBlockState(),
                ModBlocks.VIBRION_GROWTH.get().defaultBlockState(),
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
