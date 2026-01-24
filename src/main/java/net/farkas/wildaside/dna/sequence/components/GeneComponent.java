package net.farkas.wildaside.dna.sequence.components;

import net.minecraft.nbt.CompoundTag;

public interface GeneComponent {
    String getId();
    ComponentType getType();
    CompoundTag serializeNBT();

    static GeneComponent deserializeNBT(ComponentType type, CompoundTag tag) {
        return switch (type) {
            case TRAIT_DEFINER ->  TraitDefiner.deserializeNBT(tag);
            case CODING_REGION -> CodingRegion.deserializeNBT(tag);
            case ACTIVATOR -> Activator.deserializeNBT(tag);
            case ENHANCER -> Enhancer.deserializeNBT(tag);
            case SILENCER -> Silencer.deserializeNBT(tag);
            case REGULATOR -> Regulator.deserializeNBT(tag);
        };
    }

    enum ComponentType {
        TRAIT_DEFINER,
        CODING_REGION,
        ACTIVATOR,
        ENHANCER,
        SILENCER,
        REGULATOR
    }
}
