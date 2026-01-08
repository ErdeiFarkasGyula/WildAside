package net.farkas.wildaside.dna.merge;

import net.farkas.wildaside.WildAside;
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
        WildAside.LOGGER.info("=== DNA MERGE START ===");
        WildAside.LOGGER.info("Host stress: {}, Seed: {}, Tick: {}", String.format("%.2f", hostStress), seed, currentTick);
        WildAside.LOGGER.info("Host has {} traits, Invader has {} traits", hostLoci.size(), invaderLoci.size());

        Map<Trait, List<GeneLocus>> result = new HashMap<>();
        float totalStressGain = 0f;
        List<MergeEvent> events = new ArrayList<>();

        for (var entry : hostLoci.entrySet()) {
            result.put(entry.getKey(), new ArrayList<>(entry.getValue()));
            WildAside.LOGGER.debug("Copied host trait [{}] with {} loci", entry.getKey().getName(), entry.getValue().size());
        }

        for (var entry : invaderLoci.entrySet()) {
            Trait trait = entry.getKey();
            List<GeneLocus> invadingLoci = entry.getValue();
            List<GeneLocus> existingLoci = result.computeIfAbsent(trait, k -> new ArrayList<>());

            WildAside.LOGGER.info("Processing invader trait [{}] with {} loci (host has {} existing)",
                    trait.getName(), invadingLoci.size(), existingLoci.size());

            for (GeneLocus invader : invadingLoci) {
                WildAside.LOGGER.debug("  Resolving conflict for locus [{}], stability: {}", invader.getId(), String.format("%.3f", invader.getStability()));

                MergeOutcome outcome = resolveConflict(trait, existingLoci, invader, hostStress, currentTick, seed);

                WildAside.LOGGER.info("  -> Outcome: {} | Stress cost: {}", outcome.type(), String.format("%.2f", outcome.stressCost()));

                applyOutcome(existingLoci, invader, outcome, currentTick);
                totalStressGain += outcome.stressCost();
                events.add(new MergeEvent(trait, invader, outcome));

                seed = DnaUtils.mix64(seed ^ invader.getId().hashCode());
            }
        }

        WildAside.LOGGER.info("=== DNA MERGE COMPLETE ===");
        WildAside.LOGGER.info("Total stress gain: {} | Events: {} (Integrated: {}, Transient: {}, Rejected: {})",
                String.format("%.2f", totalStressGain),
                events.size(),
                events.stream().filter(e -> e.outcome().type() == MergeOutcomeType.INTEGRATED).count(),
                events.stream().filter(e -> e.outcome().type() == MergeOutcomeType.TRANSIENT).count(),
                events.stream().filter(e -> e.outcome().type() == MergeOutcomeType.REJECTED).count());

        return new MergeResult(result, totalStressGain, events);
    }

    private static MergeOutcome resolveConflict(Trait trait, List<GeneLocus> existing, GeneLocus invader, float hostStress, long currentTick, long seed) {
        float baseChance = BASE_INTEGRATION_CHANCE;
        float stressBonus = (hostStress / 100f) * STRESS_INTEGRATION_BONUS;
        float traitMult = getTraitTypeMultiplier(trait.getTraitType());
        float stabilityMult = Math.max(0.3f, invader.getStability());

        float integrationChance = (baseChance + stressBonus) * traitMult * stabilityMult;

        float roll = DnaUtils.hashToFloat(seed, trait.getName() + invader.getId(), 0);
        float stressCost = calculateStressCost(trait, invader, existing);

        WildAside.LOGGER.debug("    Integration calc: base={} + stressBonus={} = {} × traitMult={} × stability={} = {}",
                String.format("%.2f", baseChance),
                String.format("%.2f", stressBonus),
                String.format("%.2f", baseChance + stressBonus),
                String.format("%.2f", traitMult),
                String.format("%.2f", stabilityMult),
                String.format("%.3f", integrationChance));
        WildAside.LOGGER.debug("    Roll: {} vs thresholds [INTEGRATED < {}, TRANSIENT < {}, else REJECTED]",
                String.format("%.3f", roll),
                String.format("%.3f", integrationChance),
                String.format("%.3f", integrationChance + 0.2f));

        if (existing.size() < MAX_LOCI_PER_TRAIT) {
            WildAside.LOGGER.debug("    Room available ({}/{} loci)", existing.size(), MAX_LOCI_PER_TRAIT);

            if (roll < integrationChance) {
                WildAside.LOGGER.debug("    -> INTEGRATED (roll {} < {})", String.format("%.3f", roll), String.format("%.3f", integrationChance));
                return new MergeOutcome(MergeOutcomeType.INTEGRATED, null, stressCost);
            }
            else if (roll < integrationChance + 0.2f) {
                WildAside.LOGGER.debug("    -> TRANSIENT (roll {} < {})", String.format("%.3f", roll), String.format("%.3f", integrationChance + 0.2f));
                return new MergeOutcome(MergeOutcomeType.TRANSIENT, null, stressCost * 1.5f);
            }
            else {
                WildAside.LOGGER.debug("    -> REJECTED (roll {} >= {})", String.format("%.3f", roll), String.format("%.3f", integrationChance + 0.2f));
                return new MergeOutcome(MergeOutcomeType.REJECTED, null, stressCost * 0.7f);
            }
        }
        else {
            WildAside.LOGGER.debug("    At capacity ({}/{} loci) - competing for slot",
                    existing.size(), MAX_LOCI_PER_TRAIT);

            GeneLocus weakest = findWeakestLocus(existing);
            float invaderStrength = calculateLocusStrength(invader);
            float weakestStrength = weakest != null ? calculateLocusStrength(weakest) : Float.MAX_VALUE;

            WildAside.LOGGER.debug("    Invader strength: {} vs Weakest [{}] strength: {}",
                    String.format("%.3f", invaderStrength),
                    weakest != null ? weakest.getId() : "none",
                    String.format("%.3f", weakestStrength));

            if (weakest != null && invaderStrength > weakestStrength && roll < integrationChance) {
                WildAside.LOGGER.debug("    -> REPLACED (stronger and roll {} < {})",
                        String.format("%.3f", roll), String.format("%.3f", integrationChance));
                return new MergeOutcome(MergeOutcomeType.REPLACED, weakest, stressCost * 1.2f);
            }
            else {
                WildAside.LOGGER.debug("    -> REJECTED (couldn't compete)");
                return new MergeOutcome(MergeOutcomeType.REJECTED, null, stressCost * 0.5f);
            }
        }
    }

    private static void applyOutcome(List<GeneLocus> existingLoci, GeneLocus invader, MergeOutcome outcome, long currentTick) {
        switch (outcome.type()) {
            case INTEGRATED -> {
                GeneLocus integrated = invader.withSource(LocusSource.INTEGRATED, currentTick);
                existingLoci.add(integrated);
                WildAside.LOGGER.debug("    Applied:  Added locus [{}] as INTEGRATED", invader.getId());
            }
            case TRANSIENT -> {
                GeneLocus transient_ = invader.withSource(LocusSource.TRANSIENT, currentTick);
                existingLoci.add(transient_);
                WildAside.LOGGER.debug("    Applied: Added locus [{}] as TRANSIENT (will degrade)", invader.getId());
            }
            case REPLACED -> {
                if (outcome.replacedLocus() != null) {
                    existingLoci.remove(outcome.replacedLocus());
                    WildAside.LOGGER.debug("    Applied: Removed weak locus [{}]", outcome.replacedLocus().getId());
                }
                GeneLocus integrated = invader.withSource(LocusSource.INTEGRATED, currentTick);
                existingLoci.add(integrated);
                WildAside.LOGGER.debug("    Applied: Added locus [{}] as INTEGRATED (replacement)", invader.getId());
            }
            case REJECTED -> {
                GeneLocus rejected = invader.withSource(LocusSource.REJECTED, currentTick);
                existingLoci.add(rejected);
                WildAside.LOGGER.debug("    Applied: Added locus [{}] as REJECTED (will cause harm and degrade fast)",
                        invader.getId());
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

        float cost = base * fragility * crowding * flagMult;

        WildAside.LOGGER.trace("    Stress cost: base={} × fragility={} × crowding={} × flags={} = {}",
                String.format("%.2f", base),
                String.format("%.2f", fragility),
                String.format("%.2f", crowding),
                String.format("%.2f", flagMult),
                String.format("%.2f", cost));

        return cost;
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
}