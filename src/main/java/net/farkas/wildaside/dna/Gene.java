package net.farkas.wildaside.dna;

import net.farkas.wildaside.dna.allele.Allele;
import net.farkas.wildaside.dna.trait.Trait;
import net.farkas.wildaside.dna.trait.TraitExpression;
import net.farkas.wildaside.dna.trait.TraitRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.UUID;

import static net.farkas.wildaside.dna.DnaConstants.*;

public class Gene {
    private final UUID uuid;
    private final Trait trait;
    private Allele alleleA;
    private Allele alleleB;

    public Gene(Trait trait, Allele alleleA, Allele alleleB) {
        this.trait = trait;
        this.alleleA = alleleA;
        this.alleleB = alleleB;
        this.uuid = DnaUtils.generateUuid(trait.getName());
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
        if (entity instanceof LivingEntity livingEntity) {
            trait.apply(livingEntity, getExpressedValue());
        }
    }

    public void remove(Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            trait.remove(livingEntity);
        }
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putUUID(UUID, uuid);
        tag.putString(TRAIT, trait.getName());
        tag.put(ALLELE_A, alleleA.serializeNBT());
        tag.put(ALLELE_B, alleleB.serializeNBT());
        return tag;
    }

    public static Gene deserializeNBT(CompoundTag tag) {
        Trait trait = TraitRegistry.getByName(tag.getString(TRAIT));
        Allele alleleA = Allele.deserializeNBT(tag.getCompound(ALLELE_A));
        Allele alleleB = Allele.deserializeNBT(tag.getCompound(ALLELE_B));

        UUID uuid = DnaUtils.generateUuid(trait.getName());
        if (tag.contains(UUID)) {
            uuid = tag.getUUID(UUID);
        }
        return new Gene(uuid, trait, alleleA, alleleB);
    }
}