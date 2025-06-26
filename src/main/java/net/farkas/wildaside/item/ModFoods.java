package net.farkas.wildaside.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public class ModFoods {
    public static final FoodProperties VIBRION = new FoodProperties.Builder().nutrition(2).alwaysEdible()
            .saturationModifier(0.2f).build();
    public static final FoodProperties ENTORIUM_PILL = new FoodProperties.Builder().nutrition(0).fast().alwaysEdible()
            .saturationModifier(0f).build();
    public static final FoodProperties HICKORY_NUT = new FoodProperties.Builder().nutrition(1)
            .saturationModifier(0.3f).build();
    public static final FoodProperties HICKORY_NUT_TRAIL_MIX = new FoodProperties.Builder().nutrition(2).alwaysEdible()
            .saturationModifier(6f).build();
}