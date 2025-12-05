package net.farkas.wildaside.dna.trait;

import net.farkas.wildaside.dna.ability.IAbility;
import net.minecraft.world.entity.LivingEntity;

public class AbilityTrait extends Trait {
    public AbilityTrait(String name, TraitType type, float baseInstability) {
        super(name, type, baseInstability);
    }

    @Override
    public void apply(LivingEntity entity, float value) {
        entity.getPersistentData().putFloat("trait_ability_" + getName(), value);
        entity.getPersistentData().putFloat(IAbility.COOLDOWN, 0);
    }

    @Override
    public void remove(LivingEntity entity) {
        entity.getPersistentData().remove("trait_ability_" + getName());
    }
}