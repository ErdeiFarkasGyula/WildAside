package net.farkas.wildaside.dna.allele.value;

import net.farkas.wildaside.dna.dominance.Dominance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import static java.util.Objects.hash;

public class ResourceLocationAlleleValue implements AlleleValue {
    private ResourceLocation value;

    public ResourceLocationAlleleValue(ResourceLocation value) {
        this.value = value;
    }

    public ResourceLocation get() {
        return value;
    }

    @Override
    public AlleleValueType getType() {
        return AlleleValueType.RESOURCE_LOCATION;
    }

    @Override
    public AlleleValue expressWith(AlleleValue other, Dominance da, Dominance db) {
        if (!(other instanceof ResourceLocationAlleleValue o))
            return this;

        if (da == Dominance.DOMINANT && db != Dominance.DOMINANT)
            return this;

        if (db == Dominance.DOMINANT && da != Dominance.DOMINANT)
            return o;

        if (da == Dominance.CO_DOMINANT && db == Dominance.CO_DOMINANT)
            return deterministicPick(this, o);

        return deterministicPick(this, o);
    }

    private ResourceLocationAlleleValue deterministicPick(ResourceLocationAlleleValue a, ResourceLocationAlleleValue b) {
        return hash(a.value.toString(), b.value.toString()) < 0.5f ? a : b;
    }

    @Override
    public void serialize(CompoundTag tag) {
        tag.putString("Value", value.toString());
    }

    @Override
    public void deserialize(CompoundTag tag) {
        value = new ResourceLocation(tag.getString("Value"));
    }

    @Override
    public Component format() {
        return Component.literal(value.getPath());
    }

    @Override
    public AlleleValue parse(String input) {
        try {
            return new ResourceLocationAlleleValue(new ResourceLocation(input));
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Invalid resource location: " + input);
        }
    }
}
