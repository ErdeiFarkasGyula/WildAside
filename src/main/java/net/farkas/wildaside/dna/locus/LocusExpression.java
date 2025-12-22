package net.farkas.wildaside.dna.locus;

import net.farkas.wildaside.dna.DnaPolicy;
import net.farkas.wildaside.dna.allele.value.AlleleValue;
import net.farkas.wildaside.dna.allele.value.FloatAlleleValue;
import net.farkas.wildaside.dna.trait.Trait;
import net.farkas.wildaside.dna.trait.TraitType;

import java.util.List;

public final class LocusExpression {
    public static AlleleValue express(Trait trait, List<GeneLocus> loci) {
        if (loci == null || loci.isEmpty()) return new FloatAlleleValue(0f);

        float sum = 0f;
        int count = 0;
        float activator = 0f;
        float regulator = 0f;

        for (GeneLocus locus : loci) {
            AlleleValue v = locus.getExpressedValue();
            if (v instanceof FloatAlleleValue fv) {
                float val = fv.get();
                if (locus.getFlags().contains(LocusFlag.ACTIVATOR)) {
                    activator = Math.max(activator, val);
                } else if (locus.getFlags().contains(LocusFlag.REGULATOR)) {
                    regulator = Math.max(regulator, clamp01(val));
                } else {
                    sum += val;
                    count++;
                }
            }
        }

        if ((trait.getTraitType() == TraitType.ABILITY || trait.getTraitType() == TraitType.RESISTANCE) && activator <= DnaPolicy.ACTIVATOR_THRESHOLD) {
            return new FloatAlleleValue(0f);
        }

        if (count == 0) return new FloatAlleleValue(0f);

        float base = sum / count;
        float regBoost = (float) Math.pow(clamp01(regulator), DnaPolicy.REGULATOR_DR);
        regBoost = Math.min(regBoost, DnaPolicy.REGULATOR_CAP);

        float expressed = base * (1f + regBoost);
        return new FloatAlleleValue(expressed);
    }

    private static float clamp01(float v) {
        return Math.max(0f, Math.min(1f, v));
    }
}