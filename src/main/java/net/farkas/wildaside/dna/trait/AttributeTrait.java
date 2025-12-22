package net.farkas.wildaside.dna.trait;

import net.farkas.wildaside.dna.DnaUtils;
import net.farkas.wildaside.dna.allele.value.AlleleValue;
import net.farkas.wildaside.dna.allele.value.FloatAlleleValue;
import net.farkas.wildaside.dna.trait.Trait;
import net.farkas.wildaside.dna.trait.TraitType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.UUID;

public class AttributeTrait extends Trait {
    private final ResourceLocation attributeRes;
    private final UUID modifierUuid;
    private final AttributeModifier.Operation operation;

    public AttributeTrait(String name, float baseInstability, ResourceLocation attributeRes, TraitType type, AttributeModifier.Operation operation) {
        super(name, type, baseInstability);
        this.attributeRes = attributeRes;
        this.modifierUuid = DnaUtils.generateUuid(DnaUtils.fullName(name));
        this.operation = operation;
    }

    public AttributeTrait(String name, float baseInstability, ResourceLocation attributeRes, AttributeModifier.Operation operation) {
        super(name, TraitType.CORE, baseInstability);
        this.attributeRes = attributeRes;
        this.modifierUuid = DnaUtils.generateUuid(DnaUtils.fullName(name));
        this.operation = operation;
    }

    public AttributeTrait(String name, float baseInstability, ResourceLocation attributeRes) {
        super(name, TraitType.CORE, baseInstability);
        this.attributeRes = attributeRes;
        this.modifierUuid = DnaUtils.generateUuid(DnaUtils.fullName(name));
        this.operation = AttributeModifier.Operation.ADDITION;
    }

    public AttributeTrait(String name, float baseInstability, TraitType type, ResourceLocation attributeRes) {
        super(name, type, baseInstability);
        this.attributeRes = attributeRes;
        this.modifierUuid = DnaUtils.generateUuid(DnaUtils.fullName(name));
        this.operation = AttributeModifier.Operation.ADDITION;
    }

    @Override
    public void apply(LivingEntity entity, AlleleValue valueHolder) {
        var attribute = ForgeRegistries.ATTRIBUTES.getValue(attributeRes);
        if (attribute == null) return;
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance == null) return;

        try { instance.removePermanentModifier(modifierUuid); } catch (Exception ignored) {}
        try { instance.removeModifier(modifierUuid); } catch (Exception ignored) {}

        double base = DnaUtils.safeBaseAttribute(entity, attribute);
        if (Double.isNaN(base) || base == 0.0) base = 1.0;

        if (valueHolder instanceof FloatAlleleValue floatAlleleValue) {
            float value = floatAlleleValue.get();
            double modifierValue = value - base;
            AttributeModifier.Operation op;

            if (operation == AttributeModifier.Operation.MULTIPLY_BASE) {
                modifierValue = (value / base) - 1.0;
            }
            else if (operation == AttributeModifier.Operation.ADDITION) {
                modifierValue = value - base;
            }
            AttributeModifier modifier = new AttributeModifier(modifierUuid, DnaUtils.fullName(getName()), modifierValue, operation);
            instance.addPermanentModifier(modifier);
        }
    }

    @Override
    public void remove(LivingEntity entity) {
        var attribute = ForgeRegistries.ATTRIBUTES.getValue(attributeRes);
        if (attribute == null) return;
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance == null) return;
        try { instance.removeModifier(modifierUuid); } catch (Exception ignored) {}
        try { instance.removePermanentModifier(modifierUuid); } catch (Exception ignored) {}
    }
}