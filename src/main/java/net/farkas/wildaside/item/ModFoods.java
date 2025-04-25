package net.farkas.wildaside.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public class ModFoods {
    public static final FoodProperties VIBRION = new FoodProperties.Builder().nutrition(2).alwaysEat()
            .saturationMod(0.2f).build();
    public static final FoodProperties ENTORIUM_PILL = new FoodProperties.Builder().nutrition(0).fast().alwaysEat()
            .saturationMod(0f).build();
    public static final FoodProperties HICKORY_NUT_TRAIL_MIX = new FoodProperties.Builder().nutrition(2)
            .saturationMod(8f).build();
}