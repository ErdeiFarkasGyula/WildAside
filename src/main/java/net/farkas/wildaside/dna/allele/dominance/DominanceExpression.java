package net.farkas.wildaside.dna.allele.dominance;

import net.farkas.wildaside.dna.DnaUtils;
import net.farkas.wildaside.dna.trait.Trait;

public record DominanceExpression(float dominantChance, float recessiveChance, float coDominantChance, float incompleteChance) {
    public Dominance chooseDominance(Trait trait, long seed, int index) {
        DominanceExpression profile = trait.getTraitType().getDominanceExpression();

        float roll = DnaUtils.hashToFloat(seed, trait.getName(), index);

        if (roll < profile.dominantChance) return Dominance.DOMINANT;
        if (roll < profile.dominantChance + profile.recessiveChance) return Dominance.RECESSIVE;
        if (roll < profile.dominantChance + profile.recessiveChance + profile.coDominantChance) return Dominance.CO_DOMINANT;
        return Dominance.INCOMPLETE;
    }
}
