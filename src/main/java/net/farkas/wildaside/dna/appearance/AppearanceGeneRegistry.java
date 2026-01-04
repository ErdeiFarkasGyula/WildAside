package net.farkas.wildaside.dna.appearance;

import net.farkas.wildaside.dna.locus.GeneLocus;
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

    public static void extract(LivingEntity entity, Map<Trait, List<GeneLocus>> loci, long seed) {
        IAppearanceGeneExtractor extractor = EXTRACTORS.get(entity.getType());
        if (extractor != null) extractor.extract(entity, loci, seed);
    }

    public static Collection<String> getSuggestions(LivingEntity entity, Trait trait) {
        IAppearanceGeneExtractor<?> extractor = EXTRACTORS.get(entity.getType());
        if (extractor == null || extractor.trait() != trait) return List.of();
        return extractor.getSuggestions();
    }
}