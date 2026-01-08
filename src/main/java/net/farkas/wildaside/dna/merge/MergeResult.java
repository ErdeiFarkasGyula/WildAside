package net.farkas.wildaside.dna.merge;

import net.farkas.wildaside.dna.locus.GeneLocus;
import net.farkas.wildaside.dna.trait.Trait;

import java.util.List;
import java.util.Map;

public record MergeResult(Map<Trait, List<GeneLocus>> resultLoci, float stressGain, List<MergeEvent> events) {
    public int getIntegratedCount() {
        return (int) events.stream()
                .filter(e -> e.outcome().type() == MergeOutcomeType.INTEGRATED)
                .count();
    }

    public int getRejectedCount() {
        return (int) events.stream()
                .filter(e -> e.outcome().type() == MergeOutcomeType.REJECTED)
                .count();
    }

    public int getTransientCount() {
        return (int) events.stream()
                .filter(e -> e.outcome().type() == MergeOutcomeType.TRANSIENT)
                .count();
    }

    public boolean hasRejections() {
        return getRejectedCount() > 0;
    }
}