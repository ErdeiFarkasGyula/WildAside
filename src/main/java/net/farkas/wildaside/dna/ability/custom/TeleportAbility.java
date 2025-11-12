package net.farkas.wildaside.dna.ability.custom;

import net.farkas.wildaside.dna.Gene;
import net.farkas.wildaside.dna.ability.IAbility;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public class TeleportAbility implements IAbility {
    @Override
    public void onTick(LivingEntity entity, Gene gene) {

    }

    @Override
    public void onDamage(LivingEntity entity, DamageSource source, float amount) {

    }

    @Override
    public void onUse(LivingEntity entity, float cooldown) {
        System.out.println("TPPPPPP");
    }
}
