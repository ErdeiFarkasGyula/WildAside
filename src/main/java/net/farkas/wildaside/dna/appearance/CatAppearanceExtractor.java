package net.farkas.wildaside.dna.appearance;

import net.farkas.wildaside.dna.Gene;
import net.farkas.wildaside.dna.allele.Allele;
import net.farkas.wildaside.dna.trait.Trait;
import net.farkas.wildaside.dna.trait.TraitRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Cat;

import java.util.Collection;
import java.util.Map;

public class CatAppearanceExtractor implements IAppearanceGeneExtractor<Cat> {
    @Override
    public EntityType<Cat> type() {
        return EntityType.CAT;
    }

    @Override
    public Trait trait() {
        return TraitRegistry.CAT_VARIANT;
    }

    @Override
    public void extract(Cat cat, Map<Trait, Gene> genes, long seed) {
        ResourceLocation current = BuiltInRegistries.CAT_VARIANT.getKey(cat.getVariant());

        ResourceLocation other = pickOtherVariant(seed, current);

        Allele a = AppearanceAlleleHelper.createVariantAllele(current, seed, "cat_var_a");
        Allele b = AppearanceAlleleHelper.createVariantAllele(other, seed, "cat_var_b");

        genes.put(TraitRegistry.CAT_VARIANT, new Gene(TraitRegistry.CAT_VARIANT, a, b));
    }

    private ResourceLocation pickOtherVariant(long seed, ResourceLocation exclude) {
        var all = BuiltInRegistries.CAT_VARIANT.keySet()
                .stream()
                .filter(v -> !v.equals(exclude))
                .toList();

        int idx = (int) (Math.abs(seed * 17) % all.size());
        return all.get(idx);
    }

    @Override
    public Collection<String> getSuggestions() {
        return BuiltInRegistries.CAT_VARIANT.keySet()
                .stream()
                .map(ResourceLocation::toString)
                .toList();
    }
}
