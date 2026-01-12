package net.farkas.wildaside.dna.trait;

import net.farkas.wildaside.dna.allele.value.AlleleValue;
import net.farkas.wildaside.dna.allele.value.FloatAlleleValue;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.UUID;

public class Trait {
    private final String name;
    private final TraitType traitType;
    private final float instabilityModifier;
    private final UUID modifierUuid;

    public Trait(String name, TraitType traitType, float instabilityModifier) {
        this.name = name;
        this.traitType = traitType;
        this.instabilityModifier = instabilityModifier;
        this.modifierUuid = UUID.nameUUIDFromBytes(("wildaside_dna_" + name).getBytes());
    }

    public String getName() {
        return name;
    }

    public TraitType getTraitType() {
        return traitType;
    }

    public float getInstabilityModifier() {
        return instabilityModifier;
    }

    public UUID getModifierUuid() {
        return modifierUuid;
    }

    public void apply(LivingEntity entity, AlleleValue value) {
        if (value instanceof FloatAlleleValue floatValue) {
            applyRaw(entity, floatValue.get());
        }
    }

    public void applyRaw(LivingEntity entity, float value) {
        Attribute attribute = getAttribute();
        if (attribute == null) return;

        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance == null) return;

        AttributeModifier existing = instance.getModifier(modifierUuid);
        if (existing != null) {
            instance.removeModifier(modifierUuid);
        }

        double baseValue = instance.getBaseValue();
        double difference = value - baseValue;

        if (Math.abs(difference) > 0.0001) {
            AttributeModifier modifier = new AttributeModifier(
                    modifierUuid,
                    "wildaside_dna_" + name,
                    difference,
                    AttributeModifier.Operation.ADDITION
            );
            instance.addPermanentModifier(modifier);
        }
    }

    public float getCurrentValue(LivingEntity entity) {
        Attribute attribute = getAttribute();
        if (attribute == null) return 0f;

        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance == null) return 0f;

        return (float) instance.getValue();
    }

    public float getBaseValue(LivingEntity entity) {
        Attribute attribute = getAttribute();
        if (attribute == null) return 0f;

        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance == null) return 0f;

        return (float) instance.getBaseValue();
    }

    public float getDefaultValue() {
        // Default value when no gene sequences are present
        return 0f;
    }

    public void remove(LivingEntity entity) {
        Attribute attribute = getAttribute();
        if (attribute == null) return;

        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance == null) return;

        instance.removeModifier(modifierUuid);
    }

    protected Attribute getAttribute() {
        return ForgeRegistries.ATTRIBUTES.getValue(
                new net.minecraft.resources.ResourceLocation("minecraft", "generic." + name)
        );
    }
}