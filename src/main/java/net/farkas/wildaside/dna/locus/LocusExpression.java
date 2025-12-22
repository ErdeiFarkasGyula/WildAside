package net.farkas.wildaside.dna.locus;

import net.farkas.wildaside.dna.allele.value.AlleleValue;
import net.farkas.wildaside.dna.allele.value.FloatAlleleValue;
import net.farkas.wildaside.dna.trait.Trait;

import java.util.List;

public final class LocusExpression {
    public static AlleleValue express(Trait trait, List<GeneLocus> loci) {
        if (loci == null || loci.isEmpty()) return new FloatAlleleValue(0f);

        float sum = 0f;
        int count = 0;
        float regulatorBoost = 0f;

        for (GeneLocus locus : loci) {
            AlleleValue v = locus.getExpressedValue();
            if (v instanceof FloatAlleleValue fv) {
                float val = fv.get();
                if (locus.getFlags().contains(LocusFlag.REGULATOR)) {
                    regulatorBoost = Math.max(regulatorBoost, clamp01(val));
                } else {
                    sum += val;
                    count++;
                }
            }
        }

        if (count == 0) return new FloatAlleleValue(0f);

        float base = sum / count;
        float expressed = base * (1f + regulatorBoost);
        return new FloatAlleleValue(expressed);
    }

    private static float clamp01(float v) {
        return Math.max(0f, Math.min(1f, v));
    }
}