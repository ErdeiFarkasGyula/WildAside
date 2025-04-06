package net.farkas.wildaside.effect.custom;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

public class ImmunityEffect extends MobEffect {
    public ImmunityEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @Override
    public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        if (!pLivingEntity.level().isClientSide()) {
            if (pLivingEntity instanceof ServerPlayer serverPlayer) {
                List<MobEffectInstance> effects = new ArrayList<>(serverPlayer.getActiveEffects());
                for (MobEffectInstance effectInstance : effects) {
                    if (!effectInstance.getEffect().isBeneficial() && effectInstance.getEffect() != this) {
                        serverPlayer.removeEffect(effectInstance.getEffect());
                    }
                }
            }
        }

        super.applyEffectTick(pLivingEntity, pAmplifier);
    }
}
