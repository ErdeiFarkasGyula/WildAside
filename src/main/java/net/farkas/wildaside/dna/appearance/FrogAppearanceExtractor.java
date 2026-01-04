package net.farkas.wildaside.dna.appearance;

import net.farkas.wildaside.dna.locus.GeneLocus;
import net.farkas.wildaside.dna.allele.Allele;
import net.farkas.wildaside.dna.trait.Trait;
import net.farkas.wildaside.dna.trait.TraitRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.frog.Frog;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FrogAppearanceExtractor implements IAppearanceGeneExtractor<Frog> {
    @Override public EntityType<Frog> type() { return EntityType.FROG; }
    @Override public Trait trait() { return TraitRegistry.FROG_VARIANT; }

    @Override
    public void extract(Frog frog, Map<Trait, List<GeneLocus>> loci, long seed) {
        ResourceLocation current = BuiltInRegistries.FROG_VARIANT.getKey(frog.getVariant());
        ResourceLocation other = pickOtherVariant(seed, current);
        Allele a = AppearanceAlleleHelper.createVariantAllele(current, seed, "frog_var_a");
        Allele b = AppearanceAlleleHelper.createVariantAllele(other, seed, "frog_var_b");
        loci.put(TraitRegistry.FROG_VARIANT, List.of(
                new GeneLocus("frog_variant", a, b, Set.of(), TraitRegistry.FROG_VARIANT.getInstabilityModifier())
        ));
    }

    private ResourceLocation pickOtherVariant(long seed, ResourceLocation exclude) {
        var all = BuiltInRegistries.FROG_VARIANT.keySet().stream().filter(v -> !v.equals(exclude)).toList();
        int idx = (int) (Math.abs(seed * 31) % all.size());
        return all.get(idx);
    }

    @Override public Collection<String> getSuggestions() {
        return BuiltInRegistries.FROG_VARIANT.keySet().stream().map(ResourceLocation::toString).toList();
    }
}