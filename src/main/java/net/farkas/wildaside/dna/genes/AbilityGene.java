package net.farkas.wildaside.dna.genes;

import net.farkas.wildaside.dna.Gene;
import net.farkas.wildaside.dna.Traits;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public record AbilityGene(Traits.Ability type, float potency, float stabilityCost) implements Gene {
    @Override
    public void apply(Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            if (type == Traits.Ability.REGENERATION) {
                livingEntity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, (int) potency));
            }
        }
    }

    @Override
    public String id() { return type.name(); }
    @Override
    public float stabilityCost() { return stabilityCost; }
}