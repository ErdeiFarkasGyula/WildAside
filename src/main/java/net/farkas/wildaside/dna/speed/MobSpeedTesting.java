package net.farkas.wildaside.dna.speed;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MobSpeedTesting {
    private static final Set<EntityType<?>> EXCLUDED_MOBS = Set.of(
            EntityType.ENDER_DRAGON,
            EntityType.WITHER,
            EntityType.BAT,
            EntityType.PHANTOM,
            EntityType.GHAST,
            EntityType.VEX,
            EntityType.SLIME,
            EntityType.MAGMA_CUBE,
            EntityType.GIANT,
            EntityType.WANDERING_TRADER,
            EntityType.WARDEN
    );

    public static void spawnAllMobs(ServerLevel level, BlockPos start) {
        runTest(level, start, false);
        runTest(level, start, true);
    }

    public static void runTest(ServerLevel level, BlockPos start, boolean inWater) {
        String testName = inWater ? "water" : "ground";

        int offsetZ = 0;
        int yOffset = inWater ? 5 : 0;
        start = start.offset(0, yOffset, 0);

        for (EntityType<? extends Mob> type : getMobsToTest(level)) {
            Mob mob = type.create(level);
            if (mob == null) continue;

            System.out.println("MEOW UUID: " + mob.getUUID() + "MOB: " + mob);

            MobSpeedTestTracker.registerMob(mob, testName);

            buildTestBox(level, start.offset(0, 0, offsetZ), 120, 5, 5, inWater);
            BlockPos spawnPos = start.offset(0, 0, offsetZ);

            mob.moveTo(spawnPos.getX() + 0.5 + 3, spawnPos.getY() + 1, spawnPos.getZ() + 0.5 + 2, 0, 0);
            level.addFreshEntity(mob);

            mob.goalSelector.getAvailableGoals().clear();
            mob.targetSelector.getAvailableGoals().clear();

            mob.goalSelector.addGoal(0, new StraightLineGoal(mob, Direction.EAST, 5, testName));

            offsetZ += 4;
        }
    }

    public static List<EntityType<? extends Mob>> getMobsToTest(ServerLevel level) {
        var registry = level.registryAccess().registryOrThrow(Registries.ENTITY_TYPE);

        List<EntityType<? extends Mob>> mobsToTest = new ArrayList<>();

        for (EntityType<?> type : registry) {
            if (!type.canSummon()) continue;
            if (!(type.create(level) instanceof Mob)) continue;
            if (EXCLUDED_MOBS.contains(type)) continue;

            mobsToTest.add((EntityType<? extends Mob>) type);
            System.out.println("TYPE: " + type);
        }

        return mobsToTest;
    }

    public static void buildTestBox(ServerLevel level, BlockPos start, int length, int width, int height, boolean water) {
        Block floorBlock = Blocks.STONE;
        Block wallBlock = Blocks.BARRIER;
        Block waterBlock = Blocks.WATER;

        for (int x = 0; x < length; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < width; z++) {
                    BlockPos pos = start.offset(x, y, z);

                    if (y == 0) {
                        level.setBlock(pos, floorBlock.defaultBlockState(), 3);

                        if (water) {
                            level.setBlock(pos.above(), waterBlock.defaultBlockState(), 3);
                        }
                    }

                    else if (y == height - 1) {
                        level.setBlock(pos, wallBlock.defaultBlockState(), 3);
                    }
                    else if (z == 0 || z == width - 1) {
                            level.setBlock(pos, wallBlock.defaultBlockState(), 3);
                        }
                        else if (x == 0) {
                                level.setBlock(pos, wallBlock.defaultBlockState(), 3);
                            }
                }
            }
        }
    }

    public static void runBatchTest(ServerLevel level, BlockPos groundStart) {
        spawnAllMobs(level, groundStart);
    }
}