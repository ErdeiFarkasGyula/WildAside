package net.farkas.wildaside.dna;

import net.farkas.wildaside.dna.trait.Trait;
import net.farkas.wildaside.dna.trait.TraitTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.UUID;

public class Gene {
    public Trait trait;
    public float value;
    public float stabilityCost;
    private final String fullName;
    private final UUID uuid;

    public Trait trait() { return trait; }
    public float value() { return value; }
    public float stabilityCost() { return stabilityCost; }

    public Gene(Trait trait, float value, float stabilityCost) {
        this.trait = trait;
        this.value = value;
        this.stabilityCost = stabilityCost;
        this.fullName = DnaUtils.fullName(trait.name());
        this.uuid = DnaUtils.getUuid(fullName);
    }

    public void apply(Entity entity) {
        if (this.trait.traitType() == TraitTypes.CORE && entity instanceof LivingEntity living) {
            trait.apply(living, value);
        } else if (entity instanceof LivingEntity living) {
            trait.apply(living, value);
        }
    }

    public void remove(Entity entity) {
        if (entity instanceof LivingEntity living) {
            trait.remove(living);
        }
    }
}