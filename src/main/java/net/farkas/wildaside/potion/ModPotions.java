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
            () -> new Potion(new MobEffectInstance(ModMobEffects.CONTAMINATION.get(), 1800, 0)));
    public static final RegistryObject<Potion> CONTAMINATION_POTION_2 = POTIONS.register("contamination_potion_2",
            () -> new Potion(new MobEffectInstance(ModMobEffects.CONTAMINATION.get(), 1800, 1)));
    public static final RegistryObject<Potion> CONTAMINATION_POTION_3 = POTIONS.register("contamination_potion_3",
            () -> new Potion(new MobEffectInstance(ModMobEffects.CONTAMINATION.get(), 1800, 2)));

    public static final RegistryObject<Potion> IMMUNITY_POTION = POTIONS.register("immunity_potion",
            () -> new Potion(new MobEffectInstance(ModMobEffects.IMMUNITY.get(), 1800, 0)));
    public static final RegistryObject<Potion> IMMUNITY_POTION_2 = POTIONS.register("immunity_potion_2",
            () -> new Potion(new MobEffectInstance(ModMobEffects.IMMUNITY.get(), 1800, 1)));
    public static final RegistryObject<Potion> IMMUNITY_POTION_3 = POTIONS.register("immunity_potion_3",
            () -> new Potion(new MobEffectInstance(ModMobEffects.IMMUNITY.get(), 1800, 2)));


    public static final RegistryObject<Potion> OMNIVAMP_POTION = POTIONS.register("omnivamp_potion",
            () -> new Potion(new MobEffectInstance(ModMobEffects.OMNIVAMP.get(), 1800, 0)));
    public static final RegistryObject<Potion> OMNIVAMP_POTION_2 = POTIONS.register("omnivamp_potion_2",
            () -> new Potion(new MobEffectInstance(ModMobEffects.OMNIVAMP.get(), 1800, 1)));
    public static final RegistryObject<Potion> OMNIVAMP_POTION_3 = POTIONS.register("omnivamp_potion_3",
            () -> new Potion(new MobEffectInstance(ModMobEffects.OMNIVAMP.get(), 1800, 2)));



    public static void register(IEventBus eventBus) {
        POTIONS.register(eventBus);
    }
}