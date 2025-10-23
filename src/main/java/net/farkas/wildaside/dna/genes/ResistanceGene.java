package net.farkas.wildaside.dna.genes;

import net.farkas.wildaside.dna.DnaUtils;
import net.farkas.wildaside.dna.Gene;
import net.farkas.wildaside.dna.Traits;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public record ResistanceGene(Traits.Resistance type, float reduction, float stabilityCost) implements Gene {
    @Override
    public void apply(Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.getPersistentData().putFloat(DnaUtils.DNA_PREFIX + type.name(), reduction);
        }
    }

    @Override
    public String id() { return type.name(); }

    @Override
    public float stabilityCost() { return stabilityCost; }
}