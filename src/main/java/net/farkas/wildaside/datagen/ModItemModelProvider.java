package net.farkas.wildaside.datagen;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.block.ModBlocks;
import net.farkas.wildaside.dna.DnaConstants;
import net.farkas.wildaside.item.ModItems;
import net.farkas.wildaside.item.custom.DnaHolderItem;
import net.farkas.wildaside.item.custom.SyringeItem;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static net.farkas.wildaside.dna.DnaConstants.*;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, WildAside.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        //SPAWNEGG
        spawnEggItem(ModItems.MUCELLITH_SPAWN_EGG.getId());
        spawnEggItem(ModItems.HICKORY_TREANT_SPAWN_EGG.getId());
        spawnEggItem(ModItems.CONTAMINATED_CREEPER_SPAWN_EGG.getId());

        //SIMPLE
        simpleItem(ModItems.VIBRION);
        simpleItem(ModItems.MUCELLITH_JAW);

        simpleItem(ModItems.ENTORIUM);
        simpleItem(ModItems.ENTORIUM_PILL);
        simpleItem(ModItems.SPORE_ARROW);
        simpleItem(ModItems.SPORE_BOMB);
        simpleItem(ModItems.FERTILISER_BOMB);

        simpleItem(ModItems.GENE);

        simpleItem(ModItems.HICKORY_NUT);
        simpleItem(ModItems.HICKORY_NUT_TRAIL_MIX);
        simpleItem(ModItems.RED_HICKORY_NUT_TRAIL_MIX);
        simpleItem(ModItems.BROWN_HICKORY_NUT_TRAIL_MIX);
        simpleItem(ModItems.YELLOW_HICKORY_NUT_TRAIL_MIX);
        simpleItem(ModItems.GREEN_HICKORY_NUT_TRAIL_MIX);

        simpleItem(ModItems.HICKORY_LEAF);
        simpleItem(ModItems.RED_GLOWING_HICKORY_LEAF);
        simpleItem(ModItems.BROWN_GLOWING_HICKORY_LEAF);
        simpleItem(ModItems.YELLOW_GLOWING_HICKORY_LEAF);
        simpleItem(ModItems.GREEN_GLOWING_HICKORY_LEAF);

        //SIMPLE BLOCK ITEM
        simpleBlockItem(ModBlocks.VIBRION_GLASS_PANE, ModBlocks.VIBRION_GLASS);
        simpleBlockItem(ModBlocks.LIT_VIBRION_GLASS_PANE, ModBlocks.VIBRION_GLASS);
        simpleBlockItem(ModBlocks.HANGING_VIBRION_VINES, ModBlocks.HANGING_VIBRION_VINES_PLANT);
        simpleBlockItem(ModBlocks.HANGING_VIBRION_VINES_PLANT, ModBlocks.HANGING_VIBRION_VINES_PLANT);

        //EVEN SIMPLER
        evenSimplerBlockItem(ModBlocks.SUBSTILIUM_STEM);
        evenSimplerBlockItem(ModBlocks.SUBSTILIUM_WOOD);
        evenSimplerBlockItem(ModBlocks.STRIPPED_SUBSTILIUM_STEM);
        evenSimplerBlockItem(ModBlocks.STRIPPED_SUBSTILIUM_WOOD);

        evenSimplerBlockItem(ModBlocks.HICKORY_LOG);
        evenSimplerBlockItem(ModBlocks.HICKORY_WOOD);
        evenSimplerBlockItem(ModBlocks.STRIPPED_HICKORY_LOG);
        evenSimplerBlockItem(ModBlocks.STRIPPED_HICKORY_WOOD);

        //SIMPLE BLOCK ITEM BLOCK TEXTURE
        simpleBlockItemBlockTexture(ModBlocks.SUBSTILIUM_SPROUTS);
        simpleBlockItemBlockTexture(ModBlocks.VIBRION_GROWTH);

        simpleBlockItemBlockTexture(ModBlocks.HICKORY_SAPLING);
        simpleBlockItemBlockTexture(ModBlocks.RED_GLOWING_HICKORY_SAPLING);
        simpleBlockItemBlockTexture(ModBlocks.BROWN_GLOWING_HICKORY_SAPLING);
        simpleBlockItemBlockTexture(ModBlocks.YELLOW_GLOWING_HICKORY_SAPLING);
        simpleBlockItemBlockTexture(ModBlocks.GREEN_GLOWING_HICKORY_SAPLING);

        simpleBlockItemBlockTexture(ModBlocks.HICKORY_ROOT_BUSH);
        simpleBlockItemBlockTexture(ModBlocks.SPOTTED_WINTERGREEN);
        simpleBlockItemBlockTexture(ModBlocks.PINKSTER_FLOWER);

        //STAIRS
        evenSimplerBlockItem(ModBlocks.SUBSTILIUM_STAIRS);
        evenSimplerBlockItem(ModBlocks.SMOOTH_SUBSTILIUM_SOIL_STAIRS);
        evenSimplerBlockItem(ModBlocks.SUBSTILIUM_TILE_STAIRS);
        evenSimplerBlockItem(ModBlocks.HICKORY_STAIRS);

        //SLAB
        evenSimplerBlockItem(ModBlocks.SUBSTILIUM_SLAB);
        evenSimplerBlockItem(ModBlocks.SMOOTH_SUBSTILIUM_SOIL_SLAB);
        evenSimplerBlockItem(ModBlocks.SUBSTILIUM_TILE_SLAB);
        evenSimplerBlockItem(ModBlocks.HICKORY_SLAB);

        //WALLS
        wallItem(ModBlocks.SUBSTILIUM_TILE_WALLS, ModBlocks.SUBSTILIUM_TILES);
        wallItem(ModBlocks.SMOOTH_SUBSTILIUM_SOIL_WALLS, ModBlocks.SMOOTH_SUBSTILIUM_SOIL);

        //FENCE
        fenceItem(ModBlocks.SUBSTILIUM_FENCE, ModBlocks.SUBSTILIUM_PLANKS);
        fenceItem(ModBlocks.HICKORY_FENCE, ModBlocks.HICKORY_PLANKS);

        //FENCEGATE
        evenSimplerBlockItem(ModBlocks.SUBSTILIUM_FENCE_GATE);
        evenSimplerBlockItem(ModBlocks.HICKORY_FENCE_GATE);

        //PRESSUREPLATE
        evenSimplerBlockItem(ModBlocks.SUBSTILIUM_PRESSURE_PLATE);
        evenSimplerBlockItem(ModBlocks.SMOOTH_SUBSTILIUM_SOIL_PRESSURE_PLATE);
        evenSimplerBlockItem(ModBlocks.SUBSTILIUM_TILE_PRESSURE_PLATE);
        evenSimplerBlockItem(ModBlocks.HICKORY_PRESSURE_PLATE);

        //BUTTON
        buttonItem(ModBlocks.SUBSTILIUM_BUTTON, ModBlocks.SUBSTILIUM_PLANKS);
        buttonItem(ModBlocks.SMOOTH_SUBSTILIUM_SOIL_BUTTON, ModBlocks.SMOOTH_SUBSTILIUM_SOIL);
        buttonItem(ModBlocks.SUBSTILIUM_TILE_BUTTON, ModBlocks.SUBSTILIUM_TILES);
        buttonItem(ModBlocks.HICKORY_BUTTON, ModBlocks.HICKORY_PLANKS);

        //DOOR
        simpleBlockItem(ModBlocks.SUBSTILIUM_DOOR);
        simpleBlockItem(ModBlocks.HICKORY_DOOR);

        //TRAPDOOR
        trapdoorItem(ModBlocks.SUBSTILIUM_TRAPDOOR);
        trapdoorItem(ModBlocks.HICKORY_TRAPDOOR);

        //SIGN
        simpleItem(ModItems.SUBSTILIUM_SIGN);
        simpleItem(ModItems.SUBSTILIUM_HANGING_SIGN);
        simpleItem(ModItems.HICKORY_SIGN);
        simpleItem(ModItems.HICKORY_HANGING_SIGN);

        //BOAT
        simpleItem(ModItems.SUBSTILIUM_BOAT);
        simpleItem(ModItems.SUBSTILIUM_CHEST_BOAT);
        simpleItem(ModItems.HICKORY_BOAT);
        simpleItem(ModItems.HICKORY_CHEST_BOAT);

        //CUSTOM
        dnaHolder(ModItems.DNA_HOLDER.get());
        syringe(ModItems.SYRINGE.get());
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

    public ItemModelBuilder spawnEggItem(ResourceLocation item) {
        return this.getBuilder(item.toString()).parent(new ModelFile.UncheckedModelFile("item/template_spawn_egg"));
    }

    public void wallItem(RegistryObject<Block> block, RegistryObject<Block> baseBlock) {
        this.withExistingParent(ForgeRegistries.BLOCKS.getKey(block.get()).getPath(), mcLoc("block/wall_inventory"))
                .texture("wall",  new ResourceLocation(WildAside.MOD_ID, "block/" + ForgeRegistries.BLOCKS.getKey(baseBlock.get()).getPath()));
    }

    public void fenceItem(RegistryObject<Block> block, RegistryObject<Block> baseBlock) {
        this.withExistingParent(ForgeRegistries.BLOCKS.getKey(block.get()).getPath(), mcLoc("block/fence_inventory"))
                .texture("texture",  new ResourceLocation(WildAside.MOD_ID, "block/" + ForgeRegistries.BLOCKS.getKey(baseBlock.get()).getPath()));
    }

    public void buttonItem(RegistryObject<Block> block, RegistryObject<Block> baseBlock) {
        this.withExistingParent(ForgeRegistries.BLOCKS.getKey(block.get()).getPath(), mcLoc("block/button_inventory"))
                .texture("texture",  new ResourceLocation(WildAside.MOD_ID, "block/" + ForgeRegistries.BLOCKS.getKey(baseBlock.get()).getPath()));
    }

    public void trapdoorItem(RegistryObject<Block> block) {
        this.withExistingParent(ForgeRegistries.BLOCKS.getKey(block.get()).getPath(),
                modLoc("block/" + ForgeRegistries.BLOCKS.getKey(block.get()).getPath() + "_bottom"));
    }

    private void dnaHolder(Item item) {
        String baseName = ForgeRegistries.ITEMS.getKey(item).getPath();

        for (int i = 0; i <= DnaHolderItem.DEFAULT_MAX_SAMPLES; i++) {
            getBuilder(baseName + "_stage" + i)
                    .parent(getExistingFile(mcLoc("item/generated")))
                    .texture("layer0", WildAside.MOD_ID + ":item/" + baseName + "_fill1_" + i)
                    .texture("layer1", WildAside.MOD_ID + ":item/" + baseName + "_fill2_" + i)
                    .texture("layer2", WildAside.MOD_ID + ":item/" + baseName + "_base");
        }

        var builder = getBuilder(baseName)
                .parent(getExistingFile(mcLoc("item/generated")))
                .texture("layer0", WildAside.MOD_ID + ":item/" + baseName + "_base");

        for (int i = 0; i <= DnaHolderItem.DEFAULT_MAX_SAMPLES; i++) {
            float progress = i / (float) DnaHolderItem.DEFAULT_MAX_SAMPLES;
            builder.override()
                    .predicate(new ResourceLocation(WildAside.MOD_ID, DnaConstants.SAMPLE_PROGRESS), progress)
                    .model(getExistingFile(modLoc("item/" + baseName + "_stage" + i)))
                    .end();
        }
    }

    private void syringe(Item item) {
        String baseName = ForgeRegistries.ITEMS.getKey(item).getPath();

        int max = SyringeItem.DEFAULT_MAX_LOAD;

        for (int needle = 0; needle < max; needle++) {
            for (int fluid = 0; fluid < max; fluid++) {

                getBuilder(baseName + "_needle" + needle + "_fluid" + fluid)
                        .parent(getExistingFile(mcLoc("item/generated")))
                        .texture("layer0", WildAside.MOD_ID + ":item/" + baseName + "_fill_" + fluid)
                        .texture("layer1", WildAside.MOD_ID + ":item/" + baseName + "_needle_" + needle)
                        .texture("layer2", WildAside.MOD_ID + ":item/" + baseName + "_tip")
                        .texture("layer3", WildAside.MOD_ID + ":item/" + baseName + "_base");
            }
        }

        var builder = getBuilder(baseName)
                .parent(getExistingFile(mcLoc("item/generated")))
                .texture("layer0", WildAside.MOD_ID + ":item/" + baseName + "_base");

        for (int needle = 0; needle < max; needle++) {
            float needleVal = needle / (float) max;

            for (int fluid = 0; fluid < max; fluid++) {
                float fluidVal = fluid / (float) max;

                builder.override()
                       .predicate(new ResourceLocation(WildAside.MOD_ID, SYRINGE_PROGRESS), needleVal)
                       .predicate(new ResourceLocation(WildAside.MOD_ID, FLUID_LEVEL), fluidVal)
                       .model(getExistingFile(modLoc("item/" + baseName + "_needle" + needle + "_fluid" + fluid)))
                       .end();
            }
        }
    }
}