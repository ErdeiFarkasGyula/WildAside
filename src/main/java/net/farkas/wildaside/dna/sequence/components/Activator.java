package net.farkas.wildaside.dna.sequence.components;

import net.farkas.wildaside.dna.expression.ActivationCondition;
import net.farkas.wildaside.dna.expression.ExpressionContext;
import net.minecraft.nbt.CompoundTag;

public class Activator implements GeneComponent {
    private final String id;
    private final ActivationCondition condition;
    private final float activationThreshold;

    public Activator(String id, ActivationCondition condition, float activationThreshold) {
        this.id = id;
        this.condition = condition;
        this.activationThreshold = activationThreshold;
    }

    public boolean isActive(ExpressionContext context, float geneValue) {
        if (condition == ActivationCondition.GENE_VALUE_ABOVE) {
            return geneValue > activationThreshold;
        }
        if (condition == ActivationCondition.GENE_VALUE_BELOW) {
            return geneValue < activationThreshold;
        }
        return condition.test(context, activationThreshold);
    }

    public boolean isActive(ExpressionContext context) {
        return isActive(context, 0f);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public ComponentType getType() {
        return ComponentType.ACTIVATOR;
    }

    public ActivationCondition getCondition() {
        return condition;
    }

    public float getActivationThreshold() {
        return activationThreshold;
    }

    @Override
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
