package net.farkas.wildaside.dna.appearance;

import net.farkas.wildaside.dna.Gene;
import net.farkas.wildaside.dna.trait.Trait;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

import java.util.Map;

public interface AppearanceGeneExtractor<T extends LivingEntity> {
    EntityType<T> type();

    void extract(
            T entity,
            Map<Trait, Gene> genes,
            long seed
    );
}
