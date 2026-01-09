package net.farkas.wildaside.dna;

import net.farkas.wildaside.dna.allele.value.FloatAlleleValue;
import net.farkas.wildaside.dna.locus.GeneLocus;
import net.farkas.wildaside.dna.locus.LocusFlag;
import net.farkas.wildaside.dna.trait.Trait;
import net.farkas.wildaside.dna.trait.TraitType;

import java.util.List;
import java.util.Set;

public class DnaPolicy {
    public static final float ACTIVATOR_THRESHOLD = 0.05f;
    public static final float REGULATOR_CAP = 1.0f;
    public static final float REGULATOR_DR = 0.6f;

    public static float flagInstabilityMultiplier(Set<LocusFlag> flags) {
        int majors = count(flags, LocusFlag.ACTIVATOR, LocusFlag.POTENCY);
        int minors = count(flags, LocusFlag.REGULATOR, LocusFlag.SIDE_EFFECT, LocusFlag.PATHWAY_HINT);
        int excess = Math.max(0, (majors - 1) + (minors - 1));
        return 1.0f + 0.25f * excess;
    }

    public static float stressCostForEdit(Trait trait, float locusStability, float deltaMagnitude, Set<LocusFlag> flags) {
        float base = trait.getInstabilityModifier();
        float frag = 1.0f / Math.max(0.1f, locusStability);
        float flagMult = flagInstabilityMultiplier(flags);
        return base * frag * flagMult * clamp(deltaMagnitude, 0.1f, 5f);
    }

    public static boolean isLatentTrait(Trait trait, List<GeneLocus> loci) {
        if (loci == null || loci.isEmpty()) return false;
        if (trait.getTraitType() != TraitType.ABILITY && trait.getTraitType() != TraitType.RESISTANCE) return false;
        float activator = 0f;
        for (GeneLocus locus : loci) {
            if (locus.getFlags().contains(LocusFlag.ACTIVATOR) && locus.getExpressedValue() instanceof FloatAlleleValue fv) {
                activator = Math.max(activator, fv.get());
            }
        }
        return activator <= ACTIVATOR_THRESHOLD;
    }

    private static int count(Set<LocusFlag> flags, LocusFlag... of) {
        int c = 0;
        for (LocusFlag f : of) if (flags.contains(f)) c++;
        return c;
    }

    private static float clamp(float v, float min, float max) {
        return Math.max(min, Math.min(max, v));
    }
}