package net.farkas.wildaside.dna.appearance;

import net.farkas.wildaside.dna.Gene;
import net.farkas.wildaside.dna.trait.Trait;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;

public class AppearanceGeneRegistry {
    private static final Map<EntityType<?>, AppearanceGeneExtractor<?>> EXTRACTORS = new HashMap<>();

    public static <T extends LivingEntity> void register(AppearanceGeneExtractor<T> extractor) {
        EXTRACTORS.put(extractor.type(), extractor);
    }

    public static void extract(LivingEntity entity, Map<Trait, Gene> genes, long seed) {
        AppearanceGeneExtractor extractor = EXTRACTORS.get(entity.getType());
        if (extractor != null) {
            extractor.extract(entity, genes, seed);
        }
    }

    static {
        AppearanceGeneRegistry.register(new CatAppearanceExtractor());
        AppearanceGeneRegistry.register(new FrogAppearanceExtractor());
    }
}
