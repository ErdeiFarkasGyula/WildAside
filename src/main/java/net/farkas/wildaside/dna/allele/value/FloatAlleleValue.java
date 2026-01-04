package net.farkas.wildaside.dna.allele.value;

import net.farkas.wildaside.dna.DnaUtils;
import net.farkas.wildaside.dna.allele.dominance.Dominance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

import static net.farkas.wildaside.dna.allele.dominance.Dominance.*;

public class FloatAlleleValue implements AlleleValue {
    private float value;

    public FloatAlleleValue(float value) {
        this.value = value;
    }

    public float get() {
        return value;
    }

    public void set(float value) {
        this.value = value;
    }

    @Override
    public AlleleValueType getType() {
        return AlleleValueType.FLOAT;
    }

    @Override
    public AlleleValue expressWith(AlleleValue other, Dominance domA, Dominance domB) {
        if (!(other instanceof FloatAlleleValue o))
            return this;

        float va = value;
        float vb = o.value;

        if (domA == DOMINANT && domB == DOMINANT)
            return new FloatAlleleValue(Math.max(va, vb));

        if (domA == RECESSIVE && domB == RECESSIVE)
            return new FloatAlleleValue((va + vb) / 2f);

        if (domA == DOMINANT && domB == RECESSIVE)
            return new FloatAlleleValue(va);

        if (domB == DOMINANT && domA == RECESSIVE)
            return new FloatAlleleValue(vb);

        if (domA == CO_DOMINANT || domB == CO_DOMINANT)
            return new FloatAlleleValue((va + vb) / 2f);

        if (domA == INCOMPLETE || domB == INCOMPLETE)
            return new FloatAlleleValue((va + vb) / 2f);

        return new FloatAlleleValue((va + vb) / 2f);
    }

    @Override
    public void serialize(CompoundTag tag) {
        tag.putFloat("Value", value);
    }

    @Override
    public void deserialize(CompoundTag tag) {
        value = tag.getFloat("Value");
    }

    @Override
    public Component format() {
        return Component.literal(DnaUtils.getFormattedFloatString(value));
    }

    @Override
    public AlleleValue parse(String input) {
        try {
            return new FloatAlleleValue(Float.parseFloat(input));
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("Expected a number, got '" + input + "'");
        }
    }
}
