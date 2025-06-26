package net.farkas.wildaside.compat;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.*;
import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.block.ModBlocks;
import net.farkas.wildaside.recipe.BioengineeringWorkstationRecipe;
import net.farkas.wildaside.recipe.ModRecipes;
import net.farkas.wildaside.screen.bioengineering_workstation.BioengineeringWorkstationScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;

@JeiPlugin
public class JEIWildAsidePlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(WildAside.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new BioengineeringRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

        List<BioengineeringWorkstationRecipe> bioengineeringRecipes = recipeManager.getAllRecipesFor(ModRecipes.BIOENGINEERING_TYPE.get()).stream().map(RecipeHolder::value).toList();
        registration.addRecipes(BioengineeringRecipeCategory.BIOENGINEERING_TYPE, bioengineeringRecipes);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(BioengineeringWorkstationScreen.class, 88, 35, 29, 14, BioengineeringRecipeCategory.BIOENGINEERING_TYPE);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.VIBRION_BLOCK.get().asItem()), BioengineeringRecipeCategory.BIOENGINEERING_TYPE);
    }
}