package net.farkas.wildaside.dna.chromosome;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.Collection;
import java.util.EnumMap;
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

    public void setChromosome(ChromosomeType type, Chromosome chromosome) {
        chromosomes.put(type, chromosome);
    }

    public Collection<Chromosome> getAllChromosomes() {
        return chromosomes.values();
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
