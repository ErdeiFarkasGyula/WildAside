package net.farkas.wildaside.dna.chromosome;

import net.farkas.wildaside.dna.sequence.GeneSequence;
import net.farkas.wildaside.dna.trait.Trait;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class ChromosomeSet {
    private final Map<ChromosomeType, Chromosome> chromosomes;

    public ChromosomeSet() {
        this.chromosomes = new EnumMap<>(ChromosomeType.class);
        for (ChromosomeType type : ChromosomeType.values()) {
            chromosomes.put(type, new Chromosome(type));
        }
    }

    public ChromosomeSet(Map<ChromosomeType, Chromosome> chromosomes) {
        this.chromosomes = new EnumMap<>(chromosomes);
    }

    public Chromosome getChromosome(ChromosomeType type) {
        return chromosomes.get(type);
    }
    
    public Chromosome getChromosome(Trait trait) {
        return getChromosome(trait.getTraitType().getChromosomeType());
    }

    public void setChromosome(ChromosomeType type, Chromosome chromosome) {
        chromosomes.put(type, chromosome);
    }

    public Collection<Chromosome> getAllChromosomes() {
        return chromosomes.values();
    }
    
    public List<GeneSequence> getAllSequences() {
        List<GeneSequence> sequences = new ArrayList<>();
        for (Chromosome chromosome : chromosomes.values()) {
            sequences.addAll(chromosome.getAllGeneSequences());
        }
        return sequences;
    }
    
    public GeneSequence getSequence(Trait trait) {
        Chromosome chromo = getChromosome(trait.getTraitType().getChromosomeType());
        if (chromo == null) return null;
        return chromo.getGeneSequence(trait);
    }
    
    public void putSequence(Trait trait, GeneSequence sequence) {
        Chromosome chromo = getChromosome(trait.getTraitType().getChromosomeType());
        if (chromo == null) {
            chromo = new Chromosome(trait.getTraitType().getChromosomeType());
            setChromosome(trait.getTraitType().getChromosomeType(), chromo);
        }
        chromo.setGeneSequence(trait, sequence);
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();

        ListTag chromosomesTag = new ListTag();
        for (Chromosome chromosome : chromosomes.values()) {
            chromosomesTag.add(chromosome.serializeNBT());
        }
        tag.put("Chromosomes", chromosomesTag);

        return tag;
    }

    public static ChromosomeSet deserializeNBT(CompoundTag tag) {
        ChromosomeSet chromosomeSet = new ChromosomeSet();

        if (tag.contains("Chromosomes")) {
            ListTag chromosomesTag = tag.getList("Chromosomes", Tag.TAG_COMPOUND);
            for (Tag t : chromosomesTag) {
                Chromosome chromosome = Chromosome.deserializeNBT((CompoundTag) t);
                chromosomeSet.setChromosome(chromosome.getType(), chromosome);
            }
        }

        return chromosomeSet;
    }
}