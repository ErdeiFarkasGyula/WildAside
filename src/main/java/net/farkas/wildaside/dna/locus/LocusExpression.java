package net.farkas.wildaside.dna.locus;

import net.farkas.wildaside.dna.DnaPolicy;
import net.farkas.wildaside.dna.allele.value.AlleleValue;
import net.farkas.wildaside.dna.allele.value.FloatAlleleValue;
import net.farkas.wildaside.dna.trait.Trait;
import net.farkas.wildaside.dna.trait.TraitType;
import net.minecraft.util.Mth;

import java.util.List;

public final class LocusExpression {
    public static AlleleValue express(Trait trait, List<GeneLocus> loci) {
        if (loci == null || loci.isEmpty()) return new FloatAlleleValue(0f);

        float weightedSum = 0f;
        float totalWeight = 0f;
        float activator = 0f;
        float regulator = 0f;

        for (GeneLocus locus : loci) {
            AlleleValue v = locus.getExpressedValue();
            float weight = locus.getExpressionWeight();

            if (v instanceof FloatAlleleValue fv) {
                float val = fv.get();

                if (locus.getFlags().contains(LocusFlag.ACTIVATOR)) {
                    activator = Math.max(activator, val * weight);
                }
                else if (locus.getFlags().contains(LocusFlag.REGULATOR)) {
                    regulator = Math.max(regulator, Mth.clamp(val, 0f, 1f) * weight);
                }
                else {
                    weightedSum += val * weight;
                    totalWeight += weight;
                }
            }
        }

        if ((trait.getTraitType() == TraitType.ABILITY || trait.getTraitType() == TraitType.RESISTANCE)
                && activator <= DnaPolicy.ACTIVATOR_THRESHOLD) {
            return new FloatAlleleValue(0f);
        }

        if (totalWeight == 0f) return new FloatAlleleValue(0f);

        float base = weightedSum / totalWeight;

        float regBoost = (float) Math.pow(Mth.clamp(regulator, 0f, 1f), DnaPolicy.REGULATOR_DR);
        regBoost = Math.min(regBoost, DnaPolicy.REGULATOR_CAP);

        float expressed = base * (1f + regBoost);
        return new FloatAlleleValue(expressed);
    }
}