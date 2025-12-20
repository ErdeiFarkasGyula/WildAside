package net.farkas.wildaside.dna.allele.value;

import net.farkas.wildaside.dna.dominance.Dominance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

import java.util.Arrays;
import java.util.Locale;

import static java.util.Objects.hash;

public class EnumAlleleValue<E extends Enum<E>> implements AlleleValue {
    private final Class<E> enumClass;
    private E value;

    public EnumAlleleValue(E value) {
        this.enumClass = value.getDeclaringClass();
        this.value = value;
    }

    public E get() {
        return value;
    }

    @Override
    public AlleleValueType getType() {
        return AlleleValueType.ENUM;
    }

    @Override
    public AlleleValue expressWith(AlleleValue other, Dominance da, Dominance db) {
        if (!(other instanceof EnumAlleleValue<?> o))
            return this;

        if (da == Dominance.DOMINANT && db != Dominance.DOMINANT)
            return this;

        if (db == Dominance.DOMINANT && da != Dominance.DOMINANT)
            return o;

        if (da == Dominance.CO_DOMINANT && db == Dominance.CO_DOMINANT) {
            return deterministicPick(this, o);
        }

        if (da == Dominance.INCOMPLETE || db == Dominance.INCOMPLETE) {
            return da.ordinal() < db.ordinal() ? this : o;
        }

        return deterministicPick(this, o);
    }

    private EnumAlleleValue<?> deterministicPick(EnumAlleleValue<?> a, EnumAlleleValue<?> b) {
        return hash(a.value, b.value) < 0.5f ? a : b;
    }

    @Override
    public void serialize(CompoundTag tag) {
        tag.putString("EnumClass", value.getDeclaringClass().getName());
        tag.putString("Value", value.name());
    }

    @Override
    public void deserialize(CompoundTag tag) {
        try {
            Class<?> cls = Class.forName(tag.getString("EnumClass"));
            value = (E) Enum.valueOf(
                    (Class<? extends Enum>) cls,
                    tag.getString("Value")
            );
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to deserialize enum allele", e);
        }
    }

    @Override
    public Component format() {
        return Component.literal(value.name().toLowerCase());
    }

    public AlleleValue parse(String input) {
        try {
            return new EnumAlleleValue<>(Enum.valueOf(enumClass, input.toUpperCase(Locale.ROOT)));
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Expected one of: " + Arrays.toString(enumClass.getEnumConstants()));
        }
    }
}
