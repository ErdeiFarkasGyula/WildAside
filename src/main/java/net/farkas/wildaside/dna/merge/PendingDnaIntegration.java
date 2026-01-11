package net.farkas.wildaside.dna.merge;

import net.farkas.wildaside.dna.locus.GeneLocus;
import net.farkas.wildaside.dna.trait.Trait;
import net.farkas.wildaside.dna.trait.TraitRegistry;
import net.minecraft.nbt.CompoundTag;

public class PendingDnaIntegration {
    private final Trait trait;
    private final GeneLocus locus;
    private final MergeOutcomeType outcomeType;
    private final long startTick;
    private final int dormantDuration;
    private final int integrationDuration;
    private int currentProgress;
    private boolean dormantPhaseComplete;

    public static final int DORMANT_DURATION = 20 * 30;

    public static final int DURATION_INTEGRATED = 20 * 60 * 2;
    public static final int DURATION_TRANSIENT = 20 * 60;
    public static final int DURATION_REJECTED = 20 * 30;

    public PendingDnaIntegration(Trait trait, GeneLocus locus, MergeOutcomeType outcomeType, long startTick) {
        this.trait = trait;
        this.locus = locus;
        this.outcomeType = outcomeType;
        this.startTick = startTick;
        this.currentProgress = 0;
        this.dormantPhaseComplete = false;
        this.dormantDuration = DORMANT_DURATION;

        this.integrationDuration = switch (outcomeType) {
            case INTEGRATED -> DURATION_INTEGRATED;
            case TRANSIENT -> DURATION_TRANSIENT;
            case REJECTED -> DURATION_REJECTED;
            case REPLACED -> DURATION_INTEGRATED;
        };
    }

    public PendingDnaIntegration(Trait trait, GeneLocus locus, MergeOutcomeType outcomeType, long startTick, int dormantDuration, int integrationDuration, int currentProgress, boolean dormantPhaseComplete) {
        this.trait = trait;
        this.locus = locus;
        this.outcomeType = outcomeType;
        this.startTick = startTick;
        this.dormantDuration = dormantDuration;
        this.integrationDuration = integrationDuration;
        this.currentProgress = currentProgress;
        this.dormantPhaseComplete = dormantPhaseComplete;
    }

    public Trait getTrait() {
        return trait;
    }

    public GeneLocus getLocus() {
        return locus;
    }

    public MergeOutcomeType getOutcomeType() {
        return outcomeType;
    }

    public long getStartTick() {
        return startTick;
    }

    public int getDormantDuration() {
        return dormantDuration;
    }

    public int getIntegrationDuration() {
        return integrationDuration;
    }

    public int getTotalDuration() {
        return dormantDuration + integrationDuration;
    }

    public int getCurrentProgress() {
        return currentProgress;
    }

    public void incrementProgress(int amount) {
        this.currentProgress = Math.min(getTotalDuration(), currentProgress + amount);

        if (!dormantPhaseComplete && currentProgress >= dormantDuration) {
            dormantPhaseComplete = true;
        }
    }

    public boolean isDormant() {
        return currentProgress < dormantDuration;
    }

    public boolean justExitedDormancy() {
        return dormantPhaseComplete && currentProgress >= dormantDuration && currentProgress < dormantDuration + 20;
    }

    public int getIntegrationProgress() {
        if (isDormant()) return 0;
        return currentProgress - dormantDuration;
    }

    public float getIntegrationProgressPercent() {
        if (isDormant()) return 0f;
        return (float) getIntegrationProgress() / integrationDuration;
    }

    public float getTotalProgressPercent() {
        return (float) currentProgress / getTotalDuration();
    }

    public float getDormantProgressPercent() {
        if (dormantPhaseComplete) return 1f;
        return (float) currentProgress / dormantDuration;
    }

    public boolean isComplete() {
        return currentProgress >= getTotalDuration();
    }

    public IntegrationPhase getPhase() {
        if (isDormant()) return IntegrationPhase.DORMANT;

        float percent = getIntegrationProgressPercent();
        if (percent < 0.25f) return IntegrationPhase.INITIAL;
        if (percent < 0.50f) return IntegrationPhase.ADAPTING;
        if (percent < 0.75f) return IntegrationPhase.STABILIZING;
        if (percent < 1.0f) return IntegrationPhase.FINALIZING;
        return IntegrationPhase.COMPLETE;
    }

    public enum IntegrationPhase {
        DORMANT,
        INITIAL,
        ADAPTING,
        STABILIZING,
        FINALIZING,
        COMPLETE
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Trait", trait.getName());
        tag.put("Locus", locus.serializeNBT());
        tag.putString("OutcomeType", outcomeType.name());
        tag.putLong("StartTick", startTick);
        tag.putInt("DormantDuration", dormantDuration);
        tag.putInt("IntegrationDuration", integrationDuration);
        tag.putInt("CurrentProgress", currentProgress);
        tag.putBoolean("DormantPhaseComplete", dormantPhaseComplete);
        return tag;
    }

    public static PendingDnaIntegration deserializeNBT(CompoundTag tag) {
        Trait trait = TraitRegistry.getByName(tag.getString("Trait"));
        GeneLocus locus = GeneLocus.deserializeNBT(tag.getCompound("Locus"));
        MergeOutcomeType outcomeType = MergeOutcomeType.valueOf(tag.getString("OutcomeType"));
        long startTick = tag.getLong("StartTick");
        int dormantDuration = tag.contains("DormantDuration") ? tag.getInt("DormantDuration") : DORMANT_DURATION;
        int integrationDuration = tag.getInt("IntegrationDuration");
        int currentProgress = tag.getInt("CurrentProgress");
        boolean dormantPhaseComplete = tag.getBoolean("DormantPhaseComplete");

        return new PendingDnaIntegration(trait, locus, outcomeType, startTick, dormantDuration, integrationDuration, currentProgress, dormantPhaseComplete);
    }
}