package net.farkas.wildaside.dna.traits;

import java.util.ArrayList;
import java.util.List;

public class Traits {
    public static List<Trait> TRAITS = new ArrayList<>();

    public static final Trait MAX_HEALTH = register("max_health", TraitTypes.CORE, 0.1f);
    public static final Trait MOVEMENT_SPEED = register("movement_speed", TraitTypes.CORE, 1.0f);
    public static final Trait ATTACK_DAMAGE = register("attack_damage", TraitTypes.CORE, 0.9f);
    public static final Trait ATTACK_SPEED = register("attack_speed", TraitTypes.CORE, 0.7f);
    public static final Trait ATTACK_KNOCKBACK = register("attack_knockback", TraitTypes.CORE, 0.6f);
    public static final Trait ARMOR = register("armor", TraitTypes.CORE, 0.5f);
    public static final Trait ARMOR_TOUGHNESS = register("armor_toughness", TraitTypes.CORE,0.4f);
    public static final Trait KNOCKBACK_RESISTANCE = register("knockback_resistance", TraitTypes.CORE, 0.3f);

    private static Trait register(String name, TraitTypes traitType, float baseInstability) {
        Trait trait = new Trait(name, traitType, baseInstability);
        TRAITS.add(trait);
        return trait;
    }

    public static List<Trait> getByType(TraitTypes type) {
        return TRAITS.stream()
                .filter(trait -> trait.traitType() == type)
                .toList();
    }

    public static Trait getByName(String name) {
        for (Trait t : TRAITS) {
            if (t.name().equalsIgnoreCase(name)) {
                return t;
            }
        }
        return null;
    }
}
//
//    class Resistance extends Traits {
//        FIRE(0),
//        POISON(0),
//        EXPLOSION(0),
//        FALL(0),
//        TOXIN(0),
//        TEMPERATURE(0);
//    }
//
//    class Ability extends Traits {
//        REGENERATION(0),
//        LIFESTEAL(0),
//        SPORE_ATTACK(0),
//        PROJECTILE_ATTACK(0),
//        AOE_EFFECT(0);
//    }
//}