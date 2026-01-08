package net.farkas.wildaside.dna.merge;

import net.farkas.wildaside.dna.DnaPolicy;
import net.farkas.wildaside.dna.DnaUtils;
import net.farkas.wildaside.dna.allele.value.AlleleValue;
import net.farkas.wildaside.dna.allele.value.FloatAlleleValue;
import net.farkas.wildaside.dna.locus.GeneLocus;
import net.farkas.wildaside.dna.locus.LocusSource;
import net.farkas.wildaside.dna.trait.Trait;
import net.farkas.wildaside.dna.trait.TraitType;

import java.util.*;

public class DnaMerger {
    public static final int MAX_LOCI_PER_TRAIT = 4;
    public static final float BASE_INTEGRATION_CHANCE = 0.35f;
    public static final float STRESS_INTEGRATION_BONUS = 0.4f;

    public static MergeResult merge(Map<Trait, List<GeneLocus>> hostLoci, Map<Trait, List<GeneLocus>> invaderLoci, float hostStress, long currentTick, long seed) {
        Map<Trait, List<GeneLocus>> result = new HashMap<>();
        float totalStressGain = 0f;
        List<MergeEvent> events = new ArrayList<>();

        for (var entry : hostLoci.entrySet()) {
            result.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }

        for (var entry : invaderLoci.entrySet()) {
            Trait trait = entry.getKey();
            List<GeneLocus> invadingLoci = entry.getValue();
            List<GeneLocus> existingLoci = result.computeIfAbsent(trait, k -> new ArrayList<>());

            for (GeneLocus invader : invadingLoci) {
                MergeOutcome outcome = resolveConflict(trait, existingLoci, invader, hostStress, currentTick, seed);

                applyOutcome(existingLoci, invader, outcome, currentTick);
                totalStressGain += outcome.stressCost();
                events.add(new MergeEvent(trait, invader, outcome));

                seed = DnaUtils.mix64(seed ^ invader.getId().hashCode());
            }
        }

        return new MergeResult(result, totalStressGain, events);
    }

    private static MergeOutcome resolveConflict(Trait trait, List<GeneLocus> existing, GeneLocus invader, float hostStress, long currentTick, long seed) {
        float integrationChance = BASE_INTEGRATION_CHANCE + (hostStress / 100f) * STRESS_INTEGRATION_BONUS;

        integrationChance *= getTraitTypeMultiplier(trait.getTraitType());

        integrationChance *= Math.max(0.3f, invader.getStability());

        float roll = DnaUtils.hashToFloat(seed, trait.getName() + invader.getId(), 0);
        float stressCost = calculateStressCost(trait, invader, existing);

        if (existing.size() < MAX_LOCI_PER_TRAIT) {
            if (roll < integrationChance) {
                return new MergeOutcome(MergeOutcomeType.INTEGRATED, null, stressCost);
            }
            else if (roll < integrationChance + 0.2f) {
                return new MergeOutcome(MergeOutcomeType.TRANSIENT, null, stressCost * 1.5f);
            }
            else {
                return new MergeOutcome(MergeOutcomeType.REJECTED, null, stressCost * 0.7f);
            }
        }
        else {
            GeneLocus weakest = findWeakestLocus(existing);
            if (weakest != null && compareStrength(invader, weakest) > 0 && roll < integrationChance) {
                return new MergeOutcome(MergeOutcomeType.REPLACED, weakest, stressCost * 1.2f);
            }
            else {
                return new MergeOutcome(MergeOutcomeType.REJECTED, null, stressCost * 0.5f);
            }
        }
    }

    private static void applyOutcome(List<GeneLocus> existingLoci, GeneLocus invader, MergeOutcome outcome, long currentTick) {
        switch (outcome.type()) {
            case INTEGRATED -> {
                existingLoci.add(invader.withSource(LocusSource.INTEGRATED, currentTick));
            }
            case TRANSIENT -> {
                existingLoci.add(invader.withSource(LocusSource.TRANSIENT, currentTick));
            }
            case REPLACED -> {
                if (outcome.replacedLocus() != null) {
                    existingLoci.remove(outcome.replacedLocus());
                }
                existingLoci.add(invader.withSource(LocusSource.INTEGRATED, currentTick));
            }
            case REJECTED -> {
                existingLoci.add(invader.withSource(LocusSource.REJECTED, currentTick));
            }
        }
    }

    private static float getTraitTypeMultiplier(TraitType type) {
        return switch (type) {
            case CORE -> 0.8f;
            case RESISTANCE -> 1.0f;
            case ABILITY -> 0.6f;
            case APPEARANCE -> 1.2f;
        };
    }

    private static float calculateStressCost(Trait trait, GeneLocus invader, List<GeneLocus> existing) {
        float base = trait.getInstabilityModifier();
        float fragility = 1.0f / Math.max(0.1f, invader.getStability());
        float crowding = 1.0f + (existing.size() * 0.2f);
        float flagMult = DnaPolicy.flagInstabilityMultiplier(invader.getFlags());

        return base * fragility * crowding * flagMult;
    }

    private static GeneLocus findWeakestLocus(List<GeneLocus> loci) {
        if (loci.isEmpty()) return null;

        GeneLocus weakest = null;
        float weakestScore = Float.MAX_VALUE;

        for (GeneLocus locus : loci) {
            float score = calculateLocusStrength(locus);
            if (locus.getSource() != LocusSource.NATIVE) {
                score *= 0.5f;
            }
            if (score < weakestScore) {
                weakestScore = score;
                weakest = locus;
            }
        }

        return weakest;
    }

    private static float calculateLocusStrength(GeneLocus locus) {
        AlleleValue val = locus.getExpressedValue();
        float valueScore = (val instanceof FloatAlleleValue fv) ? Math.abs(fv.get()) : 1f;
        return valueScore * locus.getStability() * (1f - locus.getDegradation());
    }

    private static float compareStrength(GeneLocus a, GeneLocus b) {
        return calculateLocusStrength(a) - calculateLocusStrength(b);
    }
}