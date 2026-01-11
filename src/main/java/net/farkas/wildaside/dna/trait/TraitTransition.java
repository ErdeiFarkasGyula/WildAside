package net.farkas.wildaside.dna.trait;

import net.minecraft.nbt.CompoundTag;

public class TraitTransition {
    private final Trait trait;
    private final float startValue;
    private final float targetValue;
    private final long startTick;
    private final int duration;
    private float currentValue;
    private boolean complete;

    public static final int DEFAULT_DURATION = 20 * 30;
    public static final int FAST_DURATION = 20 * 10;
    public static final int SLOW_DURATION = 20 * 60;

    public TraitTransition(Trait trait, float startValue, float targetValue, long startTick, int duration) {
        this.trait = trait;
        this.startValue = startValue;
        this.targetValue = targetValue;
        this.startTick = startTick;
        this.duration = duration;
        this.currentValue = startValue;
        this.complete = false;
    }

    public Trait getTrait() {
        return trait;
    }

    public float getStartValue() {
        return startValue;
    }

    public float getTargetValue() {
        return targetValue;
    }

    public long getStartTick() {
        return startTick;
    }

    public int getDuration() {
        return duration;
    }

    public float getCurrentValue() {
        return currentValue;
    }

    public boolean isComplete() {
        return complete;
    }

    public float tick(long currentTick) {
        if (complete) return currentValue;

        long elapsed = currentTick - startTick;
        if (elapsed >= duration) {
            currentValue = targetValue;
            complete = true;
            return currentValue;
        }

        float progress = (float) elapsed / duration;
        float easedProgress = easeInOutCubic(progress);
        currentValue = startValue + (targetValue - startValue) * easedProgress;

        return currentValue;
    }

    private float easeInOutCubic(float t) {
        if (t < 0.5f) {
            return 4f * t * t * t;
        }
        else {
            float f = 2f * t - 2f;
            return 0.5f * f * f * f + 1f;
        }
    }

    public float getProgress(long currentTick) {
        if (complete) return 1f;
        long elapsed = currentTick - startTick;
        return Math.min(1f, (float) elapsed / duration);
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Trait", trait.getName());
        tag.putFloat("StartValue", startValue);
        tag.putFloat("TargetValue", targetValue);
        tag.putLong("StartTick", startTick);
        tag.putInt("Duration", duration);
        tag.putFloat("CurrentValue", currentValue);
        tag.putBoolean("Complete", complete);
        return tag;
    }

    public static TraitTransition deserializeNBT(CompoundTag tag) {
        Trait trait = TraitRegistry.getByName(tag.getString("Trait"));
        if (trait == null) return null;

        float startValue = tag.getFloat("StartValue");
        float targetValue = tag.getFloat("TargetValue");
        long startTick = tag.getLong("StartTick");
        int duration = tag.getInt("Duration");

        TraitTransition transition = new TraitTransition(trait, startValue, targetValue, startTick, duration);
        transition.currentValue = tag.getFloat("CurrentValue");
        transition.complete = tag.getBoolean("Complete");

        return transition;
    }
}