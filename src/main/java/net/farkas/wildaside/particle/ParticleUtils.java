package net.farkas.wildaside.particle;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

public class ParticleUtils {
    public static void spawnHickoryParticles(Level level, BlockPos pos, RandomSource random, SimpleParticleType particle) {
        level.addParticle(particle, (pos.getX() + random.nextFloat()), (pos.getY() - 0.5f), (pos.getZ() + random.nextFloat()),
                random.nextFloat() / 15, 0, random.nextFloat() / 15);
    }
}
