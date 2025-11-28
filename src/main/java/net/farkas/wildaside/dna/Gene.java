package net.farkas.wildaside.dna;

import net.farkas.wildaside.dna.allele.Allele;
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
    private final UUID uuid;
    private final Trait trait;
    private Allele alleleA;
    private Allele alleleB;

    public Gene(Trait trait, Allele alleleA, Allele alleleB) {
        this.trait = trait;
        this.alleleA = alleleA;
        this.alleleB = alleleB;
        this.uuid = DnaUtils.getUuid(trait.name());
    }

    public UUID getUuid() {
        return uuid;
    }

    public Trait getTrait() {
        return trait;
    }

    public Allele getAlleleA() {
        return alleleA;
    }

    public void setAlleleA(Allele alleleA) {
        this.alleleA = alleleA;
    }

    public Allele getAlleleB() {
        return alleleB;
    }

    public void setAlleleB(Allele alleleB) {
        this.alleleB = alleleB;
    }

    public float getExpressedValue() {
        return Trait
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