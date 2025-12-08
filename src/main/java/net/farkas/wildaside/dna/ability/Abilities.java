package net.farkas.wildaside.dna.ability;

import net.farkas.wildaside.dna.ability.custom.*;
import net.farkas.wildaside.dna.trait.Trait;
import net.farkas.wildaside.dna.trait.TraitRegistry;

import java.util.HashMap;
import java.util.Map;

public class Abilities {
    private static final Map<Trait, IAbility> ABILITIES = new HashMap<>();

    private static final IAbility FIRE_ABILITY = register(TraitRegistry.FIRE_ABILITY, new FireAbility());
    private static final IAbility TELEPORT_ABILITY = register(TraitRegistry.TELEPORT_ABILITY, new TeleportAbility());

    public static IAbility register(Trait trait, IAbility behavior) {
        ABILITIES.put(trait, behavior);
        return behavior;
    }

    public static IAbility get(Trait trait) {
        return ABILITIES.get(trait);
    }
}
