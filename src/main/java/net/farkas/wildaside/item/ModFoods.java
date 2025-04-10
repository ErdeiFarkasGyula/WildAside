package net.farkas.wildaside.item;

import net.farkas.wildaside.effect.ModMobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public class ModFoods {
    public static final FoodProperties VIBRION = new FoodProperties.Builder().nutrition(2).alwaysEat()
            .saturationMod(0.2f).effect(() -> new MobEffectInstance(MobEffects.POISON, 400), 0.5f).build();

    public static final FoodProperties ENTORIUM_PILL = new FoodProperties.Builder().nutrition(0).fast().alwaysEat()
            .saturationMod(0f).effect(() -> new MobEffectInstance(ModMobEffects.IMMUNITY.get(), 200), 1f).build();
}