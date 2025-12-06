package net.farkas.wildaside.datagen;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.block.ModBlocks;
import net.farkas.wildaside.item.ModItems;
import net.farkas.wildaside.item.custom.HickoryNutTrailMixItem;
import net.farkas.wildaside.util.HickoryColour;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;

import java.util.List;
import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> recipeOutput) {
        //SMELTING
        smelting(recipeOutput, List.of(ModItems.VIBRION.get()), RecipeCategory.MISC, Items.YELLOW_DYE, 0.25f, 200, "yellow dye");
        smelting(recipeOutput, List.of(ModBlocks.VIBRION_GEL.get()), RecipeCategory.MISC, ModItems.VIBRION.get(), 0.25f, 200, "vibrion");
        smelting(recipeOutput, List.of(ModBlocks.LIT_VIBRION_GEL.get()), RecipeCategory.MISC, ModItems.VIBRION.get(), 0.25f, 200, "vibrion");

        smelting(recipeOutput, List.of(Items.GLASS_BOTTLE), RecipeCategory.TOOLS, ModItems.DNA_HOLDER.get(), 0.0f, 100, "dna_holder");

        //SHAPED
        simpleShapedRecipe(ModBlocks.VIBRION_BLOCK.get(), 1, ModItems.VIBRION.get()).save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.VIBRION_GLASS_PANE.get(), 16)
                .pattern("SSS")
                .pattern("SSS")
                .define('S', ModBlocks.VIBRION_GLASS.get())
                .unlockedBy(getHasName( ModBlocks.VIBRION_GLASS.get()), has( ModBlocks.VIBRION_GLASS.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.LIT_VIBRION_GLASS_PANE.get(), 16)
                .pattern("SSS")
                .pattern("SSS")
                .define('S', ModBlocks.LIT_VIBRION_GLASS.get())
                .unlockedBy(getHasName( ModBlocks.LIT_VIBRION_GLASS.get()), has( ModBlocks.LIT_VIBRION_GLASS.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BIOENGINEERING_WORKSTATION.get())
                .pattern("ECE")
                .pattern("VQV")
                .define('Q', Blocks.QUARTZ_BLOCK)
                .define('C', Blocks.CRAFTING_TABLE)
                .define('V', ModBlocks.VIBRION_BLOCK.get())
                .define('E', ModItems.ENTORIUM.get())
                .unlockedBy(getHasName(ModBlocks.VIBRION_BLOCK.get()), has(ModBlocks.VIBRION_BLOCK.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.SYRINGE.get())
                .pattern("N")
                .pattern("B")
                .pattern("P")
                .define('N', Items.IRON_NUGGET)
                .define('B', Items.GLASS_BOTTLE)
                .define('P', Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE)
                .unlockedBy(getHasName(Items.GLASS_BOTTLE), has(Items.GLASS_BOTTLE))
                .save(recipeOutput);

        simpleShapedRecipe(ModBlocks.COMPRESSED_SUBSTILIUM_SOIL.get(), 2, ModBlocks.SUBSTILIUM_SOIL.get(), RecipeCategory.BUILDING_BLOCKS).save(recipeOutput);
        simpleShapedRecipe(ModBlocks.SMOOTH_SUBSTILIUM_SOIL.get(), 4, ModBlocks.COMPRESSED_SUBSTILIUM_SOIL.get(), RecipeCategory.BUILDING_BLOCKS).save(recipeOutput);
        simpleShapedRecipe(ModBlocks.SUBSTILIUM_TILES.get(), 4, ModBlocks.SMOOTH_SUBSTILIUM_SOIL.get(), RecipeCategory.BUILDING_BLOCKS).save(recipeOutput);

        //SHAPELESS
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.VIBRION.get(), 4)
                .requires(ModBlocks.VIBRION_BLOCK.get())
                .unlockedBy(getHasName(ModBlocks.VIBRION_BLOCK.get()), has(ModBlocks.VIBRION_BLOCK.get()))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.PINK_DYE, 1)
                .requires(ModBlocks.PINKSTER_FLOWER.get())
                .unlockedBy(getHasName(ModBlocks.PINKSTER_FLOWER.get()), has(ModBlocks.PINKSTER_FLOWER.get()))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.WHITE_DYE, 1)
                .requires(ModBlocks.SPOTTED_WINTERGREEN.get())
                .unlockedBy(getHasName(ModBlocks.SPOTTED_WINTERGREEN.get()), has(ModBlocks.SPOTTED_WINTERGREEN.get()))
                .save(recipeOutput);

        //WOODSET
        List<ItemLike> SUBSTILIUM_WOODSET = List.of(ModBlocks.SUBSTILIUM_STEM.get().asItem(), ModBlocks.STRIPPED_SUBSTILIUM_STEM.get(),
                ModBlocks.SUBSTILIUM_WOOD.get(), ModBlocks.STRIPPED_SUBSTILIUM_WOOD.get(), ModBlocks.SUBSTILIUM_PLANKS.get(),
                ModBlocks.SUBSTILIUM_STAIRS.get(), ModBlocks.SUBSTILIUM_SLAB.get(), ModBlocks.SUBSTILIUM_FENCE.get(),
                ModBlocks.SUBSTILIUM_FENCE_GATE.get(),  ModBlocks.SUBSTILIUM_PRESSURE_PLATE.get(), ModBlocks.SUBSTILIUM_BUTTON.get(),
                ModBlocks.SUBSTILIUM_DOOR.get(), ModBlocks.SUBSTILIUM_TRAPDOOR.get(), ModBlocks.SUBSTILIUM_SIGN.get(), ModBlocks.SUBSTILIUM_HANGING_SIGN.get(),
                ModItems.SUBSTILIUM_BOAT.get(), ModItems.SUBSTILIUM_CHEST_BOAT.get());
        defaultWoodSet(recipeOutput, SUBSTILIUM_WOODSET);

        List<ItemLike> HICKORY_WOODSET = List.of(ModBlocks.HICKORY_LOG.get().asItem(), ModBlocks.STRIPPED_HICKORY_LOG.get(),
                ModBlocks.HICKORY_WOOD.get(), ModBlocks.STRIPPED_HICKORY_WOOD.get(), ModBlocks.HICKORY_PLANKS.get(),
                ModBlocks.HICKORY_STAIRS.get(), ModBlocks.HICKORY_SLAB.get(), ModBlocks.HICKORY_FENCE.get(),
                ModBlocks.HICKORY_FENCE_GATE.get(),  ModBlocks.HICKORY_PRESSURE_PLATE.get(), ModBlocks.HICKORY_BUTTON.get(),
                ModBlocks.HICKORY_DOOR.get(), ModBlocks.HICKORY_TRAPDOOR.get(), ModBlocks.HICKORY_SIGN.get(), ModBlocks.HICKORY_HANGING_SIGN.get(),
                ModItems.HICKORY_BOAT.get(), ModItems.HICKORY_CHEST_BOAT.get());
        defaultWoodSet(recipeOutput, HICKORY_WOODSET);

        //STONE
        Block smooth_soil = ModBlocks.SMOOTH_SUBSTILIUM_SOIL.get();
        Block tiles = ModBlocks.SUBSTILIUM_TILES.get();
        Block compressed = ModBlocks.COMPRESSED_SUBSTILIUM_SOIL.get();

        stoneCutting(recipeOutput, smooth_soil, tiles);
        stoneCutting(recipeOutput, smooth_soil, ModBlocks.CRACKED_SUBSTILIUM_TILES.get());
        stoneCutting(recipeOutput, smooth_soil, ModBlocks.SMOOTH_SUBSTILIUM_SOIL_STAIRS.get());
        stoneCutting(recipeOutput, smooth_soil, ModBlocks.SMOOTH_SUBSTILIUM_SOIL_SLAB.get());
        stoneCutting(recipeOutput, smooth_soil, ModBlocks.SMOOTH_SUBSTILIUM_SOIL_BUTTON.get());
        stoneCutting(recipeOutput, smooth_soil, ModBlocks.SMOOTH_SUBSTILIUM_SOIL_PRESSURE_PLATE.get());
        stoneCutting(recipeOutput, smooth_soil, ModBlocks.SMOOTH_SUBSTILIUM_SOIL_WALLS.get());

        stairsRecipe(recipeOutput, ModBlocks.SMOOTH_SUBSTILIUM_SOIL_STAIRS.get(), smooth_soil);
        slabBuilder(RecipeCategory.BUILDING_BLOCKS, ModBlocks.SMOOTH_SUBSTILIUM_SOIL_SLAB.get(), Ingredient.of(smooth_soil)).unlockedBy(getHasName(smooth_soil), has(smooth_soil)).save(recipeOutput);
        wallBuilder(RecipeCategory.BUILDING_BLOCKS, ModBlocks.SMOOTH_SUBSTILIUM_SOIL_WALLS.get(), Ingredient.of(smooth_soil)).unlockedBy(getHasName(smooth_soil), has(smooth_soil)).save(recipeOutput);
        pressurePlateBuilder(RecipeCategory.REDSTONE, ModBlocks.SMOOTH_SUBSTILIUM_SOIL_PRESSURE_PLATE.get(), Ingredient.of(smooth_soil)).unlockedBy(getHasName(smooth_soil), has(smooth_soil)).save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, ModBlocks.SMOOTH_SUBSTILIUM_SOIL_BUTTON.get()).requires(smooth_soil).unlockedBy(getHasName(smooth_soil), has(smooth_soil)).save(recipeOutput);

        stoneCutting(recipeOutput, compressed, ModBlocks.CHISELED_SUBSTILIUM_SOIL.get());
        stoneCutting(recipeOutput, compressed, ModBlocks.SMOOTH_SUBSTILIUM_SOIL.get());
        stoneCutting(recipeOutput, compressed, tiles);
        stoneCutting(recipeOutput, compressed, ModBlocks.CRACKED_SUBSTILIUM_TILES.get());
        stoneCutting(recipeOutput, tiles, ModBlocks.CRACKED_SUBSTILIUM_TILES.get());
        stoneCutting(recipeOutput, tiles, ModBlocks.SUBSTILIUM_TILE_STAIRS.get());
        stoneCutting(recipeOutput, tiles, ModBlocks.SUBSTILIUM_TILE_SLAB.get());
        stoneCutting(recipeOutput, tiles, ModBlocks.SUBSTILIUM_TILE_BUTTON.get());
        stoneCutting(recipeOutput, tiles, ModBlocks.SUBSTILIUM_TILE_PRESSURE_PLATE.get());
        stoneCutting(recipeOutput, tiles, ModBlocks.SUBSTILIUM_TILE_WALLS.get());

        stairsRecipe(recipeOutput, ModBlocks.SUBSTILIUM_TILE_STAIRS.get(), tiles);
        slabBuilder(RecipeCategory.BUILDING_BLOCKS, ModBlocks.SUBSTILIUM_TILE_SLAB.get(), Ingredient.of(tiles)).unlockedBy(getHasName(tiles), has(tiles)).save(recipeOutput);
        wallBuilder(RecipeCategory.BUILDING_BLOCKS, ModBlocks.SUBSTILIUM_TILE_WALLS.get(), Ingredient.of(tiles)).unlockedBy(getHasName(tiles), has(tiles)).save(recipeOutput);
        pressurePlateBuilder(RecipeCategory.REDSTONE, ModBlocks.SUBSTILIUM_TILE_PRESSURE_PLATE.get(), Ingredient.of(tiles)).unlockedBy(getHasName(tiles), has(tiles)).save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, ModBlocks.SUBSTILIUM_TILE_BUTTON.get()).requires(tiles).unlockedBy(getHasName(tiles), has(tiles)).save(recipeOutput);

        smelting(recipeOutput, List.of(tiles), RecipeCategory.BUILDING_BLOCKS, ModBlocks.CRACKED_SUBSTILIUM_TILES.get(), 0.20f, 200, "cracked_substilium_tiles");

        //ORE
        smelting(recipeOutput, List.of(ModBlocks.ENTORIUM_ORE.get()), RecipeCategory.MISC, ModItems.ENTORIUM.get(), 0.5f, 200, "entorium");
        blasting(recipeOutput, List.of(ModBlocks.ENTORIUM_ORE.get()), RecipeCategory.MISC, ModItems.ENTORIUM.get(), 0.5f, 100, "entorium");
        smelting(recipeOutput, List.of(ModBlocks.SUBSTILIUM_COAL_ORE.get()), RecipeCategory.MISC, Items.COAL, 0.2f, 200, "coal");
        blasting(recipeOutput, List.of(ModBlocks.SUBSTILIUM_COAL_ORE.get()), RecipeCategory.MISC, Items.COAL, 0.3f, 100, "coal");
        smelting(recipeOutput, List.of(ModBlocks.SUBSTILIUM_COPPER_ORE.get()), RecipeCategory.MISC, Items.COPPER_INGOT, 0.8f, 200, "copper_ingot");
        blasting(recipeOutput, List.of(ModBlocks.SUBSTILIUM_COPPER_ORE.get()), RecipeCategory.MISC, Items.COPPER_INGOT, 0.8f, 100, "copper_ingot");
        smelting(recipeOutput, List.of(ModBlocks.SUBSTILIUM_LAPIS_ORE.get()), RecipeCategory.MISC, Items.LAPIS_LAZULI, 0.3f, 200, "lapis");
        blasting(recipeOutput, List.of(ModBlocks.SUBSTILIUM_LAPIS_ORE.get()), RecipeCategory.MISC, Items.LAPIS_LAZULI, 0.3f, 100, "lapis");
        smelting(recipeOutput, List.of(ModBlocks.SUBSTILIUM_IRON_ORE.get()), RecipeCategory.MISC, Items.IRON_INGOT, 0.8f, 200, "iron_ingot");
        blasting(recipeOutput, List.of(ModBlocks.SUBSTILIUM_IRON_ORE.get()), RecipeCategory.MISC, Items.IRON_INGOT, 0.8f, 100, "iron_ingot");
        smelting(recipeOutput, List.of(ModBlocks.SUBSTILIUM_GOLD_ORE.get()), RecipeCategory.MISC, Items.GOLD_INGOT, 1.1f, 200, "gold_ingot");
        blasting(recipeOutput, List.of(ModBlocks.SUBSTILIUM_GOLD_ORE.get()), RecipeCategory.MISC, Items.GOLD_INGOT, 1.1f, 100, "gold_ingot");
        smelting(recipeOutput, List.of(ModBlocks.SUBSTILIUM_REDSTONE_ORE.get()), RecipeCategory.MISC, Items.REDSTONE, 0.4f, 200, "redstone");
        blasting(recipeOutput, List.of(ModBlocks.SUBSTILIUM_REDSTONE_ORE.get()), RecipeCategory.MISC, Items.REDSTONE, 0.4f, 100, "redstone");
        smelting(recipeOutput, List.of(ModBlocks.SUBSTILIUM_DIAMOND_ORE.get()), RecipeCategory.MISC, Items.DIAMOND, 1.1f, 200, "diamond");
        blasting(recipeOutput, List.of(ModBlocks.SUBSTILIUM_DIAMOND_ORE.get()), RecipeCategory.MISC, Items.DIAMOND, 1.1f, 100, "diamond");
        smelting(recipeOutput, List.of(ModBlocks.SUBSTILIUM_EMERALD_ORE.get()), RecipeCategory.MISC, Items.EMERALD, 1.1f, 200, "emerald");
        blasting(recipeOutput, List.of(ModBlocks.SUBSTILIUM_EMERALD_ORE.get()), RecipeCategory.MISC, Items.EMERALD, 1.1f, 100, "emerald");

        //HICKORY
        for (HickoryColour colour : HickoryColour.values()) {
            simpleShapedRecipe(ModBlocks.HICKORY_LEAVES_BLOCKS.get(colour).get(), 1, ModItems.LEAF_ITEMS.get(colour).get()).save(recipeOutput);
            hickoryNutTrailMix((HickoryNutTrailMixItem)ModItems.TRAIL_MIX_ITEMS.get(colour).get()).save(recipeOutput);
        }
    }

    private ShapedRecipeBuilder simpleShapedRecipe(ItemLike output, int count, ItemLike input, RecipeCategory category) {
        return ShapedRecipeBuilder.shaped(category, output, count)
                .pattern("SS")
                .pattern("SS")
                .define('S', input)
                .unlockedBy(getHasName(input), has(input));
    }

    private ShapedRecipeBuilder simpleShapedRecipe(ItemLike output, int count, ItemLike input) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.MISC, output, count)
                .pattern("SS")
                .pattern("SS")
                .define('S', input)
                .unlockedBy(getHasName(input), has(input));
    }

    private ShapelessRecipeBuilder hickoryNutTrailMix(HickoryNutTrailMixItem mixItem) {
         return ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, mixItem)
                .requires(ModItems.HICKORY_NUT.get())
                .requires(Items.BOWL)
                .requires(Items.GLOW_BERRIES)
                .requires(Items.SWEET_BERRIES)
                .requires(ModItems.LEAF_ITEMS.get(mixItem.getColour()).get(), 3)
                .unlockedBy(getHasName(ModItems.HICKORY_NUT.get()), has(ModItems.HICKORY_NUT.get()));
    }

    protected static void smelting(Consumer<FinishedRecipe> pFinishedRecipeConsumer, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult, float pExperience, int pCookingTIme, String pGroup) {
        oreCooking(pFinishedRecipeConsumer, RecipeSerializer.SMELTING_RECIPE, pIngredients, pCategory, pResult, pExperience, pCookingTIme, pGroup, "_from_smelting");
    }

    protected static void blasting(Consumer<FinishedRecipe> pFinishedRecipeConsumer, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult, float pExperience, int pCookingTime, String pGroup) {
        oreCooking(pFinishedRecipeConsumer, RecipeSerializer.BLASTING_RECIPE, pIngredients, pCategory, pResult, pExperience, pCookingTime, pGroup, "_from_blasting");
    }

    protected static void oreCooking(Consumer<FinishedRecipe> pFinishedRecipeConsumer, RecipeSerializer<? extends AbstractCookingRecipe> pCookingSerializer, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult, float pExperience, int pCookingTime, String pGroup, String pRecipeName) {
        for (ItemLike itemlike : pIngredients) {
            SimpleCookingRecipeBuilder.generic(Ingredient.of(itemlike), pCategory, pResult,
                            pExperience, pCookingTime, pCookingSerializer)
                    .group(pGroup).unlockedBy(getHasName(itemlike), has(itemlike))
                    .save(pFinishedRecipeConsumer,  WildAside.MOD_ID + ":" + getItemName(pResult) + pRecipeName + "_" + getItemName(itemlike));
        }
    }

    protected static void stoneCutting(Consumer<FinishedRecipe> pWriter, ItemLike inputItem, ItemLike outputItem) {
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(inputItem), RecipeCategory.BUILDING_BLOCKS, outputItem).unlockedBy(getHasName(inputItem), has(inputItem))
                .save(pWriter, WildAside.MOD_ID + ":" + getItemName(outputItem) + "cut_from_" + getItemName(inputItem));
    }

    protected static void stairsRecipe(Consumer<FinishedRecipe> pWriter, ItemLike outputBlock, ItemLike inputBlock) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, outputBlock, 6)
                .pattern("S  ")
                .pattern("SS ")
                .pattern("SSS")
                .define('S', inputBlock)
                .unlockedBy(getHasName(inputBlock), has(inputBlock))
                .save(pWriter, WildAside.MOD_ID + ":" + getItemName(outputBlock) + "_1");

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, outputBlock, 6)
                .pattern("  S")
                .pattern(" SS")
                .pattern("SSS")
                .define('S', inputBlock)
                .unlockedBy(getHasName(inputBlock), has(inputBlock))
                .save(pWriter, WildAside.MOD_ID + ":" + getItemName(outputBlock) + "_2");

    }

    private static void defaultWoodSet(Consumer<FinishedRecipe> pWriter, List<ItemLike> blocks) {
        ItemLike log = blocks.get(0);
        ItemLike str_log = blocks.get(1);
        ItemLike wood = blocks.get(2);
        ItemLike str_wood = blocks.get(3);
        Ingredient planks = Ingredient.of(blocks.get(4));
        ItemLike stairs = blocks.get(5);
        ItemLike slab = blocks.get(6);
        ItemLike fence = blocks.get(7);
        ItemLike gate = blocks.get(8);
        ItemLike press = blocks.get(9);
        ItemLike button = blocks.get(10);
        ItemLike door = blocks.get(11);
        ItemLike trapdoor = blocks.get(12);
        ItemLike sign = blocks.get(13);
        ItemLike hang_sign = blocks.get(14);
        ItemLike boat = blocks.get(15);
        ItemLike chest_boat = blocks.get(16);

        List<ItemLike> PLANKS_ORIGIN = List.of(log, str_log, wood, str_wood);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, wood, 4)
                .pattern("SS")
                .pattern("SS")
                .define('S', log.asItem())
                .unlockedBy(getHasName(log), has(log))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, str_wood, 4)
                .pattern("SS")
                .pattern("SS")
                .define('S', str_log.asItem())
                .unlockedBy(getHasName(str_log), has(str_log))
                .save(pWriter);

        for (ItemLike itemLike : PLANKS_ORIGIN) {
            ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, blocks.get(4), 4)
                    .requires(itemLike)
                    .unlockedBy(getHasName(itemLike), has(itemLike))
                    .group(getItemName(blocks.get(4)))
                    .save(pWriter, WildAside.MOD_ID + ":" + getItemName(blocks.get(4)) + "_" + getItemName(itemLike));
        }

        stairsRecipe(pWriter, stairs, blocks.get(4));
        slabBuilder(RecipeCategory.BUILDING_BLOCKS, slab, planks).unlockedBy(getHasName(blocks.get(4)), has(blocks.get(4))).save(pWriter);
        fenceBuilder(fence, planks).unlockedBy(getHasName(blocks.get(4)), has(blocks.get(4))).save(pWriter);
        fenceGateBuilder(gate, planks).unlockedBy(getHasName(blocks.get(4)), has(blocks.get(4))).save(pWriter);
        pressurePlateBuilder(RecipeCategory.REDSTONE, press, planks).unlockedBy(getHasName(blocks.get(4)), has(blocks.get(4))).save(pWriter);
        buttonBuilder(button, planks).unlockedBy(getHasName(blocks.get(4)), has(blocks.get(4))).save(pWriter);
        doorBuilder(door,  planks).unlockedBy(getHasName(blocks.get(4)), has(blocks.get(4))).save(pWriter);
        trapdoorBuilder(trapdoor, planks).unlockedBy(getHasName(blocks.get(4)), has(blocks.get(4))).save(pWriter);
        signBuilder(sign, planks).unlockedBy(getHasName(blocks.get(4)), has(blocks.get(4))).save(pWriter);
        hangingSign(pWriter, hang_sign, blocks.get(4));
        woodenBoat(pWriter, boat, blocks.get(4));
        chestBoat(pWriter, chest_boat, blocks.get(15));
    }
}