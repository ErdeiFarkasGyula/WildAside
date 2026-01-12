package net.farkas.wildaside.dna.chromosome;

import net.farkas.wildaside.dna.expression.ExpressionContext;
import net.farkas.wildaside.dna.expression.GeneExpressionPair;
import net.farkas.wildaside.dna.sequence.GeneSequence;
import net.farkas.wildaside.dna.sequence.GeneSequenceExamples;
import net.farkas.wildaside.dna.trait.Trait;
import net.farkas.wildaside.dna.trait.TraitRegistry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

/**
 * Complete example demonstrating the new chromosome-based DNA system.
 * 
 * This example shows:
 * 1. Building gene sequences with regulatory components (coding regions, activators, enhancers, silencers, regulators)
 * 2. Creating a genome with maternal and paternal chromosome sets
 * 3. Expressing genes based on environmental context
 * 4. Resolving dominance between maternal and paternal alleles
 */
public class GenomeExample {

    /**
     * Creates an example genome for a wolf-like entity with fire resistance and combat abilities.
     * 
     * The genome includes:
     * - Fire resistance (maternal: high, paternal: medium) - will be averaged due to co-dominance
     * - Attack damage (maternal: high, paternal: low) - maternal is dominant
     * - Max health (maternal and paternal: same) - homozygous trait
     * 
     * @return A configured Genome ready for use
     */
    public static Genome createExampleWolfGenome() {
        // Create maternal gene sequences
        GeneSequence maternalFireRes = GeneSequenceExamples.createFireResistanceExample();
        GeneSequence maternalAttackDmg = GeneSequenceExamples.createAttackDamageExample();
        GeneSequence maternalHealth = GeneSequenceExamples.createMaxHealthExample();

        // Create paternal gene sequences (could be different values/regulation)
        GeneSequence paternalFireRes = GeneSequenceExamples.createFireResistanceExample();
        GeneSequence paternalAttackDmg = GeneSequenceExamples.createAttackDamageExample();
        GeneSequence paternalHealth = GeneSequenceExamples.createMaxHealthExample();

        // Build the genome
        return GenomeBuilder.forEntityType(EntityType.WOLF)
                .addMaternalSequence(TraitRegistry.FIRE_RESISTANCE, maternalFireRes)
                .addMaternalSequence(TraitRegistry.ATTACK_DAMAGE, maternalAttackDmg)
                .addMaternalSequence(TraitRegistry.MAX_HEALTH, maternalHealth)
                .addPaternalSequence(TraitRegistry.FIRE_RESISTANCE, paternalFireRes)
                .addPaternalSequence(TraitRegistry.ATTACK_DAMAGE, paternalAttackDmg)
                .addPaternalSequence(TraitRegistry.MAX_HEALTH, paternalHealth)
                .build();
    }

    /**
     * Demonstrates how gene expression works with environmental context.
     * 
     * This shows how the same genome can produce different trait values depending on:
     * - Entity state (health, on fire, in water, etc.)
     * - Environmental conditions (Nether, End, day/night, etc.)
     * - Behavioral state (sprinting, in combat, etc.)
     */
    public static void demonstrateGeneExpression(LivingEntity entity, Genome genome) {
        // Create expression context from the entity
        ExpressionContext context = new ExpressionContext(entity);

        // Example 1: Fire resistance changes based on environment
        Trait fireResTrait = TraitRegistry.FIRE_RESISTANCE;
        GeneExpressionPair fireResExpression = genome.getGeneExpression(fireResTrait);
        
        float fireResValue = fireResExpression.express(context);
        // If entity is in Nether: higher fire resistance due to enhancers
        // If entity is in water: lower fire resistance due to silencers
        // The value is the result of:
        //   1. Maternal and paternal genes each calculate their base values
        //   2. Each applies its activators, enhancers, silencers, and regulators
        //   3. The two results are combined based on dominance rules

        // Example 2: Attack damage changes based on combat state
        Trait attackDmgTrait = TraitRegistry.ATTACK_DAMAGE;
        float attackDmgValue = genome.getExpressedValue(attackDmgTrait, context);
        // If entity is in combat with high health: boosted attack damage
        // Attack is capped at maximum value by regulators

        // Example 3: Getting maternal and paternal base values separately
        float maternalBase = fireResExpression.getMaternalBaseValue();
        float paternalBase = fireResExpression.getPaternalBaseValue();
        // These are the raw coding region values before any regulation
    }

    /**
     * Demonstrates different dominance patterns and how they affect expression.
     */
    public static void demonstrateDominancePatterns(LivingEntity entity) {
        ExpressionContext context = new ExpressionContext(entity);
        
        // Example of different dominance outcomes:
        // 
        // DOMINANT x DOMINANT → Higher value wins (Math.max)
        // DOMINANT x RECESSIVE → Dominant value expressed
        // CO_DOMINANT x CO_DOMINANT → Averaged (50/50 blend)
        // CO_DOMINANT x DOMINANT → Weighted average (30/70 towards dominant)
        // INCOMPLETE x INCOMPLETE → Simple average
        // 
        // This allows for rich genetic diversity and realistic inheritance patterns
    }

    /**
     * Demonstrates how gene sequences can be modified for breeding or mutation.
     */
    public static void demonstrateGeneticVariation() {
        // During breeding, you would:
        // 1. Get parent genomes
        // 2. Randomly select maternal or paternal chromosome from each parent
        // 3. Create offspring genome from selected chromosomes
        // 4. Apply mutation based on gene sequence mutation rates
        // 5. Potentially modify enhancers/silencers based on environmental factors
        
        // This creates endless genetic variation while maintaining biological accuracy
    }
}
