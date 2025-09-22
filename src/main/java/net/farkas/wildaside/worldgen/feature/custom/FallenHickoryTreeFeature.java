package net.farkas.wildaside.worldgen.feature.custom;

import com.mojang.serialization.Codec;
import net.farkas.wildaside.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class FallenHickoryTreeFeature extends Feature<NoneFeatureConfiguration> {
    public FallenHickoryTreeFeature(Codec<NoneFeatureConfiguration> pCodec) {
        super(pCodec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        LevelAccessor world = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();

        Direction dir = Direction.Plane.HORIZONTAL.getRandomDirection(random);

        final int maxLength = 10;
        final int minLength = 3;
        int available = 0;

        for (int i = 0; i < maxLength; i++) {
            BlockPos pos = origin.relative(dir, i);
            BlockPos below = pos.below();
            if ((world.isEmptyBlock(pos) || (!world.getBlockState(pos).isSolid())) && world.getBlockState(below).isFaceSturdy(world, below, Direction.UP)) {
                available++;
            } else {
                break;
            }
        }

        if (available <= minLength) {
            return false;
        }

        final int length = random.nextInt(minLength, available);
        final BlockState log = ModBlocks.HICKORY_LOG.get().defaultBlockState().setValue(RotatedPillarBlock.AXIS, dir.getAxis());
        int skipPlace = random.nextInt(length);

        for (int i = 0; i < length; i++) {
            BlockPos trunkPos = origin.relative(dir, i);
            if (!(random.nextFloat() < 0.5f && i == skipPlace)) {
                world.setBlock(trunkPos, log, 3);
            }

////            if (random.nextFloat() < 0.2f && (world.isEmptyBlock(top)) && Blocks.RED_MUSHROOM.defaultBlockState().canSurvive(world, top)) {
////            }
        }

        return true;
    }
}
