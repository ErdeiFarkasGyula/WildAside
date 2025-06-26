package net.farkas.wildaside.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

import java.util.List;

public record BioengineeringWorkstationRecipeInput(List<ItemStack> input) implements RecipeInput {
    @Override
    public ItemStack getItem(int pIndex) {
        return input.get(pIndex);
    }

    @Override
    public int size() {
        return 5;
    }
}
