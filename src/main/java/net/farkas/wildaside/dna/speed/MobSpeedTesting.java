package net.farkas.wildaside.dna.speed;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.config.ModConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MobSpeedTesting {
    public static List<EntityType<? extends PathfinderMob>> mobs = new ArrayList<>();
    public static int entityCount = 0;

    public static final Set<EntityType<?>> EXCLUDED_MOBS = Set.of(
            EntityType.PLAYER,
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
            EntityType.WARDEN,
            EntityType.SHULKER
    );

    public static void spawnAllMobs(ServerLevel level, BlockPos start) {
        runTest(level, start, false);
        if (ModConfig.accurateDnaWaterMovementSpeeds) {
            runTest(level, start, true);
        }
    }

    public static void runTest(ServerLevel level, BlockPos start, boolean inWater) {
        String testName = inWater ? "water" : "ground";

        int offsetZ = 0;
        int yOffset = inWater ? 5 : 0;
        start = start.offset(0, yOffset, 0);

        for (EntityType<? extends PathfinderMob> type : mobs) {
            try {
                PathfinderMob mob = type.create(level);
                if (mob == null) continue;

                MobSpeedTestTracker.registerMob(mob, testName);

                BlockPos pos = start.offset(0, 0, offsetZ);
                buildTestBox(level, pos, 120, 5, 5, inWater);

                mob.goalSelector.getAvailableGoals().clear();
                mob.targetSelector.getAvailableGoals().clear();

                mob.moveTo(pos.getX() + 0.5 + 3, pos.getY() + 1, pos.getZ() + 0.5 + 2, 0, 0);
                level.addFreshEntity(mob);

                mob.goalSelector.addGoal(0, new MobSpeedTestGoal(mob, Direction.EAST, 5, testName));

                offsetZ += 4;
            } catch (Exception e) {
                WildAside.LOGGER.warn("Skipping entity " + type + " due to exception: " + e);
            }
        }
    }

    public static List<EntityType<? extends PathfinderMob>> getMobsToTest(ServerLevel level) {
        var registry = level.registryAccess().registryOrThrow(Registries.ENTITY_TYPE);
        List<EntityType<? extends PathfinderMob>> mobsToTest = new ArrayList<>();

        for (EntityType<?> type : registry) {
            try {
                if (!type.canSummon()) continue;

                ResourceLocation id = ForgeRegistries.ENTITY_TYPES.getKey(type);
                if (id == null) continue;

                String namespace = id.getNamespace();
                String path = id.getPath();

                Entity entity = type.create(level);
                if (entity instanceof PathfinderMob mob) {
                    if (!EXCLUDED_MOBS.contains(type)) {
                        mobsToTest.add((EntityType<? extends PathfinderMob>) type);
                    }
                }
            } catch (Exception e) {
                WildAside.LOGGER.warn("Skipped entity: {} due to exception: {}", ForgeRegistries.ENTITY_TYPES.getKey(type), e.getClass().getSimpleName());
            }
        }

        entityCount = mobsToTest.size();
        mobs = mobsToTest;
        return mobsToTest;
    }

    public static void buildTestBox(ServerLevel level, BlockPos start, int length, int width, int height, boolean water) {
        Block floorBlock = Blocks.BEDROCK;
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