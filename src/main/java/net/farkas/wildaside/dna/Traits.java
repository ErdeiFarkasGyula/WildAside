package net.farkas.wildaside.dna;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class Traits {
    public enum Core {
        MAX_HEALTH(Attributes.MAX_HEALTH),
        MOVEMENT_SPEED(Attributes.MOVEMENT_SPEED),
        ATTACK_DAMAGE(Attributes.ATTACK_DAMAGE),
        ATTACK_SPEED(Attributes.ATTACK_SPEED),
        ARMOR(Attributes.ARMOR),
        ARMOR_TOUGHNESS(Attributes.ARMOR_TOUGHNESS),
        KNOCKBACK_RESISTANCE(Attributes.KNOCKBACK_RESISTANCE);

        public final Attribute attribute;
        Core(Attribute attribute) { this.attribute = attribute; }
    }

    public enum Resistance {
        FIRE,
        POISON,
        EXPLOSION,
        FALL,
        TOXIN,
        TEMPERATURE;
    }

    public enum Ability {
        REGENERATION,
        OMNIVAMP,
        SPORE_ATTACK,
        PROJECTILE_ATTACK,
        AOE_EFFECT;
    }
}