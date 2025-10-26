package net.farkas.wildaside.dna;

import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;

public interface Traits {
    float baseInstability();

    enum Core implements Traits {
        MAX_HEALTH(1.2f, Attributes.MAX_HEALTH),
        MOVEMENT_SPEED(1.0f, Attributes.MOVEMENT_SPEED),
        ATTACK_DAMAGE(0.9f, Attributes.ATTACK_DAMAGE),
        ATTACK_SPEED(0.7f, Attributes.ATTACK_SPEED),
        ATTACK_KNOCKBACK(0.6f, Attributes.ATTACK_KNOCKBACK),
        ARMOR(0.5f, Attributes.ARMOR),
        ARMOR_TOUGHNESS(0.4f, Attributes.ARMOR_TOUGHNESS),
        KNOCKBACK_RESISTANCE(0.3f, Attributes.KNOCKBACK_RESISTANCE);

        public final Attribute attribute;
        public final float baseInstability;

        Core(float baseInstability, Attribute attribute) {
            this.attribute = attribute;
            this.baseInstability = baseInstability;
        }

        public static Core getRandom(RandomSource randomSource) {
            return Traits.Core.values()[randomSource.nextInt(Core.values().length)];
        }

        @Override
        public float baseInstability() {
            return this.baseInstability;
        }
    }

    enum Resistance implements Traits {
        FIRE(0),
        POISON(0),
        EXPLOSION(0),
        FALL(0),
        TOXIN(0),
        TEMPERATURE(0);

        public final float baseInstability;

        Resistance(float baseInstability) {
            this.baseInstability = baseInstability;
        }

        @Override
        public float baseInstability() {
            return 0;
        }
    }

    enum Ability implements Traits {
        REGENERATION(0),
        LIFESTEAL(0),
        SPORE_ATTACK(0),
        PROJECTILE_ATTACK(0),
        AOE_EFFECT(0);

        public final float baseInstability;

        Ability(float baseInstability) {
            this.baseInstability = baseInstability;
        }

        @Override
        public float baseInstability() {
            return this.baseInstability;
        }
    }
}