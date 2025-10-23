package net.farkas.wildaside.dna;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public record Gene(Traits.Trait trait, float value, float stabilityModifier) {
    void apply(Entity entity) {
        if (entity instanceof LivingEntity livingEntity)  {

        }
    }
}