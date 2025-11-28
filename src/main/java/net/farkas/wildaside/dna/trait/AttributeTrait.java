package net.farkas.wildaside.dna.traits;

import net.farkas.wildaside.dna.DnaUtils;
import net.farkas.wildaside.dna.trait.Trait;
import net.farkas.wildaside.dna.trait.TraitType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.UUID;

public class AttributeTrait extends Trait {
    private final ResourceLocation attributeRes;
    private final UUID modifierUuid;
    private final AttributeModifier.Operation operation;

    public AttributeTrait(String name, TraitType type, float baseInstability, ResourceLocation attributeRes, AttributeModifier.Operation operation) {
        super(name, type, baseInstability);
        this.attributeRes = attributeRes;
        this.modifierUuid = DnaUtils.generateUuid(DnaUtils.fullName(name));
        this.operation = operation;
    }

    public AttributeTrait(String name, TraitType type, float baseInstability, ResourceLocation attributeRes) {
        super(name, type, baseInstability);
        this.attributeRes = attributeRes;
        this.modifierUuid = DnaUtils.generateUuid(DnaUtils.fullName(name));
        this.operation = AttributeModifier.Operation.ADDITION;
    }

    @Override
    public void apply(LivingEntity entity, float value) {
        var attribute = ForgeRegistries.ATTRIBUTES.getValue(attributeRes);
        if (attribute == null) return;
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance == null) return;

        try { instance.removePermanentModifier(modifierUuid); } catch (Exception ignored) {}
        try { instance.removeModifier(modifierUuid); } catch (Exception ignored) {}

        double base = instance.getBaseValue();
        if (Double.isNaN(base) || base == 0.0) base = 1.0;

        double modifierValue = value - base;
        AttributeModifier.Operation op;

//        if (attribute == Attributes.ARMOR || attribute == Attributes.ARMOR_TOUGHNESS || attribute == Attributes.ATTACK_KNOCKBACK) {
//            modifierValue = value - base;
//            op = AttributeModifier.Operation.ADDITION;
//        } else {
//            modifierValue = (value / base) - 1.0;
//            op = AttributeModifier.Operation.MULTIPLY_BASE;
//        }

        if (operation == AttributeModifier.Operation.MULTIPLY_BASE) {
            modifierValue = (value / base) - 1.0;
        }
        else if (operation == AttributeModifier.Operation.ADDITION) {
            modifierValue = value - base;
        }

        AttributeModifier modifier = new AttributeModifier(modifierUuid, DnaUtils.fullName(name()), modifierValue, operation);
        instance.addPermanentModifier(modifier);
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