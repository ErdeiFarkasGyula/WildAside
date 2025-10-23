package net.farkas.wildaside.dna;

import net.minecraft.world.entity.Entity;

public interface Gene {
    String id();
    float stabilityCost();
    void apply(Entity entity);
}