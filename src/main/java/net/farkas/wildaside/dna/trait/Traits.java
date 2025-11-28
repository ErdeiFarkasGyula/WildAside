package net.farkas.wildaside.dna.trait;

import net.farkas.wildaside.dna.DnaUtils;
import net.farkas.wildaside.dna.Gene;
import net.farkas.wildaside.dna.trait.TraitTypes;
import net.farkas.wildaside.dna.traits.AttributeTrait;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Traits {
    public static final List<Trait> TRAITS = new ArrayList<>();

    public static final Trait MAX_HEALTH = register(new AttributeTrait("max_health", TraitTypes.CORE, 1.4f, DnaUtils.getAttributeRes("max_health")));
    public static final Trait MOVEMENT_SPEED = register(new AttributeTrait("movement_speed", TraitTypes.CORE, 1.4f, DnaUtils.getAttributeRes("movement_speed")));
    public static final Trait ATTACK_DAMAGE = register(new AttributeTrait("attack_damage", TraitTypes.CORE, 1.5f, DnaUtils.getAttributeRes("attack_damage")));
    public static final Trait ATTACK_SPEED = register(new AttributeTrait("attack_speed", TraitTypes.CORE, 1.1f, DnaUtils.getAttributeRes("attack_speed")));
    public static final Trait ATTACK_KNOCKBACK = register(new AttributeTrait("attack_knockback", TraitTypes.CORE, 1.0f, DnaUtils.getAttributeRes("attack_knockback"), AttributeModifier.Operation.ADDITION));
    public static final Trait ARMOR = register(new AttributeTrait("armor", TraitTypes.CORE, 1.5f, DnaUtils.getAttributeRes("armor"), AttributeModifier.Operation.ADDITION));
    public static final Trait ARMOR_TOUGHNESS = register(new AttributeTrait("armor_toughness", TraitTypes.CORE, 0.8f, DnaUtils.getAttributeRes("armor_toughness"), AttributeModifier.Operation.ADDITION));

    public static final Trait KNOCKBACK_RESISTANCE = register(new AttributeTrait("knockback_resistance", TraitTypes.RESISTANCE, 0.8f, DnaUtils.getAttributeRes("knockback_resistance")));
    public static final Trait FIRE_RESISTANCE = register(new ResistanceTrait("fire_resistance", TraitTypes.RESISTANCE, 2.3f));
    public static final Trait FALL_RESISTANCE = register(new ResistanceTrait("fall_resistance", TraitTypes.RESISTANCE, 2.1f));
    public static final Trait EXPLOSION_RESISTANCE = register(new ResistanceTrait("explosion_resistance", TraitTypes.RESISTANCE, 1.7f));
    public static final Trait FREEZE_RESISTANCE = register(new ResistanceTrait("freeze_resistance", TraitTypes.RESISTANCE, 1.1f));

    public static final Trait FIRE_ABILITY = register(new AbilityTrait("fire_ability", TraitTypes.ABILITY, 3f));
    public static final Trait TELEPORT_ABILITY = register(new AbilityTrait("teleport_ability", TraitTypes.ABILITY, 3f));


    private static <T extends Trait> T register(T trait) {
        TRAITS.add(trait);
        return trait;
    }


    public static List<Trait> getByType(TraitTypes type) {
        List<Trait> out = new ArrayList<>();
        for (Trait t : TRAITS) if (t.traitType() == type) out.add(t);
        return out;
    }


    public static Trait getByName(String name) {
        for (Trait t : TRAITS) if (t.name().equalsIgnoreCase(name)) return t;
        return MAX_HEALTH;
    }
}