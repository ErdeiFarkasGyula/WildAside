package net.farkas.wildaside.util;

import net.farkas.wildaside.effect.ModMobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class ContaminationHandler {
    public static void applyContamination(Entity entity, int sec) {
        if (entity instanceof LivingEntity livingEntity) {
            MobEffectInstance cont = livingEntity.getEffect(ModMobEffects.CONTAMINATION.get());
            MobEffectInstance immunity = livingEntity.getEffect(ModMobEffects.IMMUNITY.get());

            int amplifier = cont != null ? cont.getAmplifier() + 1 : 0;
            int duration = cont != null ? cont.getDuration() : 0;
            int cappedAmplifier = Math.min(amplifier, 3);

            if (immunity == null || cappedAmplifier > immunity.getAmplifier()) {
                if (duration <= (sec * 0.75 - amplifier) * 20) {
                    livingEntity.addEffect(new MobEffectInstance(ModMobEffects.CONTAMINATION.get(), sec * 20, cappedAmplifier));
                }
            }

        }
    }
}
