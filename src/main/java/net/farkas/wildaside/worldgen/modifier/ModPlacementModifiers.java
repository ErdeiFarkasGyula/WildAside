package net.farkas.wildaside.worldgen.modifier;

import net.farkas.wildaside.WildAside;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModPlacementModifiers {
    public static final DeferredRegister<PlacementModifierType<?>> PLACEMENT_MODIFIERS = DeferredRegister.create(BuiltInRegistries.PLACEMENT_MODIFIER_TYPE.key(), WildAside.MOD_ID);

    public static final Supplier<PlacementModifierType<LargePatchNoiseModifier>> LARGE_PATCH_NOISE =
            PLACEMENT_MODIFIERS.register("large_patch_noise", () -> () -> LargePatchNoiseModifier.CODEC);

    public static void register(IEventBus eventBus) {
        PLACEMENT_MODIFIERS.register(eventBus);
    }
}