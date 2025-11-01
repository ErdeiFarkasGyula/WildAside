package net.farkas.wildaside.dna.traits;

import net.farkas.wildaside.dna.Gene;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Traits {
    public static List<Trait> TRAITS = new ArrayList<>();

    public static final Trait MAX_HEALTH = register("max_health", TraitTypes.CORE, 1.4f);
    public static final Trait MOVEMENT_SPEED = register("movement_speed", TraitTypes.CORE, 1.4f);
    public static final Trait ATTACK_DAMAGE = register("attack_damage", TraitTypes.CORE, 1.5f);
    public static final Trait ATTACK_SPEED = register("attack_speed", TraitTypes.CORE, 1.1f);
    public static final Trait ATTACK_KNOCKBACK = register("attack_knockback", TraitTypes.CORE, 1f);
    public static final Trait ARMOR = register("armor", TraitTypes.CORE, 1.5f);
    public static final Trait ARMOR_TOUGHNESS = register("armor_toughness", TraitTypes.CORE, 0.8f);
    public static final Trait KNOCKBACK_RESISTANCE = register("knockback_resistance", TraitTypes.CORE, 0.8f);

    public static final Trait FIRE_RESISTANCE = register("fire_resistance", TraitTypes.RESISTANCE, 2.3f);
    public static final Trait FALL_RESISTANCE = register("fall_resistance", TraitTypes.RESISTANCE, 2.1f);
    public static final Trait EXPLOSION_RESISTANCE = register("explosion_resistance", TraitTypes.RESISTANCE, 1.7f);
    public static final Trait FREEZE_RESISTANCE = register("freeze_resistance", TraitTypes.RESISTANCE, 1.1f);

    public static final Trait FIRE_ABILITY = register("fire_ability", TraitTypes.ABILITY, 3f);

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

    public static float getTraitValue(Map<Trait, Gene> genes, @Nullable Trait trait) {
        if (trait == null || genes == null) return 0.0f;
        Gene gene = genes.get(trait);
        return gene != null ? gene.value() : 0.0f;
    }
}