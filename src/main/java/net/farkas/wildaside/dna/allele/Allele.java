package net.farkas.wildaside.dna.allele;

import net.minecraft.nbt.CompoundTag;

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
        tag.putFloat("value", value);
        tag.putFloat("mutationRate", mutationRate);
        tag.putFloat("stability", stability);
        tag.putString("dominance", dominance.name());
        return tag;
    }

    public void deserializeNBT(CompoundTag tag) {
        value = tag.getFloat("value");
        mutationRate = tag.getFloat("mutationRate");
        stability = tag.getFloat("stability");
        dominance = Dominance.valueOf(tag.getString("dominance"));
    }

    @Override
    public String toString() {
        return "Allele[" + "value=" + value + ", mutationRate=" + mutationRate + ", stability=" + stability + ", dominance=" + dominance + ']';
    }
}
