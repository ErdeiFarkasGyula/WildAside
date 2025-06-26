package net.farkas.wildaside.potion;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraftforge.common.brewing.IBrewingRecipe;

// BetterBrewingRecipe Class by CAS-ual-TY from https://github.com/CAS-ual-TY/Extra-Potions (GPL-3.0 License)
// https://github.com/CAS-ual-TY/Extra-Potions/blob/main/LICENSE

//Updated to 1.21.1 by Erdei Farkas Gyula

public class BetterBrewingRecipe implements IBrewingRecipe {
    private final Potion input;
    private final Item ingredient;
    private final Potion output;

    public BetterBrewingRecipe(Potion input, Item ingredient, Potion output) {
        this.input = input;
        this.ingredient = ingredient;
        this.output = output;
    }

    @Override
    public boolean isInput(ItemStack input) {
        return input.get(DataComponents.POTION_CONTENTS).getAllEffects() == this.input.getEffects();
    }

    @Override
    public boolean isIngredient(ItemStack ingredient) {
        return ingredient.getItem() == this.ingredient;
    }

    @Override
    public ItemStack getOutput(ItemStack input, ItemStack ingredient) {
        if (!this.isInput(input) || !this.isIngredient(ingredient)) {
            return ItemStack.EMPTY;
        }

        return PotionContents.createItemStack(PotionContents.createItemStack(input.getItem(), Holder.direct(this.output)).getItem(), Holder.direct(this.output));
    }
}