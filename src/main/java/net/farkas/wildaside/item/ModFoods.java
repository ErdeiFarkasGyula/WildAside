package net.farkas.wildaside.item;

import net.minecraft.world.food.FoodProperties;

public class ModFoods {
    public static final FoodProperties VIBRION = new FoodProperties.Builder()
            .nutrition(2)
            .saturationMod(0.2f)
            .alwaysEat()
            .build();

    public static final FoodProperties BACILLUS_BLOB = new FoodProperties.Builder()
            .nutrition(0)
            .saturationMod(0)
            .alwaysEat()
            .fast()
            .build();

    public static final FoodProperties ENTORIUM_PILL = new FoodProperties.Builder()
            .nutrition(0)
            .saturationMod(0f)
            .alwaysEat()
            .fast()
            .build();

    public static final FoodProperties HICKORY_NUT = new FoodProperties.Builder()
            .nutrition(1)
            .saturationMod(0.3f)
            .build();

    public static final FoodProperties HICKORY_NUT_TRAIL_MIX = new FoodProperties.Builder()
            .nutrition(2)
            .saturationMod(6f)
            .alwaysEat()
            .build();
}