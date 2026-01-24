package net.farkas.wildaside.dna.sequence.components;

import net.farkas.wildaside.dna.expression.ActivationCondition;
import net.farkas.wildaside.dna.expression.ExpressionContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;

public class Silencer implements GeneComponent {
    private final String id;
    private final ActivationCondition condition;
    private final float threshold;
    private final float multiplier;
    private final float flatPenalty;

    public Silencer(String id, ActivationCondition condition, float threshold, float multiplier, float flatPenalty) {
        this.id = id;
        this.condition = condition;
        this.threshold = threshold;
        this.multiplier = Mth.clamp(multiplier, 0f, 1f);
        this.flatPenalty = flatPenalty;
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
        return Math.max(0f, value * multiplier - flatPenalty);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public ComponentType getType() {
        return ComponentType.SILENCER;
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

    public float getFlatPenalty() {
        return flatPenalty;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Id", id);
        tag.putString("Condition", condition.name());
        tag.putFloat("Threshold", threshold);
        tag.putFloat("Multiplier", multiplier);
        tag.putFloat("FlatPenalty", flatPenalty);
        return tag;
    }

    public static Silencer deserializeNBT(CompoundTag tag) {
        return new Silencer(
                tag.getString("Id"),
                ActivationCondition.valueOf(tag.getString("Condition")),
                tag.getFloat("Threshold"),
                tag.getFloat("Multiplier"),
                tag.getFloat("FlatPenalty")
        );
    }
}
