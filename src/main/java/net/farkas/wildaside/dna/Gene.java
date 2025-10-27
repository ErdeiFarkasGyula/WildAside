package net.farkas.wildaside.dna;

import net.farkas.wildaside.dna.traits.Trait;
import net.farkas.wildaside.dna.traits.TraitTypes;
import net.farkas.wildaside.dna.traits.Traits;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.UUID;

public class Gene {
    public Trait trait;
    public float value;
    public float stabilityCost;
    private final String fullName;
    private final UUID uuid;

    public Trait trait() {
        return trait;
    }

    public float value() {
        return value;
    }

    public float stabilityCost() {
        return stabilityCost;
    }

    public void apply(Entity entity) {
        if (this.trait.traitType() != TraitTypes.CORE) return;
        if (entity instanceof LivingEntity livingEntity) {
            Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(DnaUtils.getAttribute(trait.name()));
            if (attribute != null) {
                var instance = livingEntity.getAttribute(attribute);
                if (instance != null) {
                    remove(livingEntity, attribute);
                }

                AttributeInstance attributeInstance = livingEntity.getAttribute(attribute);
                if (attributeInstance == null) return;
                double base = attributeInstance.getBaseValue();
                double modifierValue = (value / base) - 1.0;

                livingEntity.getAttribute(attribute).addPermanentModifier(
                        new AttributeModifier(DnaUtils.getUuid(fullName), fullName, modifierValue, AttributeModifier.Operation.MULTIPLY_BASE));
            }
        }
    }

    public void remove(Entity entity) {
        if (this.trait.traitType() != TraitTypes.CORE) return;
        if (entity instanceof LivingEntity livingEntity) {
            Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(DnaUtils.getAttribute(trait.name()));
            if (attribute != null) {
                var instance = livingEntity.getAttribute(attribute);
                if (instance != null) {
                    instance.removeModifier(uuid);
                    instance.removePermanentModifier(uuid);
                }
            }
        }
    }

    public void remove(Entity entity, Attribute attribute) {
        if (entity instanceof LivingEntity livingEntity) {
            if (attribute != null) {
                var instance = livingEntity.getAttribute(attribute);
                if (instance != null) {
                    instance.removeModifier(uuid);
                    instance.removePermanentModifier(uuid);
                }
            }
        }
    }

    public Gene(Trait trait, float value, float stabilityCost) {
        this.trait = trait;
        this.value = value;
        this.stabilityCost = stabilityCost;
        this.fullName = DnaUtils.fullName(trait.name());
        this.uuid = DnaUtils.getUuid(fullName);
    }
}