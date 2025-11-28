package net.farkas.wildaside.dna.trait;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;

public abstract class Trait {
    private final String name;
    private final TraitTypes traitType;
    private final float instabilityModifier;

    public Trait(String name, TraitTypes traitType, float baseInstability) {
        this.name = name;
        this.traitType = traitType;
        this.instabilityModifier = baseInstability;
    }

    public String getName() { return name; }
    public TraitTypes getTraitType() { return traitType; }
    public float getInstabilityModifier() { return instabilityModifier; }

    public abstract void apply(LivingEntity entity, float value);
    public abstract void remove(LivingEntity entity);

    public Component displayName() {
        return Component.translatable("trait.wildaside." + name);
    }
}