package net.farkas.wildaside.dna.sequence.components;

import net.farkas.wildaside.dna.trait.Trait;
import net.farkas.wildaside.dna.trait.TraitRegistry;
import net.minecraft.nbt.CompoundTag;

public class TraitDefiner implements GeneComponent {
    private final Trait trait;

    public TraitDefiner(Trait trait) {
        this.trait = trait;
    }

    public Trait getTrait() {
        return trait;
    }

    @Override
    public String getId() {
        return trait.getName() + "_definer";
    }

    @Override
    public ComponentType getType() {
        return ComponentType.TRAIT_DEFINER;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Trait", trait.getName());
        return tag;
    }

    public static TraitDefiner deserializeNBT(CompoundTag tag) {
        String traitName = tag.getString("Trait");
        Trait trait = TraitRegistry.getByName(traitName);
        if (trait == null) {
            throw new IllegalArgumentException("Unknown trait: " + traitName);
        }
        return new TraitDefiner(trait);
    }
}
