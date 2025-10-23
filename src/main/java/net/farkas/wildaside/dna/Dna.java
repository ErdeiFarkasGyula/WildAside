package net.farkas.wildaside.dna;
import net.farkas.wildaside.dna.genes.AbilityGene;
import net.farkas.wildaside.dna.genes.CoreGene;
import net.farkas.wildaside.dna.genes.ResistanceGene;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

public class Dna {
    LivingEntity origin;
    List<CoreGene> coreGenes;
    List<ResistanceGene> resistanceGenes;
    List<AbilityGene> abilityGenes;
    float baseStability;

    List<Gene> genes = new ArrayList<>();

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
        DnaUtils.clearDnaEffects(entity);
        for (Gene gene : genes) {
            gene.apply(entity);
        }
    }

    public float totalStabilityCost() {
        return (float) genes.stream().mapToDouble(Gene::stabilityCost).sum();
    }

    public void addGene(Gene gene) { genes.add(gene); }

    public List<Gene> getGenes() { return genes; }
}