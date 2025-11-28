package net.farkas.wildaside.dna;

import net.farkas.wildaside.dna.allele.Allele;
import net.farkas.wildaside.dna.trait.Trait;
import net.farkas.wildaside.dna.trait.TraitExpression;
import net.farkas.wildaside.dna.trait.TraitTypes;
import net.farkas.wildaside.dna.trait.Traits;
import net.minecraft.nbt.CompoundTag;
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

    public Gene(UUID uuid, Trait trait, Allele alleleA, Allele alleleB) {
        this.trait = trait;
        this.alleleA = alleleA;
        this.alleleB = alleleB;
        this.uuid = uuid;
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
        return TraitExpression.evaluate(alleleA, alleleB);
    }

    public void apply(Entity entity) {
        if (this.trait.traitType() == TraitTypes.CORE && entity instanceof LivingEntity living) {
            trait.apply(living, getExpressedValue());
        } else if (entity instanceof LivingEntity living) {
            trait.apply(living, getExpressedValue());
        }
    }

    public void remove(Entity entity) {
        if (entity instanceof LivingEntity living) {
            trait.remove(living);
        }
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putUUID("uuid", uuid);
        tag.putString("trait", trait.name());
        tag.put("alleleA", alleleA.serializeNBT());
        tag.put("alleleB", alleleB.serializeNBT());
        return tag;
    }

    public static Gene deserializeNBT(CompoundTag tag) {
        UUID uuid = tag.getUUID("uuid");
        String traitName = tag.getString("trait");
        Allele alleleA = Allele.createFromTag(tag.getCompound("alleleA"));
        Allele alleleB = Allele.createFromTag(tag.getCompound("alleleB"));
        return new Gene(uuid, Traits.getByName(traitName), alleleA, alleleB);
    }
}