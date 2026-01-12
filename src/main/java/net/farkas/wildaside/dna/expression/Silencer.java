package net.farkas.wildaside.dna.expression;

import net.minecraft.nbt.CompoundTag;

public class Silencer {
    private final String id;
    private final ActivationCondition condition;
    private final float multiplier;
    private final float flatPenalty;

    public Silencer(String id, ActivationCondition condition, float multiplier, float flatPenalty) {
        this.id = id;
        this.condition = condition;
        this.multiplier = multiplier;
        this.flatPenalty = flatPenalty;
    }

    public boolean shouldApply(ExpressionContext context) {
        return condition.test(context, 0f);
    }

    public float apply(float value) {
        return Math.max(0f, value * multiplier - flatPenalty);
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

    public float getFlatPenalty() {
        return flatPenalty;
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Id", id);
        tag.putString("Condition", condition.name());
        tag.putFloat("Multiplier", multiplier);
        tag.putFloat("FlatPenalty", flatPenalty);
        return tag;
    }

    public static Silencer deserializeNBT(CompoundTag tag) {
        return new Silencer(
                tag.getString("Id"),
                ActivationCondition.valueOf(tag.getString("Condition")),
                tag.getFloat("Multiplier"),
                tag.getFloat("FlatPenalty")
        );
    }
}
