package net.farkas.wildaside.dna.trait;

import net.farkas.wildaside.dna.trait.TraitTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;

public abstract class Trait {
    private final String name;
    private final TraitTypes traitType;
    private final float baseInstability;

    public Trait(String name, TraitTypes traitType, float baseInstability) {
        this.name = name;
        this.traitType = traitType;
        this.baseInstability = baseInstability;
    }

    public String name() { return name; }
    public TraitTypes traitType() { return traitType; }
    public float baseInstability() { return baseInstability; }

    public abstract void apply(LivingEntity entity, float value);

    public void remove(LivingEntity entity) {

    }

    public Component displayName() {
        return Component.translatable("trait.wildaside." + name);
    }
}