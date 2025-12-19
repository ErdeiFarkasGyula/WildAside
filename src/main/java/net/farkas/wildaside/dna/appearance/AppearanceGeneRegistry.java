package net.farkas.wildaside.dna.appearance;

import net.farkas.wildaside.dna.Gene;
import net.farkas.wildaside.dna.trait.Trait;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppearanceGeneRegistry {
    private static final Map<EntityType<?>, IAppearanceGeneExtractor<?>> EXTRACTORS = new HashMap<>();

    public static <T extends LivingEntity> void register(IAppearanceGeneExtractor<T> extractor) {
        EXTRACTORS.put(extractor.type(), extractor);
    }

    static {
        AppearanceGeneRegistry.register(new CatAppearanceExtractor());
        AppearanceGeneRegistry.register(new FrogAppearanceExtractor());
    }

    public static void extract(LivingEntity entity, Map<Trait, Gene> genes, long seed) {
        IAppearanceGeneExtractor extractor = EXTRACTORS.get(entity.getType());
        if (extractor != null) {
            extractor.extract(entity, genes, seed);
        }
    }

    public static Collection<String> getSuggestions(LivingEntity entity, Trait trait) {
        IAppearanceGeneExtractor<?> extractor = EXTRACTORS.get(entity.getType());
        if (extractor == null) return List.of();

        if (extractor.trait() != trait) return List.of();

        return extractor.getSuggestions();
    }

}
