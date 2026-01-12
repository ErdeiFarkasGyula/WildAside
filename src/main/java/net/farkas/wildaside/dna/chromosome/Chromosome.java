package net.farkas.wildaside.dna.chromosome;

import net.farkas.wildaside.dna.sequence.GeneSequence;
import net.farkas.wildaside.dna.trait.Trait;
import net.farkas.wildaside.dna.trait.TraitRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Chromosome {
    private final ChromosomeType type;
    private final Map<Trait, GeneSequence> geneSequences;

    public Chromosome(ChromosomeType type) {
        this.type = type;
        this.geneSequences = new HashMap<>();
    }

    public Chromosome(ChromosomeType type, Map<Trait, GeneSequence> geneSequences) {
        this.type = type;
        this.geneSequences = new HashMap<>(geneSequences);
    }

    public GeneSequence getGeneSequence(Trait trait) {
        return geneSequences.get(trait);
    }

    public void setGeneSequence(Trait trait, GeneSequence sequence) {
        geneSequences.put(trait, sequence);
    }

    public Collection<GeneSequence> getAllGeneSequences() {
        return geneSequences.values();
    }

    public ChromosomeType getType() {
        return type;
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Type", type.name());

        ListTag sequencesTag = new ListTag();
        for (Map.Entry<Trait, GeneSequence> entry : geneSequences.entrySet()) {
            CompoundTag seqTag = entry.getValue().serializeNBT();
            seqTag.putString("Trait", entry.getKey().getName());
            sequencesTag.add(seqTag);
        }
        tag.put("GeneSequences", sequencesTag);

        return tag;
    }

    public static Chromosome deserializeNBT(CompoundTag tag) {
        ChromosomeType type = ChromosomeType.valueOf(tag.getString("Type"));
        Chromosome chromosome = new Chromosome(type);

        ListTag sequencesTag = tag.getList("GeneSequences", Tag.TAG_COMPOUND);
        for (Tag t : sequencesTag) {
            CompoundTag seqTag = (CompoundTag) t;
            Trait trait = TraitRegistry.getByName(seqTag.getString("Trait"));
            if (trait != null) {
                GeneSequence sequence = GeneSequence.deserializeNBT(seqTag, trait);
                chromosome.setGeneSequence(trait, sequence);
            }
        }

        return chromosome;
    }
}
