package net.farkas.wildaside.dna.merge;

import net.farkas.wildaside.dna.sequence.GeneSequence;

public record MergeOutcome(MergeOutcomeType type, GeneSequence replacedSequence, float stressCost) {
    public static MergeOutcome integrated(float stressCost) {
        return new MergeOutcome(MergeOutcomeType.INTEGRATED, null, stressCost);
    }

    public static MergeOutcome transient_(float stressCost) {
        return new MergeOutcome(MergeOutcomeType.TRANSIENT, null, stressCost);
    }

    public static MergeOutcome rejected(float stressCost) {
        return new MergeOutcome(MergeOutcomeType.REJECTED, null, stressCost);
    }

    public static MergeOutcome replaced(GeneSequence replaced, float stressCost) {
        return new MergeOutcome(MergeOutcomeType.REPLACED, replaced, stressCost);
    }
}