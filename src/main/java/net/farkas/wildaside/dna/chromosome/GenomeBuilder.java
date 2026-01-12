package net.farkas.wildaside.dna.chromosome;

import net.farkas.wildaside.dna.sequence.GeneSequence;
import net.farkas.wildaside.dna.trait.Trait;
import net.minecraft.world.entity.EntityType;

public class GenomeBuilder {
    private final EntityType<?> entityType;
    private final ChromosomeSet maternalSet;
    private final ChromosomeSet paternalSet;

    public GenomeBuilder(EntityType<?> entityType) {
        this.entityType = entityType;
        this.maternalSet = new ChromosomeSet();
        this.paternalSet = new ChromosomeSet();
    }

    public GenomeBuilder addMaternalSequence(Trait trait, GeneSequence sequence) {
        ChromosomeType chromoType = trait.getTraitType().getChromosomeType();
        Chromosome chromosome = maternalSet.getChromosome(chromoType);
        chromosome.setGeneSequence(trait, sequence);
        return this;
    }

    public GenomeBuilder addPaternalSequence(Trait trait, GeneSequence sequence) {
        ChromosomeType chromoType = trait.getTraitType().getChromosomeType();
        Chromosome chromosome = paternalSet.getChromosome(chromoType);
        chromosome.setGeneSequence(trait, sequence);
        return this;
    }

    public GenomeBuilder addBothSequences(Trait trait, GeneSequence sequence) {
        addMaternalSequence(trait, sequence);
        addPaternalSequence(trait, sequence);
        return this;
    }

    public Genome build() {
        return new Genome(entityType, maternalSet, paternalSet);
    }

    public static GenomeBuilder forEntityType(EntityType<?> entityType) {
        return new GenomeBuilder(entityType);
    }
}
