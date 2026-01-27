package net.farkas.wildaside.dna.breeding;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.capability.dna.DnaCapability;
import net.farkas.wildaside.capability.dna.IDna;
import net.farkas.wildaside.dna.DnaUtils;
import net.farkas.wildaside.dna.chromosome.ChromosomeSet;
import net.farkas.wildaside.dna.chromosome.Genome;
import net.farkas.wildaside.dna.expression.ActivationCondition;
import net.farkas.wildaside.dna.expression.RegulationType;
import net.farkas.wildaside.dna.sequence.CombineMethod;
import net.farkas.wildaside.dna.sequence.GeneSource;
import net.farkas.wildaside.dna.sequence.GeneSequence;
import net.farkas.wildaside.dna.sequence.components.*;
import net.farkas.wildaside.dna.trait.Trait;
import net.minecraft.world.entity.AgeableMob;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BreedingHandler {
    private static final float CROSSOVER_CHANCE = 0.7f;
    private static final float MUTATION_CHANCE = 0.1f;

    private static final float VALUE_MUTATION_MAGNITUDE = 0.1f;
    private static final float ENUM_MUTATION_CHANCE = 0.05f;

    private static final float DUPLICATION_CHANCE = 0.02f;
    private static final float DELETION_CHANCE = 0.02f;
    private static final float ADDITION_CHANCE = 0.01f;


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
            GeneSequence p1Sequence = random.nextBoolean() ? p1Maternal.getSequence(trait) : p1Paternal.getSequence(trait);
            GeneSequence p2Sequence = random.nextBoolean() ? p2Maternal.getSequence(trait) : p2Paternal.getSequence(trait);

            GeneSequence childMaternal = inheritSequence(p1Sequence, p2Sequence, trait, random);
            GeneSequence childPaternal = inheritSequence(p1Sequence, p2Sequence, trait, random);

            if (childMaternal != null) {
                childMaternalSet.putSequence(trait, childMaternal);
            }
            if (childPaternal != null) {
                childPaternalSet.putSequence(trait, childPaternal);
            }
        }

        return new Genome(child.getType(), childMaternalSet, childPaternalSet);
    }

    private static GeneSequence inheritSequence(GeneSequence seq1, GeneSequence seq2, Trait trait, Random random) {
        if (seq1 == null && seq2 == null) return null;
        if (seq1 == null) return mutateSequence(seq2, random, trait);
        if (seq2 == null) return mutateSequence(seq1, random, trait);

        List<GeneComponent> childComponents;

        if (random.nextFloat() < CROSSOVER_CHANCE) {
            childComponents = crossoverComponents(seq1.getComponents(), seq2.getComponents(), random);
        } else {
            childComponents = new ArrayList<>(random.nextBoolean() ? seq1.getComponents() : seq2.getComponents());
        }

        List<GeneComponent> mutatedComponents = mutateComponents(childComponents, random);
        mutatedComponents = applyStructuralMutations(mutatedComponents, random);

        float newStability = (seq1.getStability() + seq2.getStability()) / 2f * (1f - (random.nextFloat() * 0.1f));
        newStability = Math.max(0.1f, Math.min(1.0f, newStability));

        return new GeneSequence.Builder()
                .trait(trait)
                .addComponents(mutatedComponents)
                .stability(newStability)
                .source(GeneSource.NATURAL)
                .build();
    }

    private static GeneSequence mutateSequence(GeneSequence seq, Random random, Trait trait) {
        List<GeneComponent> mutatedComponents = mutateComponents(new ArrayList<>(seq.getComponents()), random);
        mutatedComponents = applyStructuralMutations(mutatedComponents, random);

        float newStability = seq.getStability() * (1f - (random.nextFloat() * 0.05f));
        newStability = Math.max(0.1f, Math.min(1.0f, newStability));

        return new GeneSequence.Builder()
                .trait(trait)
                .addComponents(mutatedComponents)
                .stability(newStability)
                .source(GeneSource.MUTATED)
                .build();
    }

    private static List<GeneComponent> crossoverComponents(List<GeneComponent> comp1, List<GeneComponent> comp2, Random random) {
        List<GeneComponent> p1, p2;
        if (random.nextBoolean()) {
            p1 = new ArrayList<>(comp1);
            p2 = new ArrayList<>(comp2);
        } else {
            p1 = new ArrayList<>(comp2);
            p2 = new ArrayList<>(comp1);
        }

        if (p1.isEmpty() || !(p1.get(0) instanceof TraitDefiner)) {
            return p2.isEmpty() ? p1 : p2;
        }
        GeneComponent traitDefiner = p1.get(0);
        p1.remove(0);
        if (!p2.isEmpty() && p2.get(0) instanceof TraitDefiner) {
            p2.remove(0);
        }

        List<GeneComponent> childComps = new ArrayList<>();
        childComps.add(traitDefiner);

        int size1 = p1.size();
        int size2 = p2.size();
        if (size1 == 0) {
            childComps.addAll(p2);
            return childComps;
        }
        if (size2 == 0) {
            childComps.addAll(p1);
            return childComps;
        }

        int crossoverPoint = random.nextInt(Math.min(size1, size2) + 1);

        childComps.addAll(p1.subList(0, crossoverPoint));
        childComps.addAll(p2.subList(crossoverPoint, size2));

        return childComps;
    }

    private static List<GeneComponent> mutateComponents(List<GeneComponent> components, Random random) {
        List<GeneComponent> newComponents = new ArrayList<>();
        for (GeneComponent component : components) {
            if (random.nextFloat() < MUTATION_CHANCE) {
                newComponents.add(mutateComponent(component, random));
            } else {
                newComponents.add(component);
            }
        }
        return newComponents;
    }

    private static List<GeneComponent> applyStructuralMutations(List<GeneComponent> components, Random random) {
        List<GeneComponent> result = new ArrayList<>(components);

        if (random.nextFloat() < DUPLICATION_CHANCE && result.size() > 1) {
            int index = 1 + random.nextInt(result.size() - 1);
            result.add(index, result.get(index));
        }

        if (random.nextFloat() < DELETION_CHANCE && result.size() > 1) {
            int index = 1 + random.nextInt(result.size() - 1);
            result.remove(index);
        }

        if (random.nextFloat() < ADDITION_CHANCE) {
            String newId = "mutated_cr_" + random.nextInt(1000);
            float newValue = (random.nextFloat() - 0.5f) * 2f;
            CombineMethod newMethod = CombineMethod.values()[random.nextInt(CombineMethod.values().length)];
            CodingRegion newCr = new CodingRegion(newId, newValue, newMethod);

            if (result.size() > 1) {
                int index = 1 + random.nextInt(result.size() - 1);
                result.add(index, newCr);
            } else {
                result.add(newCr);
            }
        }

        return result;
    }

    private static GeneComponent mutateComponent(GeneComponent component, Random random) {
        float mutationMagnitude = (random.nextFloat() - 0.5f) * 2f * VALUE_MUTATION_MAGNITUDE;

        if (component instanceof CodingRegion cr) {
            float newValue = cr.getValue() * (1f + mutationMagnitude);
            CombineMethod newMethod = cr.getCombineMethod();
            if (random.nextFloat() < ENUM_MUTATION_CHANCE) {
                newMethod = CombineMethod.values()[random.nextInt(CombineMethod.values().length)];
            }
            return new CodingRegion(cr.getId(), newValue, newMethod);
        }
        if (component instanceof Activator act) {
            float newThreshold = act.getActivationThreshold() * (1f + mutationMagnitude);
            ActivationCondition newCondition = act.getCondition();
            if (random.nextFloat() < ENUM_MUTATION_CHANCE) {
                newCondition = ActivationCondition.values()[random.nextInt(ActivationCondition.values().length)];
            }
            return new Activator(act.getId(), newCondition, newThreshold);
        }
        if (component instanceof Enhancer enh) {
            float newThreshold = enh.getThreshold() * (1f + mutationMagnitude);
            float newMultiplier = enh.getMultiplier() * (1f + mutationMagnitude);
            float newFlatBonus = enh.getFlatBonus() * (1f + mutationMagnitude);
            return new Enhancer(enh.getId(), enh.getCondition(), newThreshold, newMultiplier, newFlatBonus);
        }
        if (component instanceof Silencer sil) {
            float newThreshold = sil.getThreshold() * (1f + mutationMagnitude);
            float newMultiplier = sil.getMultiplier() * (1f + mutationMagnitude);
            float newFlatPenalty = sil.getFlatPenalty() * (1f + mutationMagnitude);
            return new Silencer(sil.getId(), sil.getCondition(), newThreshold, newMultiplier, newFlatPenalty);
        }
        if (component instanceof Regulator reg) {
            float newValue = reg.getValue() * (1f + mutationMagnitude);
            RegulationType newType = reg.getRegulationType();
            if (random.nextFloat() < ENUM_MUTATION_CHANCE) {
                newType = RegulationType.values()[random.nextInt(RegulationType.values().length)];
            }
            return new Regulator(reg.getId(), newType, newValue);
        }

        return component;
    }
}
