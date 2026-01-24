package net.farkas.wildaside.dna.sequence.components;

import net.farkas.wildaside.dna.expression.ActivationCondition;
import net.farkas.wildaside.dna.expression.ExpressionContext;
import net.minecraft.nbt.CompoundTag;

public class Enhancer implements GeneComponent {
    private final String id;
    private final ActivationCondition condition;
    private final float threshold;
    private final float multiplier;
    private final float flatBonus;

    public Enhancer(String id, ActivationCondition condition, float threshold, float multiplier, float flatBonus) {
        this.id = id;
        this.condition = condition;
        this.threshold = threshold;
        this.multiplier = Math.max(1f, multiplier);
        this.flatBonus = flatBonus;
    }

    public boolean shouldApply(ExpressionContext context, float currentGeneValue) {
        if (condition == ActivationCondition.GENE_VALUE_ABOVE) {
            return currentGeneValue > threshold;
        }
        if (condition == ActivationCondition.GENE_VALUE_BELOW) {
            return currentGeneValue < threshold;
        }
        return condition.test(context, threshold);
    }

    public float apply(float value) {
        return value * multiplier + flatBonus;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public ComponentType getType() {
        return ComponentType.ENHANCER;
    }

    public ActivationCondition getCondition() {
        return condition;
    }

    public float getThreshold() {
        return threshold;
    }

    public float getMultiplier() {
        return multiplier;
    }

    public float getFlatBonus() {
        return flatBonus;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Id", id);
        tag.putString("Condition", condition.name());
        tag.putFloat("Threshold", threshold);
        tag.putFloat("Multiplier", multiplier);
        tag.putFloat("FlatBonus", flatBonus);
        return tag;
    }

    public static Enhancer deserializeNBT(CompoundTag tag) {
        return new Enhancer(
                tag.getString("Id"),
                ActivationCondition.valueOf(tag.getString("Condition")),
                tag.getFloat("Threshold"),
                tag.getFloat("Multiplier"),
                tag.getFloat("FlatBonus")
        );
    }
}
