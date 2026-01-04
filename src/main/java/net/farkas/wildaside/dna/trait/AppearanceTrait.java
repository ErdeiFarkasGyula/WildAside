package net.farkas.wildaside.dna.trait;

import net.farkas.wildaside.dna.allele.value.AlleleValue;
import net.minecraft.world.entity.LivingEntity;

public class AppearanceTrait<T> extends Trait {
    public AppearanceTrait(String name, float baseInstability, TraitType traitType) {
        super(name, traitType, baseInstability);
    }

    public AppearanceTrait(String name, float baseInstability) {
        super(name, TraitType.APPEARANCE, baseInstability);
    }

    @Override
    public void apply(LivingEntity entity, AlleleValue valueHolder) {

    }

    @Override
    public void remove(LivingEntity entity) {

    }
}
