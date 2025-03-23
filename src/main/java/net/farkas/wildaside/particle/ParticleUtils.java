package net.farkas.wildaside.particle;

import net.farkas.wildaside.block.ModBlocks;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Map;

public class ParticleUtils {
    public static final Map<Block, SimpleParticleType> glowingHickoryParticleMap = Map.of(
            ModBlocks.RED_GLOWING_HICKORY_LEAVES.get(), ModParticles.RED_GLOWING_HICKORY_PARTICLE.get(),
            ModBlocks.BROWN_GLOWING_HICKORY_LEAVES.get(), ModParticles.BROWN_GLOWING_HICKORY_PARTICLE.get(),
            ModBlocks.YELLOW_GLOWING_HICKORY_LEAVES.get(), ModParticles.YELLOW_GLOWING_HICKORY_PARTICLE.get(),
            ModBlocks.GREEN_GLOWING_HICKORY_SAPLING.get(), ModParticles.GREEN_GLOWING_HICKORY_PARTICLE.get()
    );
}
