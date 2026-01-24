package net.farkas.wildaside.dna.appearance;

import net.farkas.wildaside.dna.DnaUtils;
import net.farkas.wildaside.dna.dominance.Dominance;
import net.farkas.wildaside.dna.sequence.components.CodingRegion;
import net.farkas.wildaside.dna.sequence.CombineMethod;
import net.farkas.wildaside.dna.sequence.GeneSequence;
import net.farkas.wildaside.dna.sequence.GeneSource;
import net.farkas.wildaside.dna.trait.Trait;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public final class AppearanceUtils {
    public static GeneSequence createVariantSequence(Trait trait, ResourceLocation variant, long seed, String salt) {
        float mutationRate = lerp(
                0.005f,
                0.04f,
                DnaUtils.hashToFloat(seed, salt + "_mut", 0)
        );

        Dominance dominance = Dominance.DOMINANT;
        
        return GeneSequence.builder()
                .trait(trait)
                .codingRegion(new CodingRegion(salt, 1.0f, CombineMethod.SET))
                .dominance(dominance)
                .mutationRate(mutationRate)
                .source(GeneSource.NATURAL)
                .build();
    }

    private static float lerp(float min, float max, float t) {
        return min + (max - min) * t;
    }

    public static ResourceLocation pickFromSeed(Registry<?> registry, long seed) {
        var all = registry.keySet().stream().toList();
        int idx = (int) (Math.abs(seed * 17) % all.size());
        return all.get(idx);
    }
}