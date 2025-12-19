package net.farkas.wildaside.dna.allele;

import net.farkas.wildaside.dna.allele.value.*;
import net.farkas.wildaside.dna.dominance.Dominance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class Allele {
    private AlleleValue valueHolder;
    private float mutationRate;
    private float stability;
    private Dominance dominance;

    public Allele(AlleleValue valueHolder, float mutationRate, float stability, Dominance dominance) {
        this.valueHolder = valueHolder;
        this.mutationRate = mutationRate;
        this.stability = stability;
        this.dominance = dominance;
    }

    public AlleleValue getValueHolder() { return valueHolder; }
    public void setValueHolder(AlleleValue valueHolder) {
        this.valueHolder = valueHolder;
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

    public Allele copyWithValue(AlleleValue newValue) {
        return new Allele(newValue, this.mutationRate, this.stability, this.dominance);
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();

        tag.putString("ValueType", valueHolder.getType().name());

        CompoundTag valueTag = new CompoundTag();
        valueHolder.serialize(valueTag);
        tag.put("Value", valueTag);

        tag.putFloat("MutationRate", mutationRate);
        tag.putFloat("Stability", stability);
        tag.putString("Dominance", dominance.name());

        return tag;
    }

    public static Allele deserializeNBT(CompoundTag tag) {
        AlleleValueType type = AlleleValueType.valueOf(tag.getString("ValueType"));

        AlleleValue value = switch (type) {
            case FLOAT -> new FloatAlleleValue(0);
            case RESOURCE_LOCATION -> new ResourceLocationAlleleValue(new ResourceLocation("minecraft:air"));
            case ENUM -> new EnumAlleleValue<>(Dominance.DOMINANT);
        };

        value.deserialize(tag.getCompound("Value"));

        return new Allele(
                value,
                tag.getFloat("MutationRate"),
                tag.getFloat("Stability"),
                Dominance.valueOf(tag.getString("Dominance"))
        );
    }

    @Override
    public String toString() {
        return "Allele[" + "value=" + valueHolder + ", mutationRate=" + mutationRate + ", stability=" + stability + ", dominance=" + dominance + ']';
    }
}
