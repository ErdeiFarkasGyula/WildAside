package net.farkas.wildaside.dna.expression;

import net.minecraft.nbt.CompoundTag;

public class Activator {
    private final String id;
    private final ActivationCondition condition;
    private final float activationThreshold;

    public Activator(String id, ActivationCondition condition, float activationThreshold) {
        this.id = id;
        this.condition = condition;
        this.activationThreshold = activationThreshold;
    }

    public boolean isActive(ExpressionContext context) {
        return condition.test(context, activationThreshold);
    }

    public String getId() {
        return id;
    }

    public ActivationCondition getCondition() {
        return condition;
    }

    public float getActivationThreshold() {
        return activationThreshold;
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Id", id);
        tag.putString("Condition", condition.name());
        tag.putFloat("Threshold", activationThreshold);
        return tag;
    }

    public static Activator deserializeNBT(CompoundTag tag) {
        return new Activator(
                tag.getString("Id"),
                ActivationCondition.valueOf(tag.getString("Condition")),
                tag.getFloat("Threshold")
        );
    }
}
