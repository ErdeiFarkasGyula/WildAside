package net.farkas.wildaside.dna.breeding;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.capability.dna.DnaCapability;
import net.farkas.wildaside.capability.dna.IDna;
import net.farkas.wildaside.dna.DnaUtils;
import net.farkas.wildaside.dna.Gene;
import net.farkas.wildaside.dna.allele.Allele;
import net.farkas.wildaside.dna.allele.dominance.Dominance;
import net.farkas.wildaside.dna.allele.value.AlleleValue;
import net.farkas.wildaside.dna.allele.value.FloatAlleleValue;
import net.farkas.wildaside.dna.locus.GeneLocus;
import net.farkas.wildaside.dna.locus.LocusSource;
import net.farkas.wildaside.dna.trait.Trait;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BreedingHandler {
    private static final float MUTATION_CHANCE = 0.05f;
    private static final float MUTATION_MAGNITUDE = 0.15f;
    private static final float CROSSOVER_CHANCE = 0.3f;

    public static void applyInheritance(AgeableMob child, AgeableMob parent1, AgeableMob parent2) {
        if (child == null || parent1 == null || parent2 == null) return;

        WildAside.LOGGER.info("=== BREEDING INHERITANCE ===");
        WildAside.LOGGER.info("Parent 1: {}", parent1.getName().getString());
        WildAside.LOGGER.info("Parent 2: {}", parent2.getName().getString());
        WildAside.LOGGER.info("Child: {}", child.getName().getString());

        IDna parent1Dna = parent1.getCapability(DnaCapability.INSTANCE).orElse(null);
        IDna parent2Dna = parent2.getCapability(DnaCapability.INSTANCE).orElse(null);

        if (parent1Dna == null && parent2Dna == null) {
            WildAside.LOGGER.info("Neither parent has DNA capability, generating fresh DNA for child");
            child.getCapability(DnaCapability.INSTANCE).ifPresent(childDna -> {
                childDna.setSource(child.getType());
                childDna.setLoci(DnaUtils.generateBaseLoci(child));
            });
            return;
        }

        ensureParentDna(parent1, parent1Dna);
        ensureParentDna(parent2, parent2Dna);

        parent1Dna = parent1.getCapability(DnaCapability.INSTANCE).orElse(null);
        parent2Dna = parent2.getCapability(DnaCapability.INSTANCE).orElse(null);

        if (parent1Dna == null || parent2Dna == null) {
            WildAside.LOGGER.warn("Failed to ensure parent DNA");
            return;
        }

        IDna finalParent1Dna = parent1Dna;
        IDna finalParent2Dna = parent2Dna;

        child.getCapability(DnaCapability.INSTANCE).ifPresent(childDna -> {
            Map<Trait, List<GeneLocus>> childLoci = inheritLoci(
                    finalParent1Dna.getLoci(),
                    finalParent2Dna.getLoci(),
                    child
            );

            childDna.setSource(child.getType());
            childDna.setLoci(childLoci);
            childDna.setStress(0f);

            int totalLoci = childLoci.values().stream().mapToInt(List::size).sum();
            WildAside.LOGGER.info("Child inherited {} traits with {} total loci", childLoci.size(), totalLoci);
        });

        WildAside.LOGGER.info("=== BREEDING COMPLETE ===");
    }

    private static void ensureParentDna(AgeableMob parent, IDna dna) {
        if (dna == null) return;

        if (dna.getLoci().isEmpty()) {
            dna.setSource(parent.getType());
            dna.setLoci(DnaUtils.generateBaseLoci(parent));
            WildAside.LOGGER.info("Generated DNA for parent:  {}", parent.getName().getString());
        }
    }

    private static Map<Trait, List<GeneLocus>> inheritLoci(
            Map<Trait, List<GeneLocus>> parent1Loci,
            Map<Trait, List<GeneLocus>> parent2Loci,
            LivingEntity child) {

        Map<Trait, List<GeneLocus>> childLoci = new HashMap<>();
        long seed = child.getUUID().getLeastSignificantBits();

        Set<Trait> allTraits = new java.util.HashSet<>();
        allTraits.addAll(parent1Loci.keySet());
        allTraits.addAll(parent2Loci.keySet());

        for (Trait trait : allTraits) {
            List<GeneLocus> p1Loci = parent1Loci.getOrDefault(trait, new ArrayList<>());
            List<GeneLocus> p2Loci = parent2Loci.getOrDefault(trait, new ArrayList<>());

            List<GeneLocus> inheritedLoci = inheritTraitLoci(trait, p1Loci, p2Loci, seed);
            if (!inheritedLoci.isEmpty()) {
                childLoci.put(trait, inheritedLoci);
            }
        }

        return childLoci;
    }

    private static List<GeneLocus> inheritTraitLoci(Trait trait, List<GeneLocus> p1Loci, List<GeneLocus> p2Loci, long seed) {
        List<GeneLocus> result = new ArrayList<>();

        int maxLoci = Math.max(p1Loci.size(), p2Loci.size());
        if (maxLoci == 0) return result;

        for (int i = 0; i < maxLoci; i++) {
            GeneLocus p1Locus = i < p1Loci.size() ? p1Loci.get(i) : null;
            GeneLocus p2Locus = i < p2Loci.size() ? p2Loci.get(i) : null;

            GeneLocus childLocus = inheritSingleLocus(trait, p1Locus, p2Locus, seed, i);
            if (childLocus != null) {
                result.add(childLocus);
            }
        }

        return result;
    }

    private static GeneLocus inheritSingleLocus(Trait trait, GeneLocus p1Locus, GeneLocus p2Locus, long seed, int index) {
        if (p1Locus == null && p2Locus == null) return null;

        if (p1Locus == null) {
            return inheritFromSingleParent(p2Locus, seed, index);
        }
        if (p2Locus == null) {
            return inheritFromSingleParent(p1Locus, seed, index);
        }

        String locusId = p1Locus.getId();

        float roll1 = DnaUtils.hashToFloat(seed, locusId + "_p1_allele", index);
        float roll2 = DnaUtils.hashToFloat(seed, locusId + "_p2_allele", index);

        Allele fromP1 = roll1 < 0.5f ? p1Locus.getAlleleA() : p1Locus.getAlleleB();
        Allele fromP2 = roll2 < 0.5f ? p2Locus.getAlleleA() : p2Locus.getAlleleB();

        float crossoverRoll = DnaUtils.hashToFloat(seed, locusId + "_crossover", index);
        if (crossoverRoll < CROSSOVER_CHANCE) {
            Allele temp = fromP1;
            fromP1 = fromP2;
            fromP2 = temp;
        }

        Allele childAlleleA = maybeMutate(fromP1, seed, locusId + "_A", trait);
        Allele childAlleleB = maybeMutate(fromP2, seed, locusId + "_B", trait);

        float avgStability = (p1Locus.getStability() + p2Locus.getStability()) / 2f;
        avgStability += (DnaUtils.hashToFloat(seed, locusId + "_stab", index) - 0.5f) * 0.1f;
        avgStability = Math.max(0.1f, Math.min(1.0f, avgStability));

        return new GeneLocus(
                locusId,
                childAlleleA,
                childAlleleB,
                p1Locus.getFlags(),
                avgStability,
                LocusSource.NATIVE,
                0,
                0f
        );
    }

    private static GeneLocus inheritFromSingleParent(GeneLocus parentLocus, long seed, int index) {
        String locusId = parentLocus.getId();

        float roll = DnaUtils.hashToFloat(seed, locusId + "_single", index);
        Allele inherited = roll < 0.5f ? parentLocus.getAlleleA() : parentLocus.getAlleleB();

        Allele childAlleleA = maybeMutate(inherited, seed, locusId + "_single_A", null);
        Allele childAlleleB = maybeMutate(inherited.copy(), seed, locusId + "_single_B", null);

        return new GeneLocus(
                locusId,
                childAlleleA,
                childAlleleB,
                parentLocus.getFlags(),
                parentLocus.getStability() * 0.95f,
                LocusSource.NATIVE,
                0,
                0f
        );
    }

    private static Allele maybeMutate(Allele allele, long seed, String salt, Trait trait) {
        float mutationRate = allele.getMutationRate();
        float roll = DnaUtils.hashToFloat(seed, salt + "_mutate", 0);

        if (roll >= mutationRate + MUTATION_CHANCE) {
            return allele.copy();
        }

        WildAside.LOGGER.info("Mutation occurred at {}", salt);

        AlleleValue originalValue = allele.getValueHolder();

        if (originalValue instanceof FloatAlleleValue floatValue) {
            float gaussian = DnaUtils.deterministicGaussian(seed, salt + "_mutval");
            float mutationAmount = gaussian * MUTATION_MAGNITUDE;
            float newValue = floatValue.get() * (1f + mutationAmount);
            newValue = Math.max(0f, newValue);

            Dominance newDominance = allele.getDominance();
            float domRoll = DnaUtils.hashToFloat(seed, salt + "_mutdom", 0);
            if (domRoll < 0.1f) {
                newDominance = DnaUtils.deterministicDominancePick(seed, salt + "_newdom");
            }

            float newMutationRate = allele.getMutationRate() * (1f + 0.1f * gaussian);
            newMutationRate = Math.max(0.01f, Math.min(0.3f, newMutationRate));

            return new Allele(new FloatAlleleValue(newValue), newMutationRate, newDominance);
        }

        return allele.copy();
    }
}