package net.farkas.wildaside.dna.ability;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.dna.Gene;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public interface IAbility {
    String COOLDOWN = WildAside.MOD_ID + "_dna_ability_cooldown";
    void onTick(LivingEntity entity, Gene gene);
    void onDamage(LivingEntity entity, DamageSource source, float amount);
    void onUse(LivingEntity entity, float cooldown);
}
