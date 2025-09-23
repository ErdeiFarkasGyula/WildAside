package net.farkas.wildaside.entity;

import net.farkas.wildaside.entity.custom.vibrion.MucellithEntity;
import net.farkas.wildaside.util.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

public class ModEntitySpawns {
    public static void registerSpawnPlacements() {
        SpawnPlacements.register(
                ModEntities.MUCELLITH.get(),
                SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                ModEntitySpawns::canSpawnVibrionEntity
        );
    }

    private static boolean canSpawnVibrionEntity(EntityType<MucellithEntity> type, ServerLevelAccessor level, MobSpawnType reason, BlockPos pos, RandomSource random) {
        if (level.getDifficulty() == Difficulty.PEACEFUL) return false;
        if (!(level.getBlockState(pos.below()).is(ModTags.Blocks.MUCELLITH_SPAWN_BLOCKS))) return false;

        return level.getMaxLocalRawBrightness(pos) <= 7;
    }
}