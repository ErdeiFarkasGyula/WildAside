package net.farkas.wildaside.dna;

import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public record Dna(LivingEntity species, List<Gene> genes, float baseStability) {
}