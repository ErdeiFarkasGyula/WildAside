package net.farkas.wildaside.datagen;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.block.ModBlocks;
import net.farkas.wildaside.item.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, WildAside.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        //VIBRION
        simpleItem(ModItems.VIBRION);
        simpleItem(ModItems.MUCELLITH_JAW);
        withExistingParent(ModItems.MUCELLITH_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        simpleBlockItemBlockTexture(ModBlocks.VIBRION_GROWTH);
        simpleBlockItem(ModBlocks.VIBRION_GLASS_PANE, ModBlocks.VIBRION_GLASS);
        simpleBlockItem(ModBlocks.LIT_VIBRION_GLASS_PANE, ModBlocks.VIBRION_GLASS);
        simpleBlockItem(ModBlocks.HANGING_VIBRION_VINES, ModBlocks.HANGING_VIBRION_VINES_PLANT);
        simpleBlockItem(ModBlocks.HANGING_VIBRION_VINES_PLANT, ModBlocks.HANGING_VIBRION_VINES_PLANT);

        //ENTORIUM
        simpleItem(ModItems.ENTORIUM);
        simpleItem(ModItems.ENTORIUM_PILL);
        simpleItem(ModItems.SPORE_ARROW);
        simpleItem(ModItems.SPORE_BOMB);
        simpleItem(ModItems.FERTILISER_BOMB);
        evenSimplerBlockItem(ModBlocks.ENTORIUM_ORE);
        evenSimplerBlockItem(ModBlocks.OVERGROWN_ENTORIUM_ORE);

        //SUBSTILIUM
        simpleBlockItem(ModBlocks.SUBSTILIUM_DOOR);
        trapdoorItem(ModBlocks.SUBSTILIUM_TRAPDOOR);
        evenSimplerBlockItem(ModBlocks.SUBSTILIUM_STEM);
        evenSimplerBlockItem(ModBlocks.SUBSTILIUM_WOOD);
        evenSimplerBlockItem(ModBlocks.STRIPPED_SUBSTILIUM_STEM);
        evenSimplerBlockItem(ModBlocks.STRIPPED_SUBSTILIUM_WOOD);
        evenSimplerBlockItem(ModBlocks.SUBSTILIUM_STAIRS);
        evenSimplerBlockItem(ModBlocks.SUBSTILIUM_TILE_STAIRS);
        evenSimplerBlockItem(ModBlocks.SUBSTILIUM_SLAB);
        evenSimplerBlockItem(ModBlocks.SUBSTILIUM_TILE_SLAB);
        evenSimplerBlockItem(ModBlocks.SUBSTILIUM_FENCE_GATE);
        evenSimplerBlockItem(ModBlocks.SUBSTILIUM_PRESSURE_PLATE);
        evenSimplerBlockItem(ModBlocks.SUBSTILIUM_TILE_PRESSURE_PLATE);
        wallItem(ModBlocks.SUBSTILIUM_TILE_WALLS, ModBlocks.SUBSTILIUM_TILES);
        fenceItem(ModBlocks.SUBSTILIUM_FENCE, ModBlocks.SUBSTILIUM_PLANKS);
        buttonItem(ModBlocks.SUBSTILIUM_BUTTON, ModBlocks.SUBSTILIUM_PLANKS);
        buttonItem(ModBlocks.SUBSTILIUM_TILE_BUTTON, ModBlocks.SUBSTILIUM_TILES);
        simpleItem(ModItems.SUBSTILIUM_SIGN);
        simpleItem(ModItems.SUBSTILIUM_HANGING_SIGN);
        simpleItem(ModItems.SUBSTILIUM_BOAT);
        simpleItem(ModItems.SUBSTILIUM_CHEST_BOAT);
        simpleBlockItemBlockTexture(ModBlocks.SUBSTILIUM_SPROUTS);



        //HICKORY
        simpleBlockItem(ModBlocks.HICKORY_DOOR);
        trapdoorItem(ModBlocks.HICKORY_TRAPDOOR);
        evenSimplerBlockItem(ModBlocks.HICKORY_LOG);
        evenSimplerBlockItem(ModBlocks.HICKORY_WOOD);
        evenSimplerBlockItem(ModBlocks.STRIPPED_HICKORY_LOG);
        evenSimplerBlockItem(ModBlocks.STRIPPED_HICKORY_WOOD);
        evenSimplerBlockItem(ModBlocks.HICKORY_STAIRS);
        evenSimplerBlockItem(ModBlocks.HICKORY_SLAB);
        evenSimplerBlockItem(ModBlocks.HICKORY_FENCE_GATE);
        evenSimplerBlockItem(ModBlocks.HICKORY_PRESSURE_PLATE);
        fenceItem(ModBlocks.HICKORY_FENCE, ModBlocks.HICKORY_PLANKS);
        buttonItem(ModBlocks.HICKORY_BUTTON, ModBlocks.HICKORY_PLANKS);
        simpleItem(ModItems.HICKORY_SIGN);
        simpleItem(ModItems.HICKORY_HANGING_SIGN);
        simpleItem(ModItems.HICKORY_BOAT);
        simpleItem(ModItems.HICKORY_CHEST_BOAT);

        simpleItem(ModItems.HICKORY_NUT);
        simpleItem(ModItems.HICKORY_NUT_TRAIL_MIX);
        simpleBlockItemBlockTexture(ModBlocks.HICKORY_SAPLING);
        simpleBlockItemBlockTexture(ModBlocks.RED_GLOWING_HICKORY_SAPLING);
        simpleBlockItemBlockTexture(ModBlocks.BROWN_GLOWING_HICKORY_SAPLING);
        simpleBlockItemBlockTexture(ModBlocks.YELLOW_GLOWING_HICKORY_SAPLING);
        simpleBlockItemBlockTexture(ModBlocks.GREEN_GLOWING_HICKORY_SAPLING);

        simpleBlockItemBlockTexture(ModBlocks.SPOTTED_WINTERGREEN);
        simpleBlockItemBlockTexture(ModBlocks.PINKSTER_FLOWER);

    }

    private ItemModelBuilder simpleItem(RegistryObject<Item> item) {
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                new ResourceLocation(WildAside.MOD_ID,"item/" + item.getId().getPath()));
    }

    public void evenSimplerBlockItem(RegistryObject<Block> block) {
        this.withExistingParent(WildAside.MOD_ID + ":" + ForgeRegistries.BLOCKS.getKey(block.get()).getPath(),
                modLoc("block/" + ForgeRegistries.BLOCKS.getKey(block.get()).getPath()));
    }

    public void trapdoorItem(RegistryObject<Block> block) {
        this.withExistingParent(ForgeRegistries.BLOCKS.getKey(block.get()).getPath(),
                modLoc("block/" + ForgeRegistries.BLOCKS.getKey(block.get()).getPath() + "_bottom"));
    }

    public void fenceItem(RegistryObject<Block> block, RegistryObject<Block> baseBlock) {
        this.withExistingParent(ForgeRegistries.BLOCKS.getKey(block.get()).getPath(), mcLoc("block/fence_inventory"))
                .texture("texture",  new ResourceLocation(WildAside.MOD_ID, "block/" + ForgeRegistries.BLOCKS.getKey(baseBlock.get()).getPath()));
    }

    public void buttonItem(RegistryObject<Block> block, RegistryObject<Block> baseBlock) {
        this.withExistingParent(ForgeRegistries.BLOCKS.getKey(block.get()).getPath(), mcLoc("block/button_inventory"))
                .texture("texture",  new ResourceLocation(WildAside.MOD_ID, "block/" + ForgeRegistries.BLOCKS.getKey(baseBlock.get()).getPath()));
    }

    public void wallItem(RegistryObject<Block> block, RegistryObject<Block> baseBlock) {
        this.withExistingParent(ForgeRegistries.BLOCKS.getKey(block.get()).getPath(), mcLoc("block/wall_inventory"))
                .texture("wall",  new ResourceLocation(WildAside.MOD_ID, "block/" + ForgeRegistries.BLOCKS.getKey(baseBlock.get()).getPath()));
    }

    private ItemModelBuilder simpleBlockItem(RegistryObject<Block> item) {
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                new ResourceLocation(WildAside.MOD_ID,"item/" + item.getId().getPath()));
    }

    private ItemModelBuilder simpleBlockItem(RegistryObject<Block> item, RegistryObject<Block> item2) {
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                new ResourceLocation(WildAside.MOD_ID,"block/" + item2.getId().getPath()));
    }


    private ItemModelBuilder simpleBlockItemBlockTexture(RegistryObject<Block> item) {
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                new ResourceLocation(WildAside.MOD_ID,"block/" + item.getId().getPath()));
    }


    private ItemModelBuilder handheldItem(RegistryObject<Item> item) {
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/handheld")).texture("layer0",
                new ResourceLocation(WildAside.MOD_ID,"item/" + item.getId().getPath()));
    }
}