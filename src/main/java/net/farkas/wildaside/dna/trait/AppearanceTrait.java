package net.farkas.wildaside.dna.trait;

import net.minecraft.world.entity.LivingEntity;

public class AppearanceTrait<T> extends Trait {
    public AppearanceTrait(String name, float baseInstability, TraitType traitType) {
        super(name, traitType, baseInstability);
    }

    public AppearanceTrait(String name, float baseInstability) {
        super(name, TraitType.APPEARANCE, baseInstability);
    }

    @Override
    public void apply(LivingEntity entity, float value) {

    }

    @Override
    public void remove(LivingEntity entity) {

    }
}