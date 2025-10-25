package net.farkas.wildaside.dna;

import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class Traits {
    public enum Core {
        MAX_HEALTH(Attributes.MAX_HEALTH, 0.8f),
        MOVEMENT_SPEED(Attributes.MOVEMENT_SPEED, 1.2f),
        ATTACK_DAMAGE(Attributes.ATTACK_DAMAGE, 1.3f),
        ATTACK_SPEED(Attributes.ATTACK_SPEED, 1.0f),
        ATTACK_KNOCKBACK(Attributes.ATTACK_KNOCKBACK, 0.9f),
        ARMOR(Attributes.ARMOR, 0.7f),
        ARMOR_TOUGHNESS(Attributes.ARMOR_TOUGHNESS, 0.8f),
        KNOCKBACK_RESISTANCE(Attributes.KNOCKBACK_RESISTANCE, 0.6f);

        public final Attribute attribute;
        public final float baseInstability;

        Core(Attribute attribute, float baseInstability) {
            this.attribute = attribute;
            this.baseInstability = baseInstability;
        }

        public static Core getRandom(RandomSource randomSource) {
            return Traits.Core.values()[randomSource.nextInt(Core.values().length)];
        }
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
        LIFESTEAL,
        SPORE_ATTACK,
        PROJECTILE_ATTACK,
        AOE_EFFECT;
    }
}