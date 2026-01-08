package net.farkas.wildaside.dna.merge;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.dna.DnaUtils;
import net.farkas.wildaside.dna.allele.Allele;
import net.farkas.wildaside.dna.allele.dominance.Dominance;
import net.farkas.wildaside.dna.allele.value.FloatAlleleValue;
import net.farkas.wildaside.dna.locus.GeneLocus;
import net.farkas.wildaside.dna.locus.LocusSource;
import net.farkas.wildaside.dna.trait.Trait;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

import java.util.*;

public class RejectionSideEffects {
    public static void applyImmediateEffects(LivingEntity entity, MergeResult result) {
        int rejections = result.getRejectedCount();
        int transients = result.getTransientCount();

        WildAside.LOGGER.info("=== APPLYING IMMEDIATE SIDE EFFECTS ===");
        WildAside.LOGGER.info("Entity: {} | Rejections: {} | Transients: {}",
                entity.getName().getString(), rejections, transients);

        if (rejections > 0) {
            int nauseaDuration = 100 + rejections * 60;
            entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, nauseaDuration, 0, false, true, true));

            WildAside.LOGGER.info("Applied CONFUSION for {} ticks", nauseaDuration);

            int weaknessDuration = 200 + rejections * 100;
            int weaknessLevel = Math.min(2, rejections - 1);

            entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, weaknessDuration, weaknessLevel, false, true, true));

            WildAside.LOGGER.info("Applied WEAKNESS {} for {} ticks", weaknessLevel, weaknessDuration);

            if (rejections >= 3) {
                float damage = rejections * 2f;
                entity.hurt(entity.damageSources().magic(), damage);
                WildAside.LOGGER.warn("Applied {} magic damage", damage);
            }
        }

        if (transients > 0) {
            int slowDuration = 100 + transients * 40;
            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, slowDuration, 0, false, false, true));
            WildAside.LOGGER.info("Applied SLOWNESS for {} ticks (unstable integration)", slowDuration);
        }

        WildAside.LOGGER.info("=== IMMEDIATE EFFECTS COMPLETE ===");
    }

    public static void tickSideEffects(LivingEntity entity, Map<Trait, List<GeneLocus>> loci, long seed) {
        int rejectedCount = 0;
        int transientCount = 0;

        for (List<GeneLocus> locusGroup : loci.values()) {
            for (GeneLocus locus : locusGroup) {
                if (locus.getSource() == LocusSource.REJECTED) rejectedCount++;
                if (locus.getSource() == LocusSource.TRANSIENT) transientCount++;
            }
        }

        if (rejectedCount == 0 && transientCount == 0) return;

        WildAside.LOGGER.debug("Ticking side effects for {} | Rejected: {} | Transient:  {}", entity.getName().getString(), rejectedCount, transientCount);

        if (rejectedCount > 0) {
            float harmChance = 0.05f * rejectedCount;
            float roll = DnaUtils.hashToFloat(seed, "rejection_harm", entity.tickCount);

            WildAside.LOGGER.trace("Rejection harm check: roll={} vs chance={}", String.format("%.3f", roll), String.format("%.3f", harmChance));

            if (roll < harmChance) {
                WildAside.LOGGER.info("Rejection harm triggered for {} (roll {} < {})",
                        entity.getName().getString(),
                        String.format("%.3f", roll),
                        String.format("%.3f", harmChance));
                applyRandomHarm(entity, rejectedCount, seed);
            }
        }

        if (transientCount > 0) {
            float instabilityChance = 0.02f * transientCount;
            float roll = DnaUtils.hashToFloat(seed, "transient_instability", entity.tickCount);

            WildAside.LOGGER.trace("Instability check: roll={} vs chance={}", String.format("%.3f", roll), String.format("%.3f", instabilityChance));

            if (roll < instabilityChance) {
                WildAside.LOGGER.info("Instability triggered for {} (roll {} < {})",
                        entity.getName().getString(),
                        String.format("%.3f", roll),
                        String.format("%.3f", instabilityChance));
                applyInstabilityEffect(entity, transientCount);
            }
        }
    }

    private static void applyRandomHarm(LivingEntity entity, int severity, long seed) {
        float roll = DnaUtils.hashToFloat(seed, "harm_type", entity.tickCount);

        WildAside.LOGGER.debug("Random harm roll: {} for severity {}", String.format("%.3f", roll), severity);

        if (roll < 0.3f) {
            entity.addEffect(new MobEffectInstance(MobEffects.POISON, 60, 0, false, true, true));
            WildAside.LOGGER.info("Applied POISON to {}", entity.getName().getString());
        }
        else if (roll < 0.5f) {
            int level = Math.max(0, severity - 1);
            entity.addEffect(new MobEffectInstance(MobEffects.HUNGER, 100, level, false, true, true));
            WildAside.LOGGER.info("Applied HUNGER {} to {}", level, entity.getName().getString());
        }
        else if (roll < 0.7f) {
            entity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 200, 0, false, true, true));
            WildAside.LOGGER.info("Applied MINING_FATIGUE to {}", entity.getName().getString());
        }
        else {
            float damage = 1f;
            entity.hurt(entity.damageSources().magic(), damage);
            WildAside.LOGGER.info("Applied {} magic damage to {}", damage, entity.getName().getString());
        }
    }

    private static void applyInstabilityEffect(LivingEntity entity, int severity) {
        entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 40, 0, false, false, false));
        WildAside.LOGGER.info("Applied brief CONFUSION to {} (DNA instability)", entity.getName().getString());
    }

    public static List<GeneLocus> generateMutations(Map<Trait, List<GeneLocus>> loci, int rejectionCount, long seed) {
        List<GeneLocus> mutations = new ArrayList<>();

        if (rejectionCount == 0) return mutations;

        float mutationChance = 0.1f * rejectionCount;

        WildAside.LOGGER.info("=== GENERATING REJECTION-INDUCED MUTATIONS ===");
        WildAside.LOGGER.info("Rejection count: {} | Mutation chance per locus: {}%", rejectionCount, String.format("%.1f", mutationChance * 100));

        for (var entry : loci.entrySet()) {
            Trait trait = entry.getKey();
            List<GeneLocus> group = entry.getValue();

            for (int i = 0; i < group.size(); i++) {
                GeneLocus locus = group.get(i);

                if (locus.getSource() == LocusSource.REJECTED) {
                    WildAside.LOGGER.trace("Skipping rejected locus [{}]", locus.getId());
                    continue;
                }

                float roll = DnaUtils.hashToFloat(seed, trait.getName() + "_mut_" + i, 0);

                WildAside.LOGGER.trace("Mutation check for [{}]: roll={} vs chance={}",
                        locus.getId(), String.format("%.3f", roll), String.format("%.3f", mutationChance));

                if (roll < mutationChance) {
                    WildAside.LOGGER.warn("MUTATION!  Locus [{}] in trait [{}] is being corrupted by rejection interference", locus.getId(), trait.getName());

                    GeneLocus mutated = mutateLocus(locus, seed, trait.getName() + i);
                    mutations.add(mutated);
                    group.set(i, mutated);

                    WildAside.LOGGER.info("Mutated [{}] -> [{}], stability: {} -> {}",
                            locus.getId(), mutated.getId(),
                            String.format("%.3f", locus.getStability()),
                            String.format("%.3f", mutated.getStability()));
                }
            }
        }

        WildAside.LOGGER.info("=== MUTATIONS COMPLETE:  {} loci affected ===", mutations.size());
        return mutations;
    }

    private static GeneLocus mutateLocus(GeneLocus original, long seed, String salt) {
        Allele newA = mutateAllele(original.getAlleleA(), seed, salt + "_A");
        Allele newB = mutateAllele(original.getAlleleB(), seed, salt + "_B");

        float newStability = original.getStability() * 0.9f;

        return new GeneLocus(
                original.getId() + "_mut",
                newA, newB,
                original.getFlags(),
                newStability,
                original.getSource(),
                original.getIntegrationTick(),
                original.getDegradation()
        );
    }

    private static Allele mutateAllele(Allele original, long seed, String salt) {
        if (original.getValueHolder() instanceof FloatAlleleValue fv) {
            float oldValue = fv.get();
            float mutation = DnaUtils.deterministicGaussian(seed, salt) * 0.2f;
            float newValue = oldValue * (1f + mutation);

            Dominance newDom = original.getDominance();
            float domRoll = DnaUtils.hashToFloat(seed, salt + "_dom", 0);
            if (domRoll < 0.1f) {
                newDom = DnaUtils.deterministicDominancePick(seed, salt);
                WildAside.LOGGER.debug("Dominance flipped:  {} -> {}", original.getDominance(), newDom);
            }

            float newMutRate = original.getMutationRate() * 1.2f;

            WildAside.LOGGER.debug("Allele mutation: value {} -> {} ({}%), mutRate {} -> {}",
                    String.format("%.3f", oldValue),
                    String.format("%.3f", newValue),
                    String.format("%.1f", mutation * 100),
                    String.format("%.3f", original.getMutationRate()),
                    String.format("%.3f", newMutRate));

            return new Allele(new FloatAlleleValue(newValue), newMutRate, newDom);
        }
        return original;
    }
}