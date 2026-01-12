package net.farkas.wildaside.dna.chromosome;

import net.farkas.wildaside.dna.sequence.GeneSequence;
import net.farkas.wildaside.dna.trait.Trait;
import net.minecraft.world.entity.EntityType;

/**
 * Builder utility for creating Genomes with maternal and paternal chromosome sets.
 * This simplifies the construction of complex genome structures.
 */
public class GenomeBuilder {
    private final EntityType<?> entityType;
    private final ChromosomeSet maternalSet;
    private final ChromosomeSet paternalSet;

    public GenomeBuilder(EntityType<?> entityType) {
        this.entityType = entityType;
        this.maternalSet = new ChromosomeSet();
        this.paternalSet = new ChromosomeSet();
    }

    /**
     * Adds a gene sequence to the maternal chromosome set.
     * 
     * @param trait The trait this gene sequence affects
     * @param sequence The gene sequence to add
     * @return This builder for chaining
     */
    public GenomeBuilder addMaternalSequence(Trait trait, GeneSequence sequence) {
        ChromosomeType chromoType = trait.getTraitType().getChromosomeType();
        Chromosome chromosome = maternalSet.getChromosome(chromoType);
        chromosome.setGeneSequence(trait, sequence);
        return this;
    }

    /**
     * Adds a gene sequence to the paternal chromosome set.
     * 
     * @param trait The trait this gene sequence affects
     * @param sequence The gene sequence to add
     * @return This builder for chaining
     */
    public GenomeBuilder addPaternalSequence(Trait trait, GeneSequence sequence) {
        ChromosomeType chromoType = trait.getTraitType().getChromosomeType();
        Chromosome chromosome = paternalSet.getChromosome(chromoType);
        chromosome.setGeneSequence(trait, sequence);
        return this;
    }

    /**
     * Adds the same gene sequence to both maternal and paternal chromosome sets.
     * Useful for creating homozygous traits.
     * 
     * @param trait The trait this gene sequence affects
     * @param sequence The gene sequence to add to both sets
     * @return This builder for chaining
     */
    public GenomeBuilder addBothSequences(Trait trait, GeneSequence sequence) {
        addMaternalSequence(trait, sequence);
        addPaternalSequence(trait, sequence);
        return this;
    }

    /**
     * Builds the final Genome object.
     * 
     * @return The constructed Genome
     */
    public Genome build() {
        return new Genome(entityType, maternalSet, paternalSet);
    }

    /**
     * Creates a new GenomeBuilder for the specified entity type.
     * 
     * @param entityType The entity type for this genome
     * @return A new GenomeBuilder instance
     */
    public static GenomeBuilder forEntityType(EntityType<?> entityType) {
        return new GenomeBuilder(entityType);
    }
}
