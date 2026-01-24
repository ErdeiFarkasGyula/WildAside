package net.farkas.wildaside.dna.appearance;

import net.farkas.wildaside.dna.chromosome.Genome;
import net.farkas.wildaside.dna.sequence.GeneSequence;
import net.farkas.wildaside.dna.trait.Trait;
import net.farkas.wildaside.dna.trait.TraitRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Cat;

import java.util.Collection;

public class CatAppearanceExtractor implements IAppearanceGeneExtractor<Cat> {
    @Override public EntityType<Cat> type() { return EntityType.CAT; }
    @Override public Trait trait() { return TraitRegistry.CAT_VARIANT; }

    @Override
    public GeneSequence[] extract(Cat entity, Genome genome, long seed) {
        ResourceLocation current = BuiltInRegistries.CAT_VARIANT.getKey(entity.getVariant());
        ResourceLocation other = AppearanceUtils.pickFromSeed(BuiltInRegistries.CAT_VARIANT, seed);

        GeneSequence maternal = AppearanceUtils.createVariantSequence(trait(), current, seed, "cat_var_m");
        GeneSequence paternal = AppearanceUtils.createVariantSequence(trait(), other, seed, "cat_var_p");
        
        genome.getMaternal().getChromosome(trait().getTraitType().getChromosomeType()).setGeneSequence(trait(), maternal);
        genome.getPaternal().getChromosome(trait().getTraitType().getChromosomeType()).setGeneSequence(trait(), paternal);

        return new GeneSequence[]{maternal, paternal};
    }

    @Override public Collection<String> getSuggestions() {
        return BuiltInRegistries.CAT_VARIANT.keySet().stream().map(ResourceLocation::toString).toList();
    }
}