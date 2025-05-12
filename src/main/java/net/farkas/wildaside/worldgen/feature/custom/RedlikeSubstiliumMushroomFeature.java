package net.farkas.wildaside.worldgen.feature.custom;

import com.mojang.serialization.Codec;
import net.farkas.wildaside.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HugeMushroomBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.AbstractHugeMushroomFeature;
import net.minecraft.world.level.levelgen.feature.configurations.HugeMushroomFeatureConfiguration;

import java.util.List;

public class RedlikeSubstiliumMushroomFeature extends AbstractHugeMushroomFeature {
    private static final List<Block> blocks = List.of(ModBlocks.VIBRION_BLOCK.get(), ModBlocks.VIBRION_BLOCK.get(), ModBlocks.VIBRION_GEL.get(), ModBlocks.LIT_VIBRION_GEL.get());

    public RedlikeSubstiliumMushroomFeature(Codec<HugeMushroomFeatureConfiguration> pCodec) {
        super(pCodec);
    }

    @Override
    protected int getTreeHeight(RandomSource pRandom) {
        return pRandom.nextInt(5, 12);
    }

    @Override
    protected int getTreeRadiusForHeight(int p_65094_, int p_65095_, int pFoliageRadius, int pY) {
        int i = 0;
        if (pY < p_65095_ && pY >= p_65095_ - 3) {
            i = pFoliageRadius;
        } else if (pY == p_65095_) {
            i = pFoliageRadius;
        }

        return i;
    }

    @Override
    protected void makeCap(LevelAccessor pLevel, RandomSource pRandom, BlockPos pPos, int pTreeHeight, BlockPos.MutableBlockPos pMutablePos, HugeMushroomFeatureConfiguration pConfig) {
        for (int i = pTreeHeight - 3; i <= pTreeHeight; ++i) {
            int j = i < pTreeHeight ? pConfig.foliageRadius : pConfig.foliageRadius - 1;
            int k = pConfig.foliageRadius - 2;

            for (int l = -j; l <= j; ++l) {
                for (int i1 = -j; i1 <= j; ++i1) {
                    boolean flag = l == -j;
                    boolean flag1 = l == j;
                    boolean flag2 = i1 == -j;
                    boolean flag3 = i1 == j;
                    boolean flag4 = flag || flag1;
                    boolean flag5 = flag2 || flag3;
                    if (i >= pTreeHeight || flag4 != flag5) {
                        pMutablePos.setWithOffset(pPos, l, i, i1);
                        if (!pLevel.getBlockState(pMutablePos).isSolidRender(pLevel, pMutablePos)) {
                            BlockState blockstate = pConfig.capProvider.getState(pRandom, pPos);
                            if (blockstate.hasProperty(HugeMushroomBlock.WEST) && blockstate.hasProperty(HugeMushroomBlock.EAST) && blockstate.hasProperty(HugeMushroomBlock.NORTH) && blockstate.hasProperty(HugeMushroomBlock.SOUTH) && blockstate.hasProperty(HugeMushroomBlock.UP)) {
                                blockstate = blockstate.setValue(HugeMushroomBlock.UP, Boolean.valueOf(i >= pTreeHeight - 1)).setValue(HugeMushroomBlock.WEST, Boolean.valueOf(l < -k)).setValue(HugeMushroomBlock.EAST, Boolean.valueOf(l > k)).setValue(HugeMushroomBlock.NORTH, Boolean.valueOf(i1 < -k)).setValue(HugeMushroomBlock.SOUTH, Boolean.valueOf(i1 > k));
                            }

                            this.setBlock(pLevel, pMutablePos, blockstate);
                        }
                    }
                }
            }
        }

        RandomSource random = RandomSource.create();
        for (int i = 1; i < 4; i++) {
            for (int j = 0; j < random.nextInt(3, 7 - i); j++) {
                Block block = blocks.get(random.nextInt(0, blocks.size()));
                int offX = random.nextBoolean() ? -2 : 0;
                int offZ = random.nextBoolean() ? -2 : 0;
                BlockPos pos = pMutablePos.offset(offX, -i, offZ);
                if (pLevel.getBlockState(pos).isAir()) {
                    this.setBlock(pLevel, pos, block.defaultBlockState());
                }
            }
        }
    }
}
