package net.farkas.wildaside.worldgen.feature.custom;

import com.mojang.serialization.Codec;
import net.farkas.wildaside.util.LargeMushroomCapShape;
import net.farkas.wildaside.worldgen.feature.configuration.LargeMushroomConfiguration;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.phys.Vec3;
import org.joml.SimplexNoise;

import java.util.ArrayList;
import java.util.List;

public class LargeMushroomFeature extends Feature<LargeMushroomConfiguration> {
    public LargeMushroomFeature(Codec<LargeMushroomConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<LargeMushroomConfiguration> ctx) {
        WorldGenLevel level = ctx.level();
        RandomSource random = ctx.random();
        BlockPos origin = ctx.origin();
        LargeMushroomConfiguration config = ctx.config();

        BlockPos ground = findGround(level, origin, config);
        if (ground == null) return false;

        int clearance = findCeilingDistance(level, ground.above());
        if (clearance < config.minHeight()) return false;

        int height = Mth.clamp(random.nextIntBetweenInclusive(config.minHeight(), config.maxHeight()), config.minHeight(), config.maxHeight());
        height = Math.min(height, clearance - 2);

        Vec3 leanDir = calculateLeanDirection(level, ground);
        leanDir = exaggerateLean(level, ground, leanDir);

        if (!hasEnoughSpace(level, ground.above(), height, leanDir)) return false;

        generateStem(level, ground.above(), height, leanDir, config, random);

        LargeMushroomCapShape shape = LargeMushroomCapShape.pickWeightedShape(random, config.capShapeWeights());
        generateCap(level, ground.above(height), leanDir, config, random, height, shape);

        return true;
    }


    private BlockPos findGround(LevelAccessor level, BlockPos start, LargeMushroomConfiguration config) {
        BlockPos.MutableBlockPos pos = start.mutable();
        for (int dy = 0; dy < 8; dy++) {
            BlockPos below = pos.below(dy);
            BlockState stateBelow = level.getBlockState(below);

            for (var valid : config.validBaseBlocks()) {
                if (stateBelow.is(valid.getState(level.getRandom(), pos).getBlock()) && level.isEmptyBlock(below.above())) {
                    return below.above();
                }
            }
        }
        return null;
    }

    private int findCeilingDistance(WorldGenLevel level, BlockPos pos) {
        int dist = 0;
        BlockPos.MutableBlockPos cursor = pos.mutable();
        while (cursor.getY() < level.getMaxBuildHeight()) {
            cursor.move(0, 1, 0);
            if (!level.isEmptyBlock(cursor)) break;
            dist++;
        }
        return dist;
    }

    private Vec3 calculateLeanDirection(WorldGenLevel level, BlockPos base) {
        int radius = 4;
        Vec3 lean = Vec3.ZERO;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                BlockPos check = base.offset(dx, 0, dz);
                int open = findCeilingDistance(level, check);
                double weight = (open - 4.0);
                lean = lean.add(dx * weight, 0, dz * weight);
            }
        }
        if (lean.lengthSqr() < 0.01) return Vec3.ZERO;
        return lean.normalize();
    }

    private Vec3 exaggerateLean(WorldGenLevel level, BlockPos base, Vec3 lean) {
        if (lean.lengthSqr() < 0.01) return lean;

        int radius = 4;
        List<Integer> samples = new ArrayList<>();
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                samples.add(findCeilingDistance(level, base.offset(dx, 0, dz)));
            }
        }

        double avg = samples.stream().mapToInt(i -> i).average().orElse(0);
        double stddev = Math.sqrt(samples.stream().mapToDouble(v -> (v - avg) * (v - avg)).sum() / samples.size());
        double factor = Mth.clamp(0.7 + stddev * 0.25, 0.7, 2.2);

        return lean.normalize().scale(factor);
    }

    private boolean hasEnoughSpace(LevelAccessor level, BlockPos base, int height, Vec3 leanDir) {
        int blocked = 0;
        for (int i = 0; i <= height + 3; i++) {
            Vec3 offset = leanDir.scale(i / (float) height * 1.4);
            BlockPos check = base.offset((int) offset.x, i, (int) offset.z);

            if (!level.isEmptyBlock(check)) {
                blocked++;
                if (blocked > 12) return false;
            }
        }
        return true;
    }

    private void generateStem(LevelAccessor level, BlockPos base, int height, Vec3 leanDir, LargeMushroomConfiguration cfg, RandomSource random) {
        BlockState stem = cfg.stemBlock().getState(random, base);
        BlockState wood = cfg.woodBlock().getState(random, base);

        level.setBlock(base.below(), stem, 2);

        BlockPos lastPos = base;
        for (int i = 0; i < height; i++) {
            Vec3 offset = leanDir.scale(i / (float) height * 1.2);
            BlockPos pos = base.offset((int) offset.x, i, (int) offset.z);

            if (level.isEmptyBlock(pos)) {
                level.setBlock(pos, stem, 2);
            }

            if ((pos.getX() != lastPos.getX() || pos.getZ() != lastPos.getZ()) && i > 0) {
                if (level.getBlockState(lastPos).is(stem.getBlock()))
                    level.setBlock(lastPos, wood, 2);
                if (level.getBlockState(pos).is(stem.getBlock()))
                    level.setBlock(pos, wood, 2);
            }

            lastPos = pos;
        }
    }

    private void generateCap(LevelAccessor level, BlockPos top, Vec3 leanDir, LargeMushroomConfiguration cfg, RandomSource random, int stemHeight, LargeMushroomCapShape shape) {
        Vec3 capOffset = leanDir.scale(1f);
        BlockPos center = top.offset((int) capOffset.x, shape.yOffset(), (int) capOffset.z);
        BlockState capBlock = cfg.capBlock().getState(random, center);

        int radius = computeCapRadius(level, center, stemHeight, 6);

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                double dist = Math.sqrt(dx * dx + dz * dz);
                if (dist > radius + 0.3) continue;

                int capHeight = computeCapShape(shape, dist, radius);
                BlockPos pos = center.offset(dx, capHeight, dz);

                if (level.isEmptyBlock(pos)) level.setBlock(pos, capBlock, 2);

                double innerRadius = radius * 0.8f;
                if (canDecorate(shape) && dist < innerRadius) {
                    if (random.nextFloat() < 0.35f && !cfg.decoratorBlocks().isEmpty()) {
                        BlockPos decoPos = pos.below();
                        if (level.isEmptyBlock(decoPos)) {
                            BlockState deco = cfg.decoratorBlocks().get(random.nextInt(cfg.decoratorBlocks().size())).getState(random, decoPos);
                            level.setBlock(decoPos, deco, 2);
                        }
                    }

//                    if (random.nextFloat() < 0.25f) {
//                        int length = 1 + random.nextInt(3);
//                        for (int l = 1; l <= length; l++) {
//                            BlockPos hangPos = pos.below(l);
//                            if (!level.isEmptyBlock(hangPos)) break;
//                            BlockState vine = cfg.hangingVinesBlock().getState(random, hangPos);
//                            level.setBlock(hangPos, vine, 3);
//                        }
//                    }
                }
            }
        }
    }

    private int measureHeadroom(LevelAccessor level, BlockPos basePos, int maxSearch) {
        for (int dy = 1; dy < maxSearch; dy++) {
            BlockPos checkPos = basePos.above(dy);
            if (!level.getBlockState(checkPos).isAir()) {
                return dy - 1;
            }
        }
        return maxSearch;
    }

    private double measureLateralOpenness(LevelAccessor level, BlockPos basePos, int maxRadius) {
        int openDirs = 0;
        int totalDirs = 0;

        for (int dx = -maxRadius; dx <= maxRadius; dx += maxRadius / 2) {
            for (int dz = -maxRadius; dz <= maxRadius; dz += maxRadius / 2) {
                if (dx == 0 && dz == 0) continue;
                totalDirs++;
                BlockPos check = basePos.offset(dx, 1, dz);
                if (level.getBlockState(check).isAir()) openDirs++;
            }
        }

        return (double)openDirs / totalDirs;
    }

    private int computeCapRadius(LevelAccessor level, BlockPos basePos, int trunkHeight, int maxRadius) {
        int headroom = measureHeadroom(level, basePos, 12);
        double openness = measureLateralOpenness(level, basePos, 6);

        double heightFactor = 1.0 - Math.exp(-trunkHeight / 5.0);
        double spaceFactor = Math.min(1.0, (headroom / (double)trunkHeight) * 0.8 + openness * 0.2);

        double variation = (SimplexNoise.noise((float) (basePos.getX() * 0.2), (float) (basePos.getZ() * 0.2)) + 1) * 0.1 + 0.9;

        int radius = (int)(maxRadius * heightFactor * spaceFactor * variation);
        return Mth.clamp(radius, 2, maxRadius);
    }

    private int computeCapShape(LargeMushroomCapShape shape, double dist, int radius) {
        return switch(shape) {
            case FLAT -> 0;
//            case CONCAVE -> (int) (-Math.cos(dist / radius * Math.PI) * 2 + 2);
//            case VANILLA_LARGE -> {
//                if (dist > radius) yield -1;
//                int height = (int) Math.ceil(Math.cos(dist / radius * Math.PI / 2) * 3);
//                yield Math.max(1, height);
//            }
//            case BELL -> {
//                double angle = dist / radius * Math.PI / 2;
//                yield (int) (Math.cos(angle) * 3 - (dist / radius) * 2);
//            }
//            case PARABOLIC -> (int) (-(dist * dist) / (radius * 0.8) + 3);
//            case CONE -> (int) (3 - (dist / radius) * 3);
//            case PILZ -> {
//                double ratio = dist / radius;
//                yield (int) (Math.sin(ratio * Math.PI) * 2 - (ratio > 0.7 ? -1 : 0));
//            }
//            case LOBED -> {
//                double lobes = 3.5;
//                double wave = Math.cos(dist / radius * Math.PI / 2);
//                double variation = Math.sin((dist / radius) * lobes * Math.PI) * 1.3;
//                yield (int) (wave * 2 + variation);
//            }
//            case FLARED -> {
//                double ratio = dist / radius;
//                if (ratio < 0.6) yield 1;
//                yield (int) (-Math.sin((ratio - 0.6) * Math.PI / 0.8) * 2);
//            }
            default -> (int) (Math.cos(dist / radius * Math.PI / 2) * 2);
        };
    }

    private boolean canDecorate(LargeMushroomCapShape shape) {
        return shape.canDecorate();
    }
}