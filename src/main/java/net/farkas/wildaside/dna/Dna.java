package net.farkas.wildaside.dna;
import net.farkas.wildaside.dna.genes.AbilityGene;
import net.farkas.wildaside.dna.genes.CoreGene;
import net.farkas.wildaside.dna.genes.ResistanceGene;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Dna {
    LivingEntity origin;
    List<CoreGene> coreGenes;
    List<ResistanceGene> resistanceGenes;
    List<AbilityGene> abilityGenes;
    float baseStability;

    List<Gene> genes = new ArrayList<>();

    public static final String DNA_STABILITY = "dna_stability";

    public Dna(LivingEntity origin, List<CoreGene> coreGenes, List<ResistanceGene> resistanceGenes, List<AbilityGene> abilityGenes, float baseStability) {
        this.origin = origin;
        this.coreGenes = coreGenes;
        this.resistanceGenes = resistanceGenes;
        this.abilityGenes = abilityGenes;
        this.baseStability = baseStability;

        this.genes.addAll(coreGenes);
        this.genes.addAll(resistanceGenes);
        this.genes.addAll(abilityGenes);
    }

    public void applyTo(Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            CompoundTag compoundTag = livingEntity.getPersistentData();
            compoundTag.putString("dna_source_entity", origin.getType().getDescriptionId());

            float stability = compoundTag.getFloat(DNA_STABILITY);
            float stabilityCost = totalStabilityCost(ge);

            if (stability == 0.0f) {
                stability = 100;
            }

            float newStability = stability - stabilityCost;
            compoundTag.putFloat(DNA_STABILITY, newStability);
            System.out.println("Stability: " + newStability);

            System.out.println("DESC: " + origin.getType().getDescriptionId());
            DnaUtils.clearDnaEffects(livingEntity);
            for (Gene gene : genes) {
                gene.apply(livingEntity);
            }
        }
    }

    public float totalStabilityCost(LivingEntity livingEntity) {
        return (float) genes.stream()
                .filter(gene -> !isGeneAlreadyApplied(livingEntity, gene))
                .mapToDouble(Gene::stabilityCost)
                .sum();
    }

    private boolean isGeneAlreadyApplied(LivingEntity livingEntity, CoreGene gene) {
        AttributeInstance instance = livingEntity.getAttribute(gene.trait().attribute);
        if (instance == null) return false;

        UUID id = DnaUtils.getUuid(gene.fullName());
        return instance.getModifier(id) != null;
    }

    public void addGene(Gene gene) { genes.add(gene); }

    public List<Gene> getGenes() { return genes; }
}