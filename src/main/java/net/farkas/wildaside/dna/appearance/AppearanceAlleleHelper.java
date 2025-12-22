package net.farkas.wildaside.dna.appearance;

import net.farkas.wildaside.dna.DnaUtils;
import net.farkas.wildaside.dna.allele.Allele;
import net.farkas.wildaside.dna.allele.dominance.Dominance;
import net.farkas.wildaside.dna.allele.value.ResourceLocationAlleleValue;
import net.minecraft.resources.ResourceLocation;

public final class AppearanceAlleleHelper {
    public static Allele createVariantAllele(ResourceLocation variant, long seed, String salt) {
        float mutationRate = lerp(
                0.005f,
                0.04f,
                DnaUtils.hashToFloat(seed, salt + "_mut", 0)
        );

        Dominance dominance = DnaUtils.deterministicDominancePick(seed, salt);

        return new Allele(new ResourceLocationAlleleValue(variant), mutationRate, dominance);
    }

    private static float lerp(float min, float max, float t) {
        return min + (max - min) * t;
    }
}