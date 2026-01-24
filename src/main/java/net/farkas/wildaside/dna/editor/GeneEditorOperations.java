package net.farkas.wildaside.dna.editor;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.dna.bioengineering_skill.BioengineeringSkillUtils;
import net.farkas.wildaside.dna.chromosome.Chromosome;
import net.farkas.wildaside.dna.chromosome.Genome;
import net.farkas.wildaside.dna.sequence.components.CodingRegion;
import net.farkas.wildaside.dna.sequence.CombineMethod;
import net.farkas.wildaside.dna.sequence.GeneSequence;
import net.farkas.wildaside.dna.sequence.GeneSource;
import net.farkas.wildaside.dna.trait.Trait;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class GeneEditorOperations {
    public static boolean canPerformOperation(Player player, GeneEditorOperation operation) {
        ResourceLocation requiredSkill = operation.getRequiredSkillId();
        if (requiredSkill == null) return true;
        return BioengineeringSkillUtils.hasSkill(player, requiredSkill);
    }

    public static GeneEditorResult addCodingRegion(
            Genome genome, Trait trait, boolean maternal, 
            String id, float value, CombineMethod method) {
        
        Chromosome chromosome = maternal ? 
            genome.getMaternal().getChromosome(trait.getTraitType().getChromosomeType()) :
            genome.getPaternal().getChromosome(trait.getTraitType().getChromosomeType());
        
        if (chromosome == null) {
            return GeneEditorResult.failure("Chromosome not found for trait");
        }
        
        GeneSequence sequence = chromosome.getGeneSequence(trait);
        if (sequence == null) {
            return GeneEditorResult.failure("Gene sequence not found");
        }
        
        List<CodingRegion> newRegions = new ArrayList<>(sequence.getCodingRegions());
        if (newRegions.size() >= 6) {
            return GeneEditorResult.failure("Maximum coding regions reached (6)");
        }
        
        CodingRegion newRegion = new CodingRegion(id, value, method);
        newRegions.add(newRegion);
        
        GeneSequence.Builder builder = rebuildSequence(sequence);
        newRegions.forEach(builder::codingRegion);
        
        chromosome.setGeneSequence(trait, builder.build());
        
        WildAside.LOGGER.info("Added coding region {} to {} chromosome for trait [{}]", 
            id, maternal ? "maternal" : "paternal", trait.getName());
        
        return GeneEditorResult.success("Coding region added", 10f);
    }
    
    public static GeneEditorResult removeCodingRegion(
            Genome genome, Trait trait, boolean maternal, String componentId) {
        
        Chromosome chromosome = maternal ? 
            genome.getMaternal().getChromosome(trait.getTraitType().getChromosomeType()) :
            genome.getPaternal().getChromosome(trait.getTraitType().getChromosomeType());
        
        if (chromosome == null) {
            return GeneEditorResult.failure("Chromosome not found");
        }
        
        GeneSequence sequence = chromosome.getGeneSequence(trait);
        if (sequence == null) {
            return GeneEditorResult.failure("Gene sequence not found");
        }
        
        List<CodingRegion> regions = new ArrayList<>(sequence.getCodingRegions());
        boolean removed = regions.removeIf(r -> r.getId().equals(componentId));
        
        if (!removed) {
            return GeneEditorResult.failure("Coding region not found");
        }
        
        if (regions.isEmpty()) {
            return GeneEditorResult.failure("Cannot remove last coding region");
        }
        
        GeneSequence.Builder builder = rebuildSequence(sequence);
        
        builder = rebuildSequenceBase(sequence);
        regions.forEach(builder::codingRegion);
        sequence.getActivators().forEach(builder::activator);
        sequence.getEnhancers().forEach(builder::enhancer);
        sequence.getSilencers().forEach(builder::silencer);
        sequence.getRegulators().forEach(builder::regulator);
        
        chromosome.setGeneSequence(trait, builder.build());
        
        WildAside.LOGGER.info("Removed coding region {} from trait [{}]", componentId, trait.getName());
        return GeneEditorResult.success("Coding region removed", 15f);
    }

    private static GeneSequence.Builder rebuildSequenceBase(GeneSequence sequence) {
        return GeneSequence.builder()
                .trait(sequence.getTrait())
                .dominance(sequence.getDominance())
                .mutationRate(sequence.getMutationRate())
                .stability(sequence.getStability())
                .source(sequence.getSource());
    }

    private static GeneSequence.Builder rebuildSequence(GeneSequence sequence) {
        GeneSequence.Builder builder = rebuildSequenceBase(sequence);
        return builder;
    }
    
    public static GeneEditorResult stabilizeSequence(
            Genome genome, Trait trait, boolean maternal, float amount) {
        
        Chromosome chromosome = maternal ? 
            genome.getMaternal().getChromosome(trait.getTraitType().getChromosomeType()) :
            genome.getPaternal().getChromosome(trait.getTraitType().getChromosomeType());
        
        if (chromosome == null) {
            return GeneEditorResult.failure("Chromosome not found");
        }
        
        GeneSequence sequence = chromosome.getGeneSequence(trait);
        if (sequence == null) {
            return GeneEditorResult.failure("Gene sequence not found");
        }
        
        float newStability = Math.min(1.0f, sequence.getStability() + amount);
        
        GeneSequence.Builder builder = rebuildSequenceBase(sequence);
        builder.stability(newStability);
        
        sequence.getCodingRegions().forEach(builder::codingRegion);
        sequence.getActivators().forEach(builder::activator);
        sequence.getEnhancers().forEach(builder::enhancer);
        sequence.getSilencers().forEach(builder::silencer);
        sequence.getRegulators().forEach(builder::regulator);
        
        chromosome.setGeneSequence(trait, builder.build());
        
        WildAside.LOGGER.info("Stabilized gene sequence for trait [{}] to {}", trait.getName(), newStability);
        return GeneEditorResult.success("Gene sequence stabilized", 5f);
    }
    
    public static GeneEditorResult swapSequences(
            Genome genome, Trait trait) {
        
        Chromosome maternalChrom = genome.getMaternal().getChromosome(trait.getTraitType().getChromosomeType());
        Chromosome paternalChrom = genome.getPaternal().getChromosome(trait.getTraitType().getChromosomeType());
        
        if (maternalChrom == null || paternalChrom == null) {
            return GeneEditorResult.failure("Chromosomes not found");
        }
        
        GeneSequence maternalSeq = maternalChrom.getGeneSequence(trait);
        GeneSequence paternalSeq = paternalChrom.getGeneSequence(trait);
        
        if (maternalSeq == null || paternalSeq == null) {
            return GeneEditorResult.failure("Gene sequences not found");
        }
        
        maternalChrom.setGeneSequence(trait, paternalSeq);
        paternalChrom.setGeneSequence(trait, maternalSeq);
        
        WildAside.LOGGER.info("Swapped maternal and paternal sequences for trait [{}]", trait.getName());
        return GeneEditorResult.success("Sequences swapped", 12f);
    }
    
    public static GeneEditorResult integrateSequence(
            Genome genome, Trait trait, boolean maternal) {
        
        Chromosome chromosome = maternal ? 
            genome.getMaternal().getChromosome(trait.getTraitType().getChromosomeType()) :
            genome.getPaternal().getChromosome(trait.getTraitType().getChromosomeType());
        
        if (chromosome == null) {
            return GeneEditorResult.failure("Chromosome not found");
        }
        
        GeneSequence sequence = chromosome.getGeneSequence(trait);
        if (sequence == null) {
            return GeneEditorResult.failure("Gene sequence not found");
        }
        
        if (sequence.getSource() == GeneSource.NATURAL) {
             return GeneEditorResult.failure("Sequence is already natural");
        }
        
        if (sequence.getStability() < 0.6f) {
            return GeneEditorResult.failure("Stability too low for integration (need 0.6+)");
        }
        
        GeneSequence.Builder builder = rebuildSequenceBase(sequence);
        builder.source(GeneSource.INTEGRATED);
        
        sequence.getCodingRegions().forEach(builder::codingRegion);
        sequence.getActivators().forEach(builder::activator);
        sequence.getEnhancers().forEach(builder::enhancer);
        sequence.getSilencers().forEach(builder::silencer);
        sequence.getRegulators().forEach(builder::regulator);
        
        chromosome.setGeneSequence(trait, builder.build());
        
        WildAside.LOGGER.info("Integrated invading sequence for trait [{}]", trait.getName());
        return GeneEditorResult.success("Sequence integrated", 25f);
    }
}