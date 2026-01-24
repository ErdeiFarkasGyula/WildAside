package net.farkas.wildaside.dna.sequence.components;

import net.farkas.wildaside.dna.expression.ExpressionContext;
import net.farkas.wildaside.dna.expression.RegulationType;
import net.minecraft.nbt.CompoundTag;

public class Regulator implements GeneComponent {
    private final String id;
    private final RegulationType type;
    private final float value;

    public Regulator(String id, RegulationType type, float value) {
        this.id = id;
        this.type = type;
        this.value = value;
    }

    public float regulate(float input, ExpressionContext context) {
        return switch (type) {
            case CAP_MAX -> Math.min(input, value);
            case CAP_MIN -> Math.max(input, value);
            case CLAMP -> Math.max(0, Math.min(input, value));
            case ROUND -> Math.round(input / value) * value;
            case SCALE_BY_HEALTH -> input * (context.getHealthPercent() * value);
            case SCALE_BY_MISSING_HEALTH -> input * ((1f - context.getHealthPercent()) * value);
        };
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public ComponentType getType() {
        return ComponentType.REGULATOR;
    }

    public RegulationType getRegulationType() {
        return type;
    }

    public float getValue() {
        return value;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Id", id);
        tag.putString("Type", type.name());
        tag.putFloat("Value", value);
        return tag;
    }

    public static Regulator deserializeNBT(CompoundTag tag) {
        return new Regulator(
                tag.getString("Id"),
                RegulationType.valueOf(tag.getString("Type")),
                tag.getFloat("Value")
        );
    }
}
