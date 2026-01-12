package net.farkas.wildaside.dna.expression;

import net.minecraft.nbt.CompoundTag;

public class Enhancer {
    private final String id;
    private final ActivationCondition condition;
    private final float multiplier;
    private final float flatBonus;

    public Enhancer(String id, ActivationCondition condition, float multiplier, float flatBonus) {
        this.id = id;
        this.condition = condition;
        this.multiplier = multiplier;
        this.flatBonus = flatBonus;
    }

    public boolean shouldApply(ExpressionContext context) {
        return condition.test(context, 0f);
    }

    public float apply(float value) {
        return value * multiplier + flatBonus;
    }

    public String getId() {
        return id;
    }

    public ActivationCondition getCondition() {
        return condition;
    }

    public float getMultiplier() {
        return multiplier;
    }

    public float getFlatBonus() {
        return flatBonus;
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Id", id);
        tag.putString("Condition", condition.name());
        tag.putFloat("Multiplier", multiplier);
        tag.putFloat("FlatBonus", flatBonus);
        return tag;
    }

    public static Enhancer deserializeNBT(CompoundTag tag) {
        return new Enhancer(
                tag.getString("Id"),
                ActivationCondition.valueOf(tag.getString("Condition")),
                tag.getFloat("Multiplier"),
                tag.getFloat("FlatBonus")
        );
    }
}
