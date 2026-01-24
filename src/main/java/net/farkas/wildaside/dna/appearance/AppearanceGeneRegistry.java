package net.farkas.wildaside.dna.appearance;

import net.farkas.wildaside.dna.chromosome.Genome;
import net.farkas.wildaside.dna.sequence.GeneSequence;
import net.farkas.wildaside.dna.trait.Trait;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

import java.util.*;

public class AppearanceGeneRegistry {
    private static final Map<EntityType<?>, IAppearanceGeneExtractor<?>> EXTRACTORS = new HashMap<>();

    public static <T extends LivingEntity> void register(IAppearanceGeneExtractor<T> extractor) {
        EXTRACTORS.put(extractor.type(), extractor);
    }

    static {
        register(new CatAppearanceExtractor());
        register(new FrogAppearanceExtractor());
    }

    public static GeneSequence[] extract(LivingEntity entity, Genome genome, long seed) {
        IAppearanceGeneExtractor extractor = EXTRACTORS.get(entity.getType());
        if (extractor != null) return extractor.extract(entity, genome, seed);
        return new GeneSequence[0];
    }

    public static Collection<String> getSuggestions(LivingEntity entity, Trait trait) {
        IAppearanceGeneExtractor<?> extractor = EXTRACTORS.get(entity.getType());
        if (extractor == null || extractor.trait() != trait) return List.of();
        return extractor.getSuggestions();
    }
}