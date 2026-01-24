package net.farkas.wildaside.dna.merge;

import net.farkas.wildaside.dna.chromosome.Genome;

import java.util.List;

public record MergeResult(Genome resultGenome, float stressGain, List<MergeEvent> events) {
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