package net.farkas.wildaside.compat;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.block.ModBlocks;
import net.farkas.wildaside.recipe.BioengineeringWorkstationRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class BioengineeringRecipeCategory implements IRecipeCategory<BioengineeringWorkstationRecipe> {
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(WildAside.MOD_ID, "bioengineering");
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(WildAside.MOD_ID, "textures/gui/bioengineering_workstation.png");

    public static final RecipeType<BioengineeringWorkstationRecipe> BIOENGINEERING_TYPE =
            new RecipeType<>(UID, BioengineeringWorkstationRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public BioengineeringRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 17, 10, 140, 65);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.BIOENGINEERING_WORKSTATION.get()));
    }

    @Override
    public RecipeType<BioengineeringWorkstationRecipe> getRecipeType() {
        return BIOENGINEERING_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.wildaside.bioengineering_workstation");
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public @Nullable IDrawable getBackground() {
        return background;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, BioengineeringWorkstationRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 27, 24).addIngredients(recipe.getIngredients().get(0));
        builder.addSlot(RecipeIngredientRole.INPUT, 27, 6).addIngredients(recipe.getIngredients().get(1));
        builder.addSlot(RecipeIngredientRole.INPUT, 45, 24).addIngredients(recipe.getIngredients().get(2));
        builder.addSlot(RecipeIngredientRole.INPUT, 27, 42).addIngredients(recipe.getIngredients().get(3));
        builder.addSlot(RecipeIngredientRole.INPUT, 9, 24).addIngredients(recipe.getIngredients().get(4));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 113, 24).addItemStack(recipe.getResultItem(null));
    }
}