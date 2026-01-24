package net.farkas.wildaside.dna.merge;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.capability.dna.DnaCapability;
import net.farkas.wildaside.dna.chromosome.Chromosome;
import net.farkas.wildaside.dna.chromosome.Genome;
import net.farkas.wildaside.dna.sequence.GeneSequence;
import net.farkas.wildaside.dna.sequence.GeneSource;
import net.farkas.wildaside.dna.trait.Trait;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

public class DnaDegradationHandler {
    public static final float TRANSIENT_DEGRADATION_RATE = 0.005f;
    public static final float REJECTED_DEGRADATION_RATE = 0.02f;

    public static final float STABILIZATION_CHANCE = 0.002f;

    public static void tickDegradation(LivingEntity entity) {
        entity.getCapability(DnaCapability.INSTANCE).ifPresent(dna -> {
            Genome genome = dna.getGenome();
            if (genome == null) return;

            boolean changed = false;
            long seed = entity.getUUID().getLeastSignificantBits() ^ entity.tickCount;

            int totalDegrading = 0;
            int removed = 0;
            int stabilized = 0;

            List<Chromosome> allChromosomes = new ArrayList<>();
            allChromosomes.addAll(genome.getMaternal().getAllChromosomes());
            allChromosomes.addAll(genome.getPaternal().getAllChromosomes());

            for (Chromosome chromosome : allChromosomes) {
                for (GeneSequence sequence : chromosome.getAllGeneSequences()) {
                    if (sequence.getSource() == GeneSource.INTEGRATED) {

                    }
                }
            }

            if (totalDegrading > 0) {
                WildAside.LOGGER.debug("Degradation tick for {}: {} degrading, {} removed, {} stabilized",
                        entity.getName().getString(), totalDegrading, removed, stabilized);
            }

            if (changed) {
                dna.setGenome(genome);
                dna.recomputeAndApply(entity);
            }
        });
    }
}