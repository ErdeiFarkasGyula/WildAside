package net.farkas.wildaside.util;

import net.farkas.wildaside.effect.ModMobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class ContaminationHandler {
    public static void applyContamination(LivingEntity entity, int sec) {
        MobEffectInstance cont = entity.getEffect(ModMobEffects.CONTAMINATION.get());
        MobEffectInstance immunity = entity.getEffect(ModMobEffects.IMMUNITY.get());

        int amplifier = cont != null ? cont.getAmplifier() + 1 : 0;
        int duration = cont != null ? cont.getDuration() : 0;
        int cappedAmplifier = Math.min(amplifier, 4);

        if (immunity == null || cappedAmplifier > immunity.getAmplifier()) {
            if (duration <= (sec * 0.75 - amplifier) * 20) {
                entity.addEffect(new MobEffectInstance(ModMobEffects.CONTAMINATION.get(), sec * 20, cappedAmplifier));
            }
        }
    }
}
