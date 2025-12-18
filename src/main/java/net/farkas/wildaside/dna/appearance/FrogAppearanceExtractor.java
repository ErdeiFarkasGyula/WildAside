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
import net.minecraft.world.entity.animal.frog.Frog;

import java.util.Map;

public class FrogAppearanceExtractor implements AppearanceGeneExtractor<Frog> {
    @Override
    public EntityType<Frog> type() {
        return EntityType.FROG;
    }

    @Override
    public void extract(Frog frog, Map<Trait, Gene> genes, long seed) {
        ResourceLocation variant = BuiltInRegistries.FROG_VARIANT.getKey(frog.getVariant());

        Allele a = new Allele(
                new ResourceLocationAlleleValue(variant),
                0.01f,
                0.8f,
                Dominance.DOMINANT
        );

        Allele b = new Allele(
                new ResourceLocationAlleleValue(variant),
                0.01f,
                0.8f,
                Dominance.INCOMPLETE
        );

        genes.put(
                TraitRegistry.,
                new Gene(TraitRegistry.FROG_VARIANT, a, b)
        );
    }
}
