package net.farkas.wildaside.dna.genes;

import net.farkas.wildaside.dna.DnaUtils;
import net.farkas.wildaside.dna.Gene;
import net.farkas.wildaside.dna.Traits;
import net.farkas.wildaside.effect.ModMobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public record AbilityGene(Traits.Ability type, float amplifier, float stabilityCost) implements Gene {
    @Override
    public void apply(Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            if (type == Traits.Ability.REGENERATION) {
                livingEntity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, MobEffectInstance.INFINITE_DURATION, (int) amplifier, false, false));
                entity.getPersistentData().putFloat(DnaUtils.DNA_PREFIX + type.name(), amplifier);
            }
            if (type == Traits.Ability.LIFESTEAL) {
                livingEntity.addEffect(new MobEffectInstance(ModMobEffects.LIFESTEAL.get(), MobEffectInstance.INFINITE_DURATION, (int) amplifier, false, false));
                entity.getPersistentData().putFloat(DnaUtils.DNA_PREFIX + type.name(), amplifier);
            }

        }
    }

    @Override
    public String id() { return type.name(); }

    @Override
    public float stabilityCost() { return stabilityCost; }
}