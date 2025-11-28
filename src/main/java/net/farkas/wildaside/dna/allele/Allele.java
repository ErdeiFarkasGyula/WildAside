package net.farkas.wildaside.dna.allele;

import net.farkas.wildaside.dna.DnaConstants;
import net.minecraft.nbt.CompoundTag;

import static net.farkas.wildaside.dna.DnaConstants.*;

public class Allele {
    private float value;
    private float mutationRate;
    private float stability;
    private Dominance dominance;

    public Allele(float value, float mutationRate, float stability, Dominance dominance) {
        this.value = value;
        this.mutationRate = mutationRate;
        this.stability = stability;
        this.dominance = dominance;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public float getMutationRate() {
        return mutationRate;
    }

    public void setMutationRate(float mutationRate) {
        this.mutationRate = mutationRate;
    }

    public float getStability() {
        return stability;
    }

    public void setStability(float stability) {
        this.stability = stability;
    }

    public Dominance getDominance() {
        return dominance;
    }

    public void setDominance(Dominance dominance) {
        this.dominance = dominance;
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putFloat(VALUE, value);
        tag.putFloat(MUTATION_RATE, mutationRate);
        tag.putFloat(STABILITY, stability);
        tag.putString(DOMINANCE, dominance.name());
        return tag;
    }

    public void deserializeNBT(CompoundTag tag) {
        value = tag.getFloat(VALUE);
        mutationRate = tag.getFloat(MUTATION_RATE);
        stability = tag.getFloat(STABILITY);
        dominance = Dominance.valueOf(tag.getString(DOMINANCE));
    }

    public static Allele createFromTag(CompoundTag tag) {
        float value = tag.getFloat(VALUE);
        float mutationRate = tag.getFloat(MUTATION_RATE);
        float stability = tag.getFloat(STABILITY);
        String dominance = tag.getString(DOMINANCE);
        return new Allele(value, mutationRate, stability, Dominance.valueOf(dominance));
    }

    @Override
    public String toString() {
        return "Allele[" + "value=" + value + ", mutationRate=" + mutationRate + ", stability=" + stability + ", dominance=" + dominance + ']';
    }
}
