package net.farkas.wildaside.recipe;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public record BioengineeringWorkstationRecipe(List<Ingredient> ingredients, ItemStack output) implements Recipe<BioengineeringWorkstationRecipeInput> {

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.addAll(ingredients);
        return list;
    }

    @Override
    public boolean matches(BioengineeringWorkstationRecipeInput pInput, Level pLevel) {
        if (pLevel.isClientSide()) return false;

        List<ItemStack> stacks = new ArrayList<>();

        for (int i = 0; i < pInput.size(); i++) {
            ItemStack s = pInput.getItem(i);
            if (!s.isEmpty()) {
                stacks.add(s.copy());
            }
        }

        for (Ingredient ing : ingredients) {
            if (ing == Ingredient.EMPTY) continue;

            boolean matched = false;
            for (Iterator<ItemStack> it = stacks.iterator(); it.hasNext();) {
                ItemStack candidate = it.next();
                if (ing.test(candidate)) {
                    it.remove();
                    matched = true;
                    break;
                }
            }

            if (!matched) return false;
        }

        return true;
    }

    @Override
    public ItemStack assemble(BioengineeringWorkstationRecipeInput pInput, HolderLookup.Provider pRegistries) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider pRegistries) {
        return output;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.BIOENGINEERING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.BIOENGINEERING_TYPE.get();
    }

    public static final Codec<Ingredient> INGREDIENT_ALLOW_EMPTY =
            Codec.either(Ingredient.CODEC_NONEMPTY, Codec.unit(Ingredient.EMPTY))
                    .xmap(
                            e -> e.map(i -> i, i -> i),
                            ing -> ing.isEmpty()
                                    ? Either.right(Ingredient.EMPTY)
                                    : Either.left(ing)
                    );

    public static class Serializer implements RecipeSerializer<BioengineeringWorkstationRecipe> {
        public static final MapCodec<BioengineeringWorkstationRecipe> CODEC =
                RecordCodecBuilder.mapCodec(inst -> inst.group(
                        Codec.list(INGREDIENT_ALLOW_EMPTY)
                                .fieldOf("ingredients")
                                .forGetter(BioengineeringWorkstationRecipe::ingredients),
                        ItemStack.CODEC
                                .fieldOf("output")
                                .forGetter(BioengineeringWorkstationRecipe::output)
                ).apply(inst, BioengineeringWorkstationRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, BioengineeringWorkstationRecipe> STREAM_CODEC =
                StreamCodec.of(
                        (buf, recipe) -> {
                            for (Ingredient ing : recipe.ingredients()) {
                                Ingredient.CONTENTS_STREAM_CODEC.encode(buf, ing);
                            }
                            ItemStack.STREAM_CODEC.encode(buf, recipe.output());
                        },
                        buf -> {
                            List<Ingredient> ings = new ArrayList<>(5);
                            for (int i = 0; i < 5; i++) {
                                ings.add(Ingredient.CONTENTS_STREAM_CODEC.decode(buf));
                            }
                            ItemStack out = ItemStack.STREAM_CODEC.decode(buf);
                            return new BioengineeringWorkstationRecipe(ings, out);
                        }
                );


        @Override
        public MapCodec<BioengineeringWorkstationRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, BioengineeringWorkstationRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}