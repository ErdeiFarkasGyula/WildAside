package net.farkas.wildaside.util;

import net.farkas.wildaside.capability.contamination.ContaminationCapability;
import net.farkas.wildaside.effect.ModMobEffects;
import net.farkas.wildaside.entity.custom.vibrion.MucellithEntity;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class ContaminationHandler {
    private static final int maxAmplifier = 5;

    public static void giveContaminationDose(Entity entity, int dose) {
        if (!(entity instanceof LivingEntity livingEntity)) return;

        livingEntity.getCapability(ContaminationCapability.INSTANCE).ifPresent(data -> {
            data.addDose(dose);
            applyContamination(livingEntity, data.getDose());
        });

    }

    public static void applyContamination(LivingEntity entity, int dose) {
        if (entity instanceof MucellithEntity) return;

        Holder<MobEffect> immunity = ModMobEffects.IMMUNITY.getHolder().get();
        if (entity.hasEffect(immunity)) {
            int immunityAmp = entity.getEffect(immunity).getAmplifier();
            if (dose < (immunityAmp + 1) * 1000) {
                return;
            }
        }

        Holder<MobEffect> contamination = ModMobEffects.CONTAMINATION.getHolder().get();
        int amplifier = Math.min(maxAmplifier, dose / 1000);
        if (entity.hasEffect(contamination)) {
            entity.removeEffect(contamination);
        }
        entity.addEffect(new MobEffectInstance(contamination, (amplifier + 1) * 10 * 20, amplifier));
        if (amplifier >= 4) {
            entity.addEffect(new MobEffectInstance(MobEffects.POISON, (amplifier + 1) * 5 * 20, amplifier - 4));
        }
    }
}
