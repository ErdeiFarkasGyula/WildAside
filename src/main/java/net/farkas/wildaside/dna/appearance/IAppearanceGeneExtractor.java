package net.farkas.wildaside.dna.appearance;

import net.farkas.wildaside.dna.chromosome.Genome;
import net.farkas.wildaside.dna.sequence.GeneSequence;
import net.farkas.wildaside.dna.trait.Trait;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

import java.util.Collection;
import java.util.List;

public interface IAppearanceGeneExtractor<T extends LivingEntity> {
    EntityType<T> type();
    Trait trait();
    GeneSequence[] extract(T entity, Genome genome, long seed);
    default Collection<String> getSuggestions() { return List.of(); }
}