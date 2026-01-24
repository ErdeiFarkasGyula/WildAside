package net.farkas.wildaside.dna.sequence;

import net.farkas.wildaside.dna.dominance.Dominance;
import net.farkas.wildaside.dna.expression.*;
import net.farkas.wildaside.dna.sequence.components.*;
import net.farkas.wildaside.dna.trait.TraitRegistry;

public class GeneSequenceExamples {
    public static GeneSequence createFireResistanceExample() {
        return GeneSequence.builder()
                .trait(TraitRegistry.FIRE_RESISTANCE)
                .dominance(Dominance.CO_DOMINANT)
                .mutationRate(0.05f)
                .stability(1.0f)
                .source(GeneSource.NATURAL)

                .activator(new Activator("heat_sense", ActivationCondition.ALWAYS, 0f))
                .codingRegion(new CodingRegion("base", 0.3f, CombineMethod.SET))
                .codingRegion(new CodingRegion("bonus", 0.1f, CombineMethod.ADD))

                .enhancer(new Enhancer("nether_adaptation", ActivationCondition.IN_NETHER, 0f, 1.5f, 0.1f))
                .enhancer(new Enhancer("fire_exposure", ActivationCondition.ON_FIRE, 0f, 1.2f, 0f))

                .silencer(new Silencer("water_weakness", ActivationCondition.IN_WATER, 0f, 0.5f, 0.1f))

                .regulator(new Regulator("cap", RegulationType.CAP_MAX, 1.0f))
                .regulator(new Regulator("floor", RegulationType.CAP_MIN, 0.0f))

                .build();
    }

    public static GeneSequence createAttackDamageExample() {
        return GeneSequence.builder()
                .trait(TraitRegistry.ATTACK_DAMAGE)
                .dominance(Dominance.DOMINANT)
                .mutationRate(0.02f)
                .stability(0.9f)
                .source(GeneSource.NATURAL)

                .activator(new Activator("damage_activator", ActivationCondition.ALWAYS, 0f))
                .codingRegion(new CodingRegion("base", 8.5f, CombineMethod.SET))
                .codingRegion(new CodingRegion("scaling", 1.2f, CombineMethod.MULTIPLY))

                .enhancer(new Enhancer("combat_boost", ActivationCondition.IN_COMBAT, 0f, 1.3f, 0f))
                .enhancer(new Enhancer("vitality_boost", ActivationCondition.HEALTH_ABOVE, 0.8f, 1.2f, 0f))

                .regulator(new Regulator("damage_cap", RegulationType.CAP_MAX, 50.0f))

                .build();
    }

    public static GeneSequence createMaxHealthExample() {
        return GeneSequence.builder()
                .trait(TraitRegistry.MAX_HEALTH)
                .dominance(Dominance.CO_DOMINANT)
                .mutationRate(0.01f)
                .stability(1.0f)
                .source(GeneSource.NATURAL)

                .activator(new Activator("health_activator", ActivationCondition.ALWAYS, 0f))
                .codingRegion(new CodingRegion("base", 20.0f, CombineMethod.SET))
                .codingRegion(new CodingRegion("bonus", 5.0f, CombineMethod.ADD))

                .enhancer(new Enhancer("health_boost", ActivationCondition.ALWAYS, 0f, 1.3f, 0f))
                .enhancer(new Enhancer("vitality", ActivationCondition.HEALTH_ABOVE, 0.8f, 1.2f, 0f))

                .regulator(new Regulator("min_health", RegulationType.CAP_MIN, 1.0f))

                .build();
    }

    public static GeneSequence createMovementSpeedExample() {
        return GeneSequence.builder()
                .trait(TraitRegistry.MOVEMENT_SPEED)
                .dominance(Dominance.INCOMPLETE)
                .mutationRate(0.03f)
                .stability(0.95f)
                .source(GeneSource.NATURAL)

                .activator(new Activator("speed_activator", ActivationCondition.ALWAYS, 0f))
                .codingRegion(new CodingRegion("base", 1.0f, CombineMethod.SET))

                .enhancer(new Enhancer("sprint_boost", ActivationCondition.SPRINTING, 0f, 1.4f, 0f))
                .enhancer(new Enhancer("daylight_boost", ActivationCondition.IS_DAY, 0f, 1.0f, 0.1f))

                .silencer(new Silencer("underground_slow", ActivationCondition.UNDERGROUND, 0f, 0.8f, 0f))

                .regulator(new Regulator("speed_floor", RegulationType.CAP_MIN, 0.2f))
                .regulator(new Regulator("speed_cap", RegulationType.CAP_MAX, 2.0f))

                .build();
    }

    public static GeneSequence createFreezeResistanceExample() {
        return GeneSequence.builder()
                .trait(TraitRegistry.FREEZE_RESISTANCE)
                .dominance(Dominance.RECESSIVE)
                .mutationRate(0.04f)
                .stability(0.85f)
                .source(GeneSource.NATURAL)

                .activator(new Activator("cold_sense", ActivationCondition.ALWAYS, 0f))
                .codingRegion(new CodingRegion("base", 0.2f, CombineMethod.SET))
                .codingRegion(new CodingRegion("bonus", 0.1f, CombineMethod.ADD))

                .enhancer(new Enhancer("underground_warmth", ActivationCondition.UNDERGROUND, 0f, 1.3f, 0.05f))

                .silencer(new Silencer("water_chill", ActivationCondition.IN_WATER, 0f, 0.6f, 0.2f))
                .silencer(new Silencer("exposed_cold", ActivationCondition.UNDER_SKY, 0f, 0.7f, 0f))

                .regulator(new Regulator("max_resistance", RegulationType.CAP_MAX, 1.0f))
                .regulator(new Regulator("min_resistance", RegulationType.CAP_MIN, 0.0f))

                .build();
    }

    public static GeneSequence createComplexConditionalExample() {
        return GeneSequence.builder()
                .trait(TraitRegistry.MOVEMENT_SPEED)
                .dominance(Dominance.CO_DOMINANT)
                .source(GeneSource.ENGINEERED)

                .activator(new Activator("base_act", ActivationCondition.ALWAYS, 0f))
                .codingRegion(new CodingRegion("base_speed", 1.0f, CombineMethod.SET))

                .activator(new Activator("emergency_act", ActivationCondition.HEALTH_BELOW, 0.3f))
                .codingRegion(new CodingRegion("adrenaline", 0.5f, CombineMethod.ADD))
                .enhancer(new Enhancer("panic", ActivationCondition.IN_COMBAT, 0f, 1.5f, 0f))

                .activator(new Activator("global_reg_act", ActivationCondition.ALWAYS, 0f))
                .regulator(new Regulator("speed_limit", RegulationType.CAP_MAX, 3.0f))

                .build();
    }
}
