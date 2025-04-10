package net.farkas.wildaside.potion;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.effect.ModMobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModPotions {
    public static final DeferredRegister<Potion> POTIONS =
            DeferredRegister.create(ForgeRegistries.POTIONS, WildAside.MOD_ID);

    public static final RegistryObject<Potion> CONTAMINATION_POTION = POTIONS.register("contamination_potion",
            () -> new Potion(new MobEffectInstance(ModMobEffects.CONTAMINATION.get(), 800, 0)));
    public static final RegistryObject<Potion> IMMUNITY_POTION = POTIONS.register("immunity_potion",
            () -> new Potion(new MobEffectInstance(ModMobEffects.IMMUNITY.get(), 800, 0)));
    public static final RegistryObject<Potion> LIFE_STEAL_POTION = POTIONS.register("life_steal_potion",
            () -> new Potion(new MobEffectInstance(ModMobEffects.LIFE_STEAL.get(), 800, 0)));

    public static void register(IEventBus eventBus) {
        POTIONS.register(eventBus);
    }
}