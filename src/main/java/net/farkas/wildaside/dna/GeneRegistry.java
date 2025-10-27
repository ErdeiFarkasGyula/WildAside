package net.farkas.wildaside.dna;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class GeneRegistry {
    private static final Map<String, Function<Float, Gene>> REGISTRY = new HashMap<>();

    public static void register(String id, Function<Float, Gene> factory) {
        REGISTRY.put(id, factory);
    }

    @Nullable
    public static Gene createGene(String id, float cost) {
        Function<Float, Gene> factory = REGISTRY.get(id);
        return factory != null ? factory.apply(cost) : null;
    }
}