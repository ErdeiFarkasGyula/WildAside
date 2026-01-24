package net.farkas.wildaside.compat;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.recipe.IFocusFactory;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IRecipesGui;
import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.recipe.BioengineeringWorkstationRecipe;
import net.farkas.wildaside.screen.bioengineering_workstation.BioengineeringWorkstationScreen;
import net.farkas.wildaside.screen.bioengineering_workstation.BioengineeringWorkstationTab;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@JeiPlugin
public class JEIWildAsidePlugin implements IModPlugin {
    public static int xPos = 129;
    public static int yPos = 62;
    public static int width = 26;
    public static int height = 16;

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(WildAside.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new BioengineeringRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

        List<BioengineeringWorkstationRecipe> bioengineeringRecipes = recipeManager.getAllRecipesFor(BioengineeringWorkstationRecipe.Type.INSTANCE);
        registration.addRecipes(BioengineeringRecipeCategory.BIOENGINEERING_TYPE, bioengineeringRecipes);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGuiContainerHandler(BioengineeringWorkstationScreen.class, new IGuiContainerHandler<BioengineeringWorkstationScreen>() {
            @Override
            public Collection<IGuiClickableArea> getGuiClickableAreas(BioengineeringWorkstationScreen containerScreen, double guiMouseX, double guiMouseY) {
                if (containerScreen.tab == BioengineeringWorkstationTab.ASSEMBLER) {
                    IGuiClickableArea clickableArea = createSounded(xPos, yPos, width, height, BioengineeringRecipeCategory.BIOENGINEERING_TYPE);
                    return List.of(clickableArea);
                }
                return Collections.emptyList();
            }

        });
    }

    private static IGuiClickableArea createSounded(int xPos, int yPos, int width, int height, RecipeType<?>... recipeTypes) {
        Rect2i area = new Rect2i(xPos, yPos, width, height);
        List<RecipeType<?>> recipeTypesList = Arrays.asList(recipeTypes);
        return new IGuiClickableArea() {
            @Override
            public Rect2i getArea() {
                return area;
            }

            @Override
            public void onClick(IFocusFactory focusFactory, IRecipesGui recipesGui) {
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK.get(), 1.0f));
                recipesGui.showTypes(recipeTypesList);
            }
        };
    }
}