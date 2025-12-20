package net.farkas.wildaside.dna.allele.dominance;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public enum Dominance {
    DOMINANT,
    RECESSIVE,
    CO_DOMINANT,
    INCOMPLETE;

    public MutableComponent getComponent() {
        return Component.translatable("dominance.wildaside." + this.name().toLowerCase());
    }
}
