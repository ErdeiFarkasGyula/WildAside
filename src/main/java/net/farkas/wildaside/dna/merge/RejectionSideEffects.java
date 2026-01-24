package net.farkas.wildaside.dna.merge;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.dna.DnaUtils;
import net.farkas.wildaside.dna.chromosome.Genome;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

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

    public static void generateMutations(Genome genome, int rejectionCount, long seed) {

    }
}