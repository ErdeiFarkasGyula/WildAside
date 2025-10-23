package net.farkas.wildaside.dna;

import com.mojang.serialization.Codec;

public class Traits {
    public interface Trait {
        String id();
    }

    public enum Core implements Trait {
        HEALTH("health"),
        KNOCKBACK_RESISTANCE("knockback_resistance"),
        SPEED("speed"),
        ATTACK_DAMAGE("attack_damage"),
        KNOCKBACK("knockback"),
        ATTACK_SPEED("attack_speed"),
        ARMOR("armor"),
        ARMOR_TOUGHNESS("armor_toughness");

        public static final Codec<Core> CODEC = Codec.STRING.xmap(Core::valueOf, Core::name);

        Core(String id) {}

        @Override
        public String id() {
            return "";
        }
    }
}