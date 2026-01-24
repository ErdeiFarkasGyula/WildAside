package net.farkas.wildaside.dna.appearance;

import net.farkas.wildaside.dna.chromosome.Genome;
import net.farkas.wildaside.dna.sequence.GeneSequence;
import net.farkas.wildaside.dna.trait.Trait;
import net.farkas.wildaside.dna.trait.TraitRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.frog.Frog;

import java.util.Collection;

public class FrogAppearanceExtractor implements IAppearanceGeneExtractor<Frog> {
    @Override public EntityType<Frog> type() { return EntityType.FROG; }
    @Override public Trait trait() { return TraitRegistry.FROG_VARIANT; }

    @Override
    public GeneSequence[] extract(Frog entity, Genome genome, long seed) {
        ResourceLocation current = BuiltInRegistries.FROG_VARIANT.getKey(entity.getVariant());
        ResourceLocation other = pickOtherVariant(seed, current);

        GeneSequence maternal = AppearanceUtils.createVariantSequence(trait(), current, seed, "frog_var_m");
        GeneSequence paternal = AppearanceUtils.createVariantSequence(trait(), other, seed, "frog_var_p");
        
        genome.getMaternal().getChromosome(trait().getTraitType().getChromosomeType()).setGeneSequence(trait(), maternal);
        genome.getPaternal().getChromosome(trait().getTraitType().getChromosomeType()).setGeneSequence(trait(), paternal);

        return new GeneSequence[]{maternal, paternal};
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