package net.farkas.wildaside.dna.breeding;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.capability.dna.DnaCapability;
import net.farkas.wildaside.capability.dna.IDna;
import net.farkas.wildaside.dna.DnaUtils;
import net.farkas.wildaside.dna.chromosome.ChromosomeSet;
import net.farkas.wildaside.dna.chromosome.Genome;
import net.farkas.wildaside.dna.sequence.GeneSource;
import net.farkas.wildaside.dna.sequence.components.Activator;
import net.farkas.wildaside.dna.sequence.components.Enhancer;
import net.farkas.wildaside.dna.sequence.components.CodingRegion;
import net.farkas.wildaside.dna.sequence.GeneSequence;
import net.farkas.wildaside.dna.trait.Trait;
import net.minecraft.world.entity.AgeableMob;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BreedingHandler {
    private static final float MUTATION_CHANCE = 0.05f;
    private static final float MUTATION_MAGNITUDE = 0.15f;
    private static final float COMPONENT_CROSSOVER_CHANCE = 0.2f;

    public static void applyInheritance(AgeableMob child, AgeableMob parent1, AgeableMob parent2) {
        if (child == null || parent1 == null || parent2 == null) return;

        WildAside.LOGGER.info("=== BREEDING INHERITANCE (GENOME-BASED) ===");
        WildAside.LOGGER.info("Parent 1: {}", parent1.getName().getString());
        WildAside.LOGGER.info("Parent 2: {}", parent2.getName().getString());
        WildAside.LOGGER.info("Child: {}", child.getName().getString());

        IDna parent1Dna = parent1.getCapability(DnaCapability.INSTANCE).orElse(null);
        IDna parent2Dna = parent2.getCapability(DnaCapability.INSTANCE).orElse(null);

        if (parent1Dna == null && parent2Dna == null) {
            WildAside.LOGGER.info("Neither parent has DNA capability, generating fresh genome for child");
            child.getCapability(DnaCapability.INSTANCE).ifPresent(childDna -> {
                childDna.setGenome(DnaUtils.generateBaseGenome(child));
            });
            return;
        }

        ensureParentGenome(parent1, parent1Dna);
        ensureParentGenome(parent2, parent2Dna);

        parent1Dna = parent1.getCapability(DnaCapability.INSTANCE).orElse(null);
        parent2Dna = parent2.getCapability(DnaCapability.INSTANCE).orElse(null);

        if (parent1Dna == null || parent2Dna == null) {
            WildAside.LOGGER.warn("Failed to ensure parent DNA");
            return;
        }

        IDna finalParent1Dna = parent1Dna;
        IDna finalParent2Dna = parent2Dna;

        child.getCapability(DnaCapability.INSTANCE).ifPresent(childDna -> {
            Genome childGenome = inheritGenome(
                    finalParent1Dna.getGenome(),
                    finalParent2Dna.getGenome(),
                    child
            );

            childDna.setGenome(childGenome);
            childDna.setStress(0f);

            WildAside.LOGGER.info("Child inherited genome with {} chromosome pairs", 
                    childGenome.getMaternal().getAllChromosomes().size());
        });

        WildAside.LOGGER.info("=== BREEDING COMPLETE ===");
    }

    private static void ensureParentGenome(AgeableMob parent, IDna dna) {
        if (dna == null) return;

        Genome genome = dna.getGenome();
        if (genome == null || genome.getMaternal().getAllChromosomes().isEmpty()) {
            dna.setGenome(DnaUtils.generateBaseGenome(parent));
            WildAside.LOGGER.info("Generated genome for parent: {}", parent.getName().getString());
        }
    }

    private static Genome inheritGenome(Genome parent1Genome, Genome parent2Genome, AgeableMob child) {
        long seed = child.getUUID().getLeastSignificantBits();
        Random random = new Random(seed);

        ChromosomeSet p1Maternal = parent1Genome.getMaternal();
        ChromosomeSet p1Paternal = parent1Genome.getPaternal();
        ChromosomeSet p2Maternal = parent2Genome.getMaternal();
        ChromosomeSet p2Paternal = parent2Genome.getPaternal();

        ChromosomeSet childMaternalSet = new ChromosomeSet();
        ChromosomeSet childPaternalSet = new ChromosomeSet();

        for (Trait trait : net.farkas.wildaside.dna.trait.TraitRegistry.getAllTraits()) {
            GeneSequence p1Mat = p1Maternal.getSequence(trait);
            GeneSequence p1Pat = p1Paternal.getSequence(trait);
            GeneSequence p2Mat = p2Maternal.getSequence(trait);
            GeneSequence p2Pat = p2Paternal.getSequence(trait);

            GeneSequence childMaternal = inheritSequence(p1Mat, p1Pat, trait, seed, random, true);
            GeneSequence childPaternal = inheritSequence(p2Mat, p2Pat, trait, seed, random, false);

            if (childMaternal != null) {
                childMaternalSet.putSequence(trait, childMaternal);
            }
            if (childPaternal != null) {
                childPaternalSet.putSequence(trait, childPaternal);
            }
        }

        return new Genome(child.getType(), childMaternalSet, childPaternalSet);
    }

    private static GeneSequence inheritSequence(GeneSequence mat, GeneSequence pat, Trait trait, long seed, Random random, boolean fromParent1) {
        if (mat == null && pat == null) return null;
        
        GeneSequence source = random.nextBoolean() ? mat : pat;
        if (source == null) source = (mat != null) ? mat : pat;

        List<CodingRegion> newCodingRegions = new ArrayList<>();
        for (CodingRegion cr : source.getCodingRegions()) {
            float value = cr.getValue();
            if (random.nextFloat() < MUTATION_CHANCE) {
                float mutation = (random.nextFloat() - 0.5f) * 2f * MUTATION_MAGNITUDE;
                value *= (1f + mutation);
                value = Math.max(0f, value);
            }
            newCodingRegions.add(new CodingRegion(cr.getId(), value, cr.getCombineMethod()));
        }

        if (random.nextFloat() < COMPONENT_CROSSOVER_CHANCE && pat != null && mat != null) {
            GeneSequence other = (source == mat) ? pat : mat;
            if (!other.getCodingRegions().isEmpty() && !newCodingRegions.isEmpty()) {
                int swapIdx = random.nextInt(Math.min(newCodingRegions.size(), other.getCodingRegions().size()));
                if (swapIdx < other.getCodingRegions().size()) {
                    CodingRegion swapped = other.getCodingRegions().get(swapIdx);
                    newCodingRegions.set(Math.min(swapIdx, newCodingRegions.size() - 1), swapped);
                }
            }
        }

        List<Activator> newActivators = new ArrayList<>(source.getActivators());
        List<Enhancer> newEnhancers = new ArrayList<>(source.getEnhancers());

        float newStability = source.getStability() * 0.95f;
        newStability = Math.max(0.1f, Math.min(1.0f, newStability));

        return new GeneSequence.Builder()
                .trait(trait)
                .codingRegions(newCodingRegions)
                .activators(newActivators)
                .enhancers(newEnhancers)
                .silencers(new ArrayList<>(source.getSilencers()))
                .regulators(new ArrayList<>(source.getRegulators()))
                .stability(newStability)
                .source(GeneSource.NATURAL)
                .build();
    }
}
