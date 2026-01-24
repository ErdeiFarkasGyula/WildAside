package net.farkas.wildaside.dna.merge;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.dna.DnaUtils;
import net.farkas.wildaside.dna.chromosome.Chromosome;
import net.farkas.wildaside.dna.chromosome.ChromosomeType;
import net.farkas.wildaside.dna.chromosome.Genome;
import net.farkas.wildaside.dna.sequence.GeneSequence;
import net.farkas.wildaside.dna.sequence.GeneSource;
import net.farkas.wildaside.dna.trait.Trait;
import net.farkas.wildaside.dna.trait.TraitType;

import java.util.ArrayList;
import java.util.List;

public class DnaMerger {
    public static final float BASE_INTEGRATION_CHANCE = 0.35f;
    public static final float STRESS_INTEGRATION_BONUS = 0.4f;

    public static MergeResult merge(Genome hostGenome, Genome invaderGenome, float hostStress, long currentTick, long seed) {
        WildAside.LOGGER.info("=== DNA MERGE START ===");
        WildAside.LOGGER.info("Host stress: {}, Seed: {}, Tick: {}", String.format("%.2f", hostStress), seed, currentTick);

        List<MergeEvent> events = new ArrayList<>();
        float totalStressGain = 0f;

        for (Chromosome invaderChromo : invaderGenome.getMaternal().getAllChromosomes()) {
            for (GeneSequence invaderSeq : invaderChromo.getAllGeneSequences()) {
                Trait trait = invaderSeq.getTrait();

                Chromosome hostChromo = hostGenome.getMaternal().getChromosome(invaderChromo.getType());
                GeneSequence hostSeq = hostChromo.getGeneSequence(trait);
                
                MergeOutcome outcome = resolveConflict(trait, hostSeq, invaderSeq, hostStress, currentTick, seed);
                
                if (outcome.type() == MergeOutcomeType.INTEGRATED || outcome.type() == MergeOutcomeType.REPLACED) {
                    hostChromo.setGeneSequence(trait, invaderSeq);
                }
                
                events.add(new MergeEvent(trait, invaderSeq, outcome));
                totalStressGain += outcome.stressCost();
                
                seed = DnaUtils.mix64(seed ^ trait.getName().hashCode());
            }
        }

        WildAside.LOGGER.info("=== DNA MERGE COMPLETE ===");

        return new MergeResult(hostGenome, totalStressGain, events);
    }

    private static MergeOutcome resolveConflict(Trait trait, GeneSequence hostSeq, GeneSequence invaderSeq, float hostStress, long currentTick, long seed) {
        float baseChance = BASE_INTEGRATION_CHANCE;
        float stressBonus = (hostStress / 100f) * STRESS_INTEGRATION_BONUS;
        float traitMult = getTraitTypeMultiplier(trait.getTraitType());
        float stabilityMult = Math.max(0.3f, invaderSeq.getStability());

        float integrationChance = (baseChance + stressBonus) * traitMult * stabilityMult;

        float roll = DnaUtils.hashToFloat(seed, trait.getName(), 0);
        float stressCost = calculateStressCost(trait, invaderSeq);

        if (hostSeq == null) {
            if (roll < integrationChance) {
                return MergeOutcome.integrated(stressCost);
            } else if (roll < integrationChance + 0.2f) {
                return MergeOutcome.transient_(stressCost * 1.5f);
            } else {
                return MergeOutcome.rejected(stressCost * 0.7f);
            }
        } else {
            if (roll < integrationChance) {
                 return MergeOutcome.replaced(hostSeq, stressCost * 1.2f);
            } else {
                return MergeOutcome.rejected(stressCost * 0.5f);
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

    private static float calculateStressCost(Trait trait, GeneSequence invader) {
        float base = trait.getInstabilityModifier();
        float fragility = 1.0f / Math.max(0.1f, invader.getStability());
        
        return base * fragility;
    }
}