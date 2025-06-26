package net.farkas.wildaside.recipe;

import net.farkas.wildaside.WildAside;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, WildAside.MOD_ID);
    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, WildAside.MOD_ID);

    public static final RegistryObject<RecipeSerializer<BioengineeringWorkstationRecipe>> BIOENGINEERING_SERIALIZER =
            SERIALIZERS.register("bioengineering", BioengineeringWorkstationRecipe.Serializer::new);
    public static final RegistryObject<RecipeType<BioengineeringWorkstationRecipe>> BIOENGINEERING_TYPE =
            TYPES.register("bioengineering", () -> new RecipeType<BioengineeringWorkstationRecipe>() {
                @Override
                public String toString() {
                    return "bioengineering";
                }
            });

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
        TYPES.register(eventBus);
    }
}