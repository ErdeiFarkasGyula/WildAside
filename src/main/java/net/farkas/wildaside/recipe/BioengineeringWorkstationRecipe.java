package net.farkas.wildaside.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import net.farkas.wildaside.WildAside;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BioengineeringWorkstationRecipe implements Recipe<SimpleContainer> {
    private final NonNullList<Ingredient> inputItems;
    private final ItemStack output;
    private final ResourceLocation id;

    public BioengineeringWorkstationRecipe(NonNullList<Ingredient> inputItems, ItemStack output, ResourceLocation id) {
        this.inputItems = inputItems;
        this.output = output;
        this.id = id;
    }

    @Override
    public boolean matches(SimpleContainer inv, Level level) {
        if (level.isClientSide()) return false;

        List<ItemStack> stacks = new ArrayList<>();

        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack s = inv.getItem(i);
            if (!s.isEmpty()) {
                stacks.add(s.copy());
            }
        }

        for (Ingredient ing : inputItems) {
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

        return stacks.isEmpty();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return inputItems;
    }

    @Override
    public ItemStack assemble(SimpleContainer pContainer, RegistryAccess pRegistryAccess) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return output.copy();
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<BioengineeringWorkstationRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "bioengineering";
    }

    public static class Serializer implements RecipeSerializer<BioengineeringWorkstationRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation(WildAside.MOD_ID, "bioengineering");

        @Override
        public BioengineeringWorkstationRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "output"));

            JsonArray ingredients = GsonHelper.getAsJsonArray(pSerializedRecipe, "ingredients");
            NonNullList<Ingredient> inputs = NonNullList.withSize(5, Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); i++) {
                JsonElement element = ingredients.size() > i ? ingredients.get(i) : JsonNull.INSTANCE;

                if (element.isJsonNull() || (element.isJsonObject() && element.getAsJsonObject().entrySet().isEmpty())) {
                    inputs.set(i, Ingredient.EMPTY);
                } else {
                    inputs.set(i, Ingredient.fromJson(element));
                }
            }

            return new BioengineeringWorkstationRecipe(inputs, output, pRecipeId);
        }

        @Override
        public @Nullable BioengineeringWorkstationRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            NonNullList<Ingredient> inputs = NonNullList.withSize(pBuffer.readInt(), Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromNetwork(pBuffer));
            }

            ItemStack output = pBuffer.readItem();
            return new BioengineeringWorkstationRecipe(inputs, output, pRecipeId);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, BioengineeringWorkstationRecipe pRecipe) {
            pBuffer.writeInt(pRecipe.inputItems.size());

            for (Ingredient ingredient : pRecipe.getIngredients()) {
                ingredient.toNetwork(pBuffer);
            }

            pBuffer.writeItemStack(pRecipe.getResultItem(null), false);
        }
    }
}