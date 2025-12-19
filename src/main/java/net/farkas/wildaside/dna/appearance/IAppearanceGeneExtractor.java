package net.farkas.wildaside.dna.appearance;

import net.farkas.wildaside.dna.Gene;
import net.farkas.wildaside.dna.trait.Trait;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface IAppearanceGeneExtractor<T extends LivingEntity> {
    EntityType<T> type();
    Trait trait();

    void extract(T entity, Map<Trait, Gene> genes, long seed);

    default Collection<String> getSuggestions() {
        return List.of();
    }
}
