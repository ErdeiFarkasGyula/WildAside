package net.farkas.wildaside.util;

import net.farkas.wildaside.particle.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

public class ParticleUtils {
    public static void spawnHickoryParticles(Level level, BlockPos pos, RandomSource random, SimpleParticleType particle) {
        level.addParticle(particle, (pos.getX() + random.nextFloat()), (pos.getY() - 0.5f), (pos.getZ() + random.nextFloat()),
                random.nextFloat() / 15, 0, random.nextFloat() / 15);
    }

    public static void spawnSubstiliumSoilParticles(Level level, BlockPos pos, RandomSource random) {
        if (!level.getBlockState(pos.above()).isCollisionShapeFullBlock(level, pos.above())) {
            if (random.nextFloat() < 0.5f) {
                level.addParticle(ModParticles.SUBSTILIUM_PARTICLE.get(), (pos.getX() + random.nextFloat()), (pos.getY() + 1), (pos.getZ() + random.nextFloat()),
                        0, ((double) Mth.nextInt(random, 7, 13) / 1000), 0);
            }
        }
    }
}
