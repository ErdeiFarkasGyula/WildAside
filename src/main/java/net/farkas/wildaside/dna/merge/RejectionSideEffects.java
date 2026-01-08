package net.farkas.wildaside.dna.merge;

import net.farkas.wildaside.dna.DnaUtils;
import net.farkas.wildaside.dna.allele.Allele;
import net.farkas.wildaside.dna.allele.dominance.Dominance;
import net.farkas.wildaside.dna.allele.value.FloatAlleleValue;
import net.farkas.wildaside.dna.locus.GeneLocus;
import net.farkas.wildaside.dna.locus.LocusFlag;
import net.farkas.wildaside.dna.locus.LocusSource;
import net.farkas.wildaside.dna.trait.Trait;
import net.farkas.wildaside.dna.trait.TraitRegistry;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

import java.util.*;

public class RejectionSideEffects {
    public static void applyImmediateEffects(LivingEntity entity, MergeResult result) {
        int rejections = result.getRejectedCount();
        int transients = result.getTransientCount();

        if (rejections > 0) {
            entity.addEffect(new MobEffectInstance(
                    MobEffects.CONFUSION,
                    100 + rejections * 60,
                    0, false, true, true
            ));

            entity.addEffect(new MobEffectInstance(
                    MobEffects.WEAKNESS,
                    200 + rejections * 100,
                    Math.min(2, rejections - 1),
                    false, true, true
            ));

            if (rejections >= 3) {
                entity.hurt(entity.damageSources().magic(), rejections * 2f);
            }
        }

        if (transients > 0) {
            entity.addEffect(new MobEffectInstance(
                    MobEffects.MOVEMENT_SLOWDOWN,
                    100 + transients * 40,
                    0, false, false, true
            ));
        }
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

        if (rejectedCount > 0) {
            float harmChance = 0.05f * rejectedCount;
            if (DnaUtils.hashToFloat(seed, "rejection_harm", entity.tickCount) < harmChance) {
                applyRandomHarm(entity, rejectedCount, seed);
            }
        }

        if (transientCount > 0) {
            float instabilityChance = 0.02f * transientCount;
            if (DnaUtils.hashToFloat(seed, "transient_instability", entity.tickCount) < instabilityChance) {
                applyInstabilityEffect(entity, transientCount);
            }
        }
    }

    private static void applyRandomHarm(LivingEntity entity, int severity, long seed) {
        float roll = DnaUtils.hashToFloat(seed, "harm_type", entity.tickCount);

        if (roll < 0.3f) {
            entity.addEffect(new MobEffectInstance(MobEffects.POISON, 60, 0, false, true, true));
        }
        else if (roll < 0.5f) {
            entity.addEffect(new MobEffectInstance(MobEffects.HUNGER, 100, severity - 1, false, true, true));
        }
        else if (roll < 0.7f) {
            entity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 200, 0, false, true, true));
        }
        else {
            entity.hurt(entity.damageSources().magic(), 1f);
        }
    }

    private static void applyInstabilityEffect(LivingEntity entity, int severity) {
        entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 40, 0, false, false, false));
    }

    public static List<GeneLocus> generateMutations(Map<Trait, List<GeneLocus>> loci, int rejectionCount, long seed) {
        List<GeneLocus> mutations = new ArrayList<>();

        if (rejectionCount == 0) return mutations;

        float mutationChance = 0.1f * rejectionCount;

        for (var entry : loci.entrySet()) {
            Trait trait = entry.getKey();
            List<GeneLocus> group = entry.getValue();

            for (int i = 0; i < group.size(); i++) {
                GeneLocus locus = group.get(i);

                if (locus.getSource() == LocusSource.REJECTED) continue;

                float roll = DnaUtils.hashToFloat(seed, trait.getName() + "_mut_" + i, 0);
                if (roll < mutationChance) {
                    GeneLocus mutated = mutateLocus(locus, seed, trait.getName() + i);
                    mutations.add(mutated);
                    group.set(i, mutated);
                }
            }
        }

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
            float mutation = DnaUtils.deterministicGaussian(seed, salt) * 0.2f;
            float newValue = fv.get() * (1f + mutation);

            Dominance newDom = original.getDominance();
            if (DnaUtils.hashToFloat(seed, salt + "_dom", 0) < 0.1f) {
                newDom = DnaUtils.deterministicDominancePick(seed, salt);
            }

            return new Allele(new FloatAlleleValue(newValue), original.getMutationRate() * 1.2f, newDom);
        }
        return original;
    }
}