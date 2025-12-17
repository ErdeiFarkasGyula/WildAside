package net.farkas.wildaside.dna.trait;

import net.farkas.wildaside.dna.ability.IAbility;
import net.minecraft.world.entity.LivingEntity;

public class AbilityTrait extends Trait {
    public AbilityTrait(String name, float baseInstability, TraitType type) {
        super(name, type, baseInstability);
    }

    public AbilityTrait(String name, float baseInstability) {
        super(name, TraitType.ABILITY, baseInstability);
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