package net.farkas.wildaside.dna.genes;

import net.farkas.wildaside.dna.Gene;
import net.farkas.wildaside.dna.Traits;
import net.minecraft.world.entity.Entity;

public record ResistanceGene(Traits.Resistance type, float reduction, float stabilityCost) implements Gene {
    @Override
    public void apply(Entity entity) {
        entity.getPersistentData().putFloat("resistance_" + type.name(), reduction);
    }

    @Override
    public String id() { return type.name(); }

    @Override
    public float stabilityCost() { return stabilityCost; }
}