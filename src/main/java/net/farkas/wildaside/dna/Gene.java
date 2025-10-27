package net.farkas.wildaside.dna;

import net.farkas.wildaside.dna.traits.Trait;
import net.farkas.wildaside.dna.traits.TraitTypes;
import net.farkas.wildaside.dna.traits.Traits;
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
        if (!(entity instanceof LivingEntity livingEntity)) return;

        Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(DnaUtils.getAttribute(trait.name()));
        if (attribute == null) return;

        AttributeInstance instance = livingEntity.getAttribute(attribute);
        if (instance == null) return;

        remove(livingEntity, attribute);

        double base = instance.getBaseValue();
        if (Double.isNaN(base) || base == 0.0) base = 1.0;

        double modifierValue;
        AttributeModifier.Operation op;

        if (attribute == Attributes.ARMOR || attribute == Attributes.ARMOR_TOUGHNESS || attribute == Attributes.ATTACK_KNOCKBACK) {
            modifierValue = value - base;
            op = AttributeModifier.Operation.ADDITION;
        } else {
            modifierValue = (value / base) - 1.0;
            op = AttributeModifier.Operation.MULTIPLY_BASE;
        }

        instance.addPermanentModifier(new AttributeModifier(DnaUtils.getUuid(fullName), fullName, modifierValue, op));
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