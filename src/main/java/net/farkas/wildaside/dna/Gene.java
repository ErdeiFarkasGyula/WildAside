package net.farkas.wildaside.dna;

import net.farkas.wildaside.dna.expression.ExpressionContext;
import net.farkas.wildaside.dna.expression.GeneExpressionPair;
import net.farkas.wildaside.dna.sequence.GeneSequence;
import net.farkas.wildaside.dna.trait.Trait;
import net.farkas.wildaside.dna.trait.TraitRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.UUID;

import static net.farkas.wildaside.dna.DnaConstants.*;

public class Gene {
    private final UUID uuid;
    private final Trait trait;
    private GeneSequence maternalSequence;
    private GeneSequence paternalSequence;

    public Gene(Trait trait, GeneSequence maternalSequence, GeneSequence paternalSequence) {
        this.trait = trait;
        this.maternalSequence = maternalSequence;
        this.paternalSequence = paternalSequence;
        this.uuid = DnaUtils.generateUuid(trait.getName());
    }

    public Gene(UUID uuid, Trait trait, GeneSequence maternalSequence, GeneSequence paternalSequence) {
        this.trait = trait;
        this.maternalSequence = maternalSequence;
        this.paternalSequence = paternalSequence;
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Trait getTrait() {
        return trait;
    }

    public GeneSequence getMaternalSequence() {
        return maternalSequence;
    }

    public void setMaternalSequence(GeneSequence sequence) {
        this.maternalSequence = sequence;
    }

    public GeneSequence getPaternalSequence() {
        return paternalSequence;
    }

    public void setPaternalSequence(GeneSequence sequence) {
        this.paternalSequence = sequence;
    }

    public GeneExpressionPair getExpressionPair() {
        return new GeneExpressionPair(trait, maternalSequence, paternalSequence);
    }

    public float getExpressedValue(ExpressionContext context) {
        return getExpressionPair().express(context);
    }

    public void apply(Entity entity, ExpressionContext context) {
        if (entity instanceof LivingEntity livingEntity) {
            float expressedValue = getExpressedValue(context);
            trait.applyValue(livingEntity, expressedValue);
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
        tag.put("MaternalSequence", maternalSequence.serializeNBT());
        tag.put("PaternalSequence", paternalSequence.serializeNBT());

        return tag;
    }

    public static Gene deserializeNBT(CompoundTag tag) {
        Trait trait = TraitRegistry.getByName(tag.getString(TRAIT));
        GeneSequence maternal = null;
        if (tag.contains("MaternalSequence")) {
            maternal = GeneSequence.deserializeNBT(tag.getCompound("MaternalSequence"), trait);
        }
        GeneSequence paternal = null;
        if (tag.contains("PaternalSequence")) {
            paternal = GeneSequence.deserializeNBT(tag.getCompound("PaternalSequence"), trait);
        }

        UUID uuid = DnaUtils.generateUuid(trait.getName());
        if (tag.contains(UUID)) {
            uuid = tag.getUUID(UUID);
        }
        return new Gene(uuid, trait, maternal, paternal);
    }
}