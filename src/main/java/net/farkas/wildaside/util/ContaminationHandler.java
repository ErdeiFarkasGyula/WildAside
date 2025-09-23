package net.farkas.wildaside.util;

import net.farkas.wildaside.capability.contamination.ContaminationCapability;
import net.farkas.wildaside.effect.ModMobEffects;
import net.farkas.wildaside.entity.custom.vibrion.MucellithEntity;
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

        MobEffect immunity = ModMobEffects.IMMUNITY.get();
        MobEffect contamination = ModMobEffects.CONTAMINATION.get();

        if (entity.hasEffect(immunity)) {
            int immunityAmp = entity.getEffect(immunity).getAmplifier();
            if (dose < (immunityAmp + 1) * 1000) {
                return;
            }
        }

        int contaminationAmp = 0;
        if (entity.hasEffect(contamination)) {
            contaminationAmp = entity.getEffect(contamination).getAmplifier();
        }

        int amplifier = Math.max(Math.min(maxAmplifier, dose / 1000), contaminationAmp);

        entity.addEffect(new MobEffectInstance(contamination, (amplifier + 1) * 10 * 20, amplifier));
        if (amplifier >= 4) {
            entity.addEffect(new MobEffectInstance(MobEffects.POISON, (amplifier + 1) * 5 * 20, amplifier - 4));
        }
    }
}
