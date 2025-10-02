package net.farkas.wildaside.worldgen.feature.custom;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import org.joml.Vector3d;

public class HangingStringFeature extends Feature<SimpleBlockConfiguration> {
    private BlockStateProvider stringBlock;

    public HangingStringFeature(Codec<SimpleBlockConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<SimpleBlockConfiguration> context) {
        stringBlock = context.config().toPlace();

        LevelAccessor level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();

        if (!areChunksLoaded(level, origin, 2)) {
            return false;
        }

        BlockPos wallPos = findWall(level, origin, 20, 10, random);
        if (wallPos == null) return false;

        placeSaggingLine(level, origin, wallPos, stringBlock.getState(random, context.origin()), (float) random.nextInt(1, 11) / 10);

        return true;
    }

    private boolean areChunksLoaded(LevelAccessor level, BlockPos pos, int radius) {
        if (level instanceof ServerLevel serverLevel) {
            int chunkX = pos.getX() >> 4;
            int chunkZ = pos.getZ() >> 4;

            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (!serverLevel.hasChunk(chunkX + dx, chunkZ + dz)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private BlockPos findWall(LevelAccessor world, BlockPos origin, int maxHorizontal, int maxVertical, RandomSource random) {
        int dx = Mth.clamp(random.nextInt(maxHorizontal * 2 + 1) - maxHorizontal, -8, 8);
        int dz = Mth.clamp(random.nextInt(maxHorizontal * 2 + 1) - maxHorizontal, -8, 8);

        for (int y = origin.getY() - maxVertical; y < origin.getY() + maxVertical; y++) {
            BlockPos pos = new BlockPos(origin.getX() + dx, y, origin.getZ() + dz);
            if (!world.getBlockState(pos).isAir()) {
                return pos;
            }
        }
        return null;
    }

    private void placeSaggingLine(LevelAccessor world, BlockPos start, BlockPos end, BlockState state, float sagFactor) {
        Vector3d startVec = new Vector3d(start.getX() + 0.5, start.getY() + 0.5, start.getZ() + 0.5);
        Vector3d endVec = new Vector3d(end.getX() + 0.5, end.getY() + 0.5, end.getZ() + 0.5);

        double length = startVec.distance(endVec);
        int steps = (int)(length * 3);

        for (int i = 0; i <= steps; i++) {
            double t = i / (double) steps;

            double x = Mth.lerp(t, startVec.x, endVec.x);
            double z = Mth.lerp(t, startVec.z, endVec.z);

            double y = Mth.lerp(t, startVec.y, endVec.y);
            double sag = Math.sin(t * Math.PI) * length * sagFactor;
            y -= sag;

            BlockPos pos = new BlockPos(Mth.floor(x), Mth.floor(y), Mth.floor(z));
            if (world.isEmptyBlock(pos) || world.getBlockState(pos).canBeReplaced()) {
                world.setBlock(pos, state, 3);
            }
        }
    }
}