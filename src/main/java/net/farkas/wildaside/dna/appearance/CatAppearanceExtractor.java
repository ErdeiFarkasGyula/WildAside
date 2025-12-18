package net.farkas.wildaside.dna.appearance;

import net.farkas.wildaside.dna.Gene;
import net.farkas.wildaside.dna.allele.Allele;
import net.farkas.wildaside.dna.allele.value.ResourceLocationAlleleValue;
import net.farkas.wildaside.dna.dominance.Dominance;
import net.farkas.wildaside.dna.trait.Trait;
import net.farkas.wildaside.dna.trait.TraitRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Cat;

import java.util.List;
import java.util.Map;

public class CatAppearanceExtractor implements AppearanceGeneExtractor<Cat> {
    @Override
    public EntityType<Cat> type() {
        return EntityType.CAT;
    }

    @Override
    public void extract(Cat cat, Map<Trait, Gene> genes, long seed) {
        ResourceLocation current = BuiltInRegistries.CAT_VARIANT.getKey(cat.getVariant());

        Allele alleleA = createVariantAllele(
                current,
                seed,
                "cat_variant_a",
                Dominance.DOMINANT
        );

        Allele alleleB = createVariantAllele(
                pickRandomCatVariant(seed, current),
                seed,
                "cat_variant_b",
                Dominance.RECESSIVE
        );

        genes.put(
                TraitRegistry.CAT_VARIANT,
                new Gene(TraitRegistry.CAT_VARIANT, alleleA, alleleB)
        );
    }

    private Allele createVariantAllele(
            ResourceLocation variant,
            long seed,
            String salt,
            Dominance dom
    ) {
        return new Allele(
                new ResourceLocationAlleleValue(variant),
                0.02f,
                0.6f,
                dom
        );
    }

    private ResourceLocation pickRandomCatVariant(long seed, ResourceLocation exclude) {
        List<ResourceLocation> all = BuiltInRegistries.CAT_VARIANT.keySet()
                .stream()
                .filter(v -> !v.equals(exclude))
                .toList();

        int idx = (int) (Math.abs(seed) % all.size());
        return all.get(idx);
    }
}
