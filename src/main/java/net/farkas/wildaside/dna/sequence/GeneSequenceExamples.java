package net.farkas.wildaside.dna.sequence;

import net.farkas.wildaside.dna.allele.dominance.Dominance;
import net.farkas.wildaside.dna.expression.*;
import net.farkas.wildaside.dna.trait.Trait;
import net.farkas.wildaside.dna.trait.TraitRegistry;

/**
 * Example factory for creating common gene sequences with regulatory components.
 * This demonstrates the new chromosome-based DNA system with biologically-inspired
 * gene regulation including activators, enhancers, silencers, and regulators.
 */
public class GeneSequenceExamples {

    /**
     * Creates a fire resistance gene sequence as shown in the design specification.
     * 
     * Base value: 0.3 + 0.1 = 0.4
     * In Nether: 0.4 × 1.5 + 0.1 = 0.7
     * In Nether while on fire: 0.7 × 1.2 = 0.84
     * In water: 0.4 × 0.5 - 0.1 = 0.1
     * Always clamped between 0.0 and 1.0
     */
    public static GeneSequence createFireResistanceExample() {
        return GeneSequence.builder()
                .trait(TraitRegistry.FIRE_RESISTANCE)
                .dominance(Dominance.CO_DOMINANT)
                .mutationRate(0.05f)
                .stability(1.0f)
                .source(GeneSource.NATURAL)

                .codingRegion(new CodingRegion("base", 0.3f, CombineMethod.SET))
                .codingRegion(new CodingRegion("bonus", 0.1f, CombineMethod.ADD))

                .activator(new Activator("heat_sense", ActivationCondition.ALWAYS, 0f))

                .enhancer(new Enhancer("nether_adaptation", ActivationCondition.IN_NETHER, 1.5f, 0.1f))
                .enhancer(new Enhancer("fire_exposure", ActivationCondition.ON_FIRE, 1.2f, 0f))

                .silencer(new Silencer("water_weakness", ActivationCondition.IN_WATER, 0.5f, 0.1f))

                .regulator(new Regulator("cap", RegulationType.CAP_MAX, 1.0f))
                .regulator(new Regulator("floor", RegulationType.CAP_MIN, 0.0f))

                .build();
    }

    /**
     * Creates an attack damage gene sequence with combat-responsive regulation.
     * 
     * Base value: 8.5 * 1.2 = 10.2
     * When in combat and health above 50%: boost by 1.5x
     * Maximum damage capped at 50.0
     */
    public static GeneSequence createAttackDamageExample() {
        return GeneSequence.builder()
                .trait(TraitRegistry.ATTACK_DAMAGE)
                .dominance(Dominance.DOMINANT)
                .mutationRate(0.02f)
                .stability(0.9f)
                .source(GeneSource.NATURAL)

                .codingRegion(new CodingRegion("base", 8.5f, CombineMethod.SET))
                .codingRegion(new CodingRegion("scaling", 1.2f, CombineMethod.MULTIPLY))

                .activator(new Activator("damage_activator", ActivationCondition.ALWAYS, 0f))

                .enhancer(new Enhancer("combat_boost", ActivationCondition.IN_COMBAT, 1.3f, 0f))
                .enhancer(new Enhancer("vitality_boost", ActivationCondition.HEALTH_ABOVE, 1.2f, 0f))

                .regulator(new Regulator("damage_cap", RegulationType.CAP_MAX, 50.0f))

                .build();
    }

    /**
     * Creates a max health gene sequence with adaptive regulation based on current health.
     * 
     * Base value: 20.0 + 5.0 = 25.0
     * When health > 50%: boosted by 1.2x
     * Base health enhanced by 1.3x multiplier
     */
    public static GeneSequence createMaxHealthExample() {
        return GeneSequence.builder()
                .trait(TraitRegistry.MAX_HEALTH)
                .dominance(Dominance.CO_DOMINANT)
                .mutationRate(0.01f)
                .stability(1.0f)
                .source(GeneSource.NATURAL)

                .codingRegion(new CodingRegion("base", 20.0f, CombineMethod.SET))
                .codingRegion(new CodingRegion("bonus", 5.0f, CombineMethod.ADD))

                .activator(new Activator("health_activator", ActivationCondition.ALWAYS, 0f))

                .enhancer(new Enhancer("health_boost", ActivationCondition.ALWAYS, 1.3f, 0f))
                .enhancer(new Enhancer("vitality", ActivationCondition.HEALTH_ABOVE, 1.2f, 0f))

                .regulator(new Regulator("min_health", RegulationType.CAP_MIN, 1.0f))

                .build();
    }

    /**
     * Creates a movement speed gene sequence with environmental modifiers.
     * 
     * Base value: 1.0
     * Sprint boost when sprinting: 1.4x
     * Day time boost: +0.1
     * Underground penalty: 0.8x
     * Floor at 0.2 to prevent complete immobility
     */
    public static GeneSequence createMovementSpeedExample() {
        return GeneSequence.builder()
                .trait(TraitRegistry.MOVEMENT_SPEED)
                .dominance(Dominance.INCOMPLETE)
                .mutationRate(0.03f)
                .stability(0.95f)
                .source(GeneSource.NATURAL)

                .codingRegion(new CodingRegion("base", 1.0f, CombineMethod.SET))

                .activator(new Activator("speed_activator", ActivationCondition.ALWAYS, 0f))

                .enhancer(new Enhancer("sprint_boost", ActivationCondition.SPRINTING, 1.4f, 0f))
                .enhancer(new Enhancer("daylight_boost", ActivationCondition.IS_DAY, 1.0f, 0.1f))

                .silencer(new Silencer("underground_slow", ActivationCondition.UNDERGROUND, 0.8f, 0f))

                .regulator(new Regulator("speed_floor", RegulationType.CAP_MIN, 0.2f))
                .regulator(new Regulator("speed_cap", RegulationType.CAP_MAX, 2.0f))

                .build();
    }

    /**
     * Creates a freeze resistance gene sequence with environmental adaptation.
     * 
     * Base value: 0.2 + 0.1 = 0.3
     * In water: penalty of 0.6x - 0.2
     * Under sky: penalty of 0.7x
     * Clamped between 0.0 and 1.0
     */
    public static GeneSequence createFreezeResistanceExample() {
        return GeneSequence.builder()
                .trait(TraitRegistry.FREEZE_RESISTANCE)
                .dominance(Dominance.RECESSIVE)
                .mutationRate(0.04f)
                .stability(0.85f)
                .source(GeneSource.NATURAL)

                .codingRegion(new CodingRegion("base", 0.2f, CombineMethod.SET))
                .codingRegion(new CodingRegion("bonus", 0.1f, CombineMethod.ADD))

                .activator(new Activator("cold_sense", ActivationCondition.ALWAYS, 0f))

                .enhancer(new Enhancer("underground_warmth", ActivationCondition.UNDERGROUND, 1.3f, 0.05f))

                .silencer(new Silencer("water_chill", ActivationCondition.IN_WATER, 0.6f, 0.2f))
                .silencer(new Silencer("exposed_cold", ActivationCondition.UNDER_SKY, 0.7f, 0f))

                .regulator(new Regulator("max_resistance", RegulationType.CAP_MAX, 1.0f))
                .regulator(new Regulator("min_resistance", RegulationType.CAP_MIN, 0.0f))

                .build();
    }
}
