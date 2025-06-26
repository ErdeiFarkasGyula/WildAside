package net.farkas.wildaside.potion;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.effect.ModMobEffects;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static net.farkas.wildaside.effect.ModMobEffects.*;

public class ModPotions {
    public static final DeferredRegister<Potion> POTIONS =
            DeferredRegister.create(ForgeRegistries.POTIONS, WildAside.MOD_ID);

    public static final RegistryObject<Potion> CONTAMINATION_POTION = POTIONS.register("contamination_potion",
            () -> new Potion(new MobEffectInstance(ModMobEffects.CONTAMINATION.getHolder().get(), 400, 0)));
    public static final RegistryObject<Potion> CONTAMINATION_POTION_2 = POTIONS.register("contamination_potion_2",
            () -> new Potion(new MobEffectInstance(ModMobEffects.CONTAMINATION.getHolder().get(), 400, 1)));

    public static final RegistryObject<Potion> IMMUNITY_POTION = POTIONS.register("immunity_potion",
            () -> new Potion(new MobEffectInstance(ModMobEffects.IMMUNITY.getHolder().get(), 400, 0)));
    public static final RegistryObject<Potion> IMMUNITY_POTION_2 = POTIONS.register("immunity_potion_2",
            () -> new Potion(new MobEffectInstance(ModMobEffects.IMMUNITY.getHolder().get(), 400, 1)));

    public static final RegistryObject<Potion> LIFESTEAL_POTION = POTIONS.register("lifesteal_potion",
            () -> new Potion(new MobEffectInstance(ModMobEffects.LIFESTEAL.getHolder().get(), 1800, 0)));
    public static final RegistryObject<Potion> LIFESTEAL_POTION_2 = POTIONS.register("lifesteal_potion_2",
            () -> new Potion(new MobEffectInstance(ModMobEffects.LIFESTEAL.getHolder().get(), 1800, 1)));


    public static void register(IEventBus eventBus) {
        POTIONS.register(eventBus);
    }
}