package net.farkas.wildaside.dna.appearance;

import net.farkas.wildaside.dna.Gene;
import net.farkas.wildaside.dna.allele.Allele;
import net.farkas.wildaside.dna.trait.Trait;
import net.farkas.wildaside.dna.trait.TraitRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.frog.Frog;

import java.util.Collection;
import java.util.Map;

public class FrogAppearanceExtractor implements IAppearanceGeneExtractor<Frog> {
    @Override
    public EntityType<Frog> type() {
        return EntityType.FROG;
    }

    @Override
    public Trait trait() {
        return TraitRegistry.FROG_VARIANT;
    }

    @Override
    public void extract(Frog frog, Map<Trait, Gene> genes, long seed) {
        ResourceLocation current = BuiltInRegistries.FROG_VARIANT.getKey(frog.getVariant());

        ResourceLocation other = pickOtherVariant(seed, current);

        Allele a = AppearanceAlleleHelper.createVariantAllele(current, seed, "frog_var_a");
        Allele b = AppearanceAlleleHelper.createVariantAllele(other, seed, "frog_var_b");

        genes.put(TraitRegistry.FROG_VARIANT, new Gene(TraitRegistry.FROG_VARIANT, a, b));
    }

    private ResourceLocation pickOtherVariant(long seed, ResourceLocation exclude) {
        var all = BuiltInRegistries.FROG_VARIANT.keySet()
                .stream()
                .filter(v -> !v.equals(exclude))
                .toList();

        int idx = (int) (Math.abs(seed * 31) % all.size());
        return all.get(idx);
    }

    @Override
    public Collection<String> getSuggestions() {
        return BuiltInRegistries.FROG_VARIANT.keySet()
                .stream()
                .map(ResourceLocation::toString)
                .toList();
    }
}
