package net.farkas.wildaside.dna.trait;

import net.minecraft.world.entity.LivingEntity;

public class ResistanceTrait extends Trait {
    public ResistanceTrait(String name, float baseInstability, TraitType type) {
        super(name, type, baseInstability);
    }

    public ResistanceTrait(String name, float baseInstability) {
        super(name, TraitType.RESISTANCE, baseInstability);
    }

    @Override
    public void apply(LivingEntity entity, float value) {
        entity.getPersistentData().putFloat("trait_resistance_" + getName(), value);
    }

    @Override
    public void remove(LivingEntity entity) {
        entity.getPersistentData().remove("trait_resistance_" + getName());
    }
}