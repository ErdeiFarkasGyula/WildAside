package net.farkas.wildaside.datagen;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.block.ModBlocks;
import net.farkas.wildaside.block.custom.FallenHickoryLeavesBlock;
import net.farkas.wildaside.util.HickoryColour;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.*;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.Locale;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, WildAside.MOD_ID, exFileHelper);
    }

    private final HickoryColour[] colours = HickoryColour.values();
    private final Block fallenLeaves = ModBlocks.FALLEN_HICKORY_LEAVES.get();

    @Override
    protected void registerStatesAndModels() {
        //VIBRION
        blockWithItem(ModBlocks.VIBRION_BLOCK);
        blockWithItem(ModBlocks.COMPRESSED_VIBRION_BLOCK);
        translucentBlockWithItem(ModBlocks.VIBRION_GEL);
        simpleBlockWithItem(ModBlocks.LIT_VIBRION_GEL.get(), translucentAll(ModBlocks.VIBRION_GEL.get()));

        translucentBlockWithItem(ModBlocks.VIBRION_GLASS);
        simpleBlockWithItem(ModBlocks.LIT_VIBRION_GLASS.get(), translucentAll(ModBlocks.VIBRION_GLASS.get()));
        paneBlockWithRenderType(((IronBarsBlock)ModBlocks.VIBRION_GLASS_PANE.get()), modLoc("block/vibrion_glass"), modLoc("block/vibrion_glass"), "translucent");
        paneBlockWithRenderType(((IronBarsBlock)ModBlocks.LIT_VIBRION_GLASS_PANE.get()), modLoc("block/vibrion_glass"), modLoc("block/vibrion_glass"), "translucent");

        simpleBlockWithItem(ModBlocks.VIBRION_GROWTH.get(), models().cross(blockTexture(ModBlocks.VIBRION_GROWTH.get()).getPath(), blockTexture(ModBlocks.VIBRION_GROWTH.get())).renderType("cutout"));
        simpleBlockWithItem(ModBlocks.POTTED_VIBRION_GROWTH.get(), models().singleTexture("potted_vibrion_growth", new ResourceLocation("flower_pot_cross"), "plant", blockTexture(ModBlocks.VIBRION_GROWTH.get())).renderType("cutout"));
        simpleBlockWithItem(ModBlocks.VIBRION_SPOREHOLDER.get(),  new ModelFile.UncheckedModelFile(modLoc("custom/vibrion_sporeholder")));
        simpleBlockWithItem(ModBlocks.HANGING_VIBRION_VINES.get(), models().cross(blockTexture(ModBlocks.HANGING_VIBRION_VINES.get()).getPath(), blockTexture(ModBlocks.HANGING_VIBRION_VINES.get())).renderType("cutout"));
        simpleBlockWithItem(ModBlocks.HANGING_VIBRION_VINES_PLANT.get(), models().cross(blockTexture(ModBlocks.HANGING_VIBRION_VINES_PLANT.get()).getPath(), blockTexture(ModBlocks.HANGING_VIBRION_VINES_PLANT.get())).renderType("cutout"));

        directionalBlock(ModBlocks.SPORE_BLASTER.get(),  new ModelFile.UncheckedModelFile(modLoc("custom/spore_blaster")));
        simpleBlockItem(ModBlocks.SPORE_BLASTER.get(), new ModelFile.UncheckedModelFile(modLoc("custom/spore_blaster")));
        directionalBlock(ModBlocks.POTION_BLASTER.get(),  new ModelFile.UncheckedModelFile(modLoc("custom/potion_blaster")));
        simpleBlockItem(ModBlocks.POTION_BLASTER.get(), new ModelFile.UncheckedModelFile(modLoc("custom/potion_blaster")));
        blockWithItem(ModBlocks.SPORE_AIR);
        axisBlock(((RotatedPillarBlock)ModBlocks.NATURAL_SPORE_BLASTER.get()), modLoc("block/substilium_soil"), modLoc("block/natural_spore_blaster"));
        simpleBlockItem(ModBlocks.NATURAL_SPORE_BLASTER.get(), new ModelFile.UncheckedModelFile(modLoc("custom/natural_spore_blaster")));

        //SUBSTILIUM
        blockWithItem(ModBlocks.SUBSTILIUM_SOIL);

        blockWithItem(ModBlocks.COMPRESSED_SUBSTILIUM_SOIL);
        blockWithItem(ModBlocks.SMOOTH_SUBSTILIUM_SOIL);
        blockWithItem(ModBlocks.CHISELED_SUBSTILIUM_SOIL);
        blockWithItem(ModBlocks.SUBSTILIUM_TILES);
        blockWithItem(ModBlocks.CRACKED_SUBSTILIUM_TILES);
        wallBlock((WallBlock) ModBlocks.SUBSTILIUM_TILE_WALLS.get(), modLoc("block/substilium_tiles"));
        stairsBlock(((StairBlock)ModBlocks.SUBSTILIUM_TILE_STAIRS.get()), blockTexture(ModBlocks.SUBSTILIUM_TILES.get()));
        slabBlock(((SlabBlock)ModBlocks.SUBSTILIUM_TILE_SLAB.get()), blockTexture(ModBlocks.SUBSTILIUM_TILES.get()), blockTexture(ModBlocks.SUBSTILIUM_TILES.get()));
        buttonBlock(((ButtonBlock)ModBlocks.SUBSTILIUM_TILE_BUTTON.get()), blockTexture(ModBlocks.SUBSTILIUM_TILES.get()));
        pressurePlateBlock(((PressurePlateBlock)ModBlocks.SUBSTILIUM_TILE_PRESSURE_PLATE.get()), blockTexture(ModBlocks.SUBSTILIUM_TILES.get()));

        blockWithItem(ModBlocks.SUBSTILIUM_COAL_ORE);
        blockWithItem(ModBlocks.SUBSTILIUM_COPPER_ORE);
        blockWithItem(ModBlocks.SUBSTILIUM_LAPIS_ORE);
        blockWithItem(ModBlocks.SUBSTILIUM_IRON_ORE);
        blockWithItem(ModBlocks.SUBSTILIUM_GOLD_ORE);
        blockWithItem(ModBlocks.SUBSTILIUM_REDSTONE_ORE);
        blockWithItem(ModBlocks.SUBSTILIUM_DIAMOND_ORE);
        blockWithItem(ModBlocks.SUBSTILIUM_EMERALD_ORE);

        axisBlock(((RotatedPillarBlock) ModBlocks.SUBSTILIUM_STEM.get()), modLoc("block/substilium_stem_side"), modLoc("block/substilium_stem_top"));
        axisBlock(((RotatedPillarBlock) ModBlocks.STRIPPED_SUBSTILIUM_STEM.get()), modLoc("block/stripped_substilium_stem_side"), modLoc("block/stripped_substilium_stem_top"));
        axisBlock(((RotatedPillarBlock) ModBlocks.SUBSTILIUM_WOOD.get()), modLoc("block/substilium_stem_side"), modLoc("block/substilium_stem_side"));
        axisBlock(((RotatedPillarBlock) ModBlocks.STRIPPED_SUBSTILIUM_WOOD.get()), modLoc("block/stripped_substilium_stem_side"), modLoc("block/stripped_substilium_stem_side"));
        blockWithItem(ModBlocks.SUBSTILIUM_PLANKS);
        stairsBlock(((StairBlock)ModBlocks.SUBSTILIUM_STAIRS.get()), blockTexture(ModBlocks.SUBSTILIUM_PLANKS.get()));
        slabBlock(((SlabBlock)ModBlocks.SUBSTILIUM_SLAB.get()), blockTexture(ModBlocks.SUBSTILIUM_PLANKS.get()), blockTexture(ModBlocks.SUBSTILIUM_PLANKS.get()));
        buttonBlock(((ButtonBlock)ModBlocks.SUBSTILIUM_BUTTON.get()), blockTexture(ModBlocks.SUBSTILIUM_PLANKS.get()));
        pressurePlateBlock(((PressurePlateBlock)ModBlocks.SUBSTILIUM_PRESSURE_PLATE.get()), blockTexture(ModBlocks.SUBSTILIUM_PLANKS.get()));
        fenceBlock(((FenceBlock)ModBlocks.SUBSTILIUM_FENCE.get()), blockTexture(ModBlocks.SUBSTILIUM_PLANKS.get()));
        fenceGateBlock(((FenceGateBlock)ModBlocks.SUBSTILIUM_FENCE_GATE.get()), blockTexture(ModBlocks.SUBSTILIUM_PLANKS.get()));
        doorBlockWithRenderType(((DoorBlock)ModBlocks.SUBSTILIUM_DOOR.get()), modLoc("block/substilium_door_bottom"), modLoc("block/substilium_door_top"), "cutout");
        trapdoorBlockWithRenderType(((TrapDoorBlock)ModBlocks.SUBSTILIUM_TRAPDOOR.get()), modLoc("block/substilium_trapdoor"), true, "cutout");
        signBlock(((StandingSignBlock) ModBlocks.SUBSTILIUM_SIGN.get()), ((WallSignBlock) ModBlocks.SUBSTILIUM_WALL_SIGN.get()), blockTexture(ModBlocks.SUBSTILIUM_PLANKS.get()));
        hangingSignBlock((ModBlocks.SUBSTILIUM_HANGING_SIGN.get()), (ModBlocks.SUBSTILIUM_WALL_HANGING_SIGN.get()), blockTexture(ModBlocks.SUBSTILIUM_PLANKS.get()));

        simpleBlockWithItem(ModBlocks.SUBSTILIUM_SPROUTS.get(), models().cross(blockTexture(ModBlocks.SUBSTILIUM_SPROUTS.get()).getPath(), blockTexture(ModBlocks.SUBSTILIUM_SPROUTS.get())).renderType("cutout"));

        simpleBlock(ModBlocks.BIOENGINEERING_WORKSTATION.get(), new ModelFile.UncheckedModelFile(modLoc("custom/bioengineering_workstation")));
        simpleBlockItem(ModBlocks.BIOENGINEERING_WORKSTATION.get(), new ModelFile.UncheckedModelFile(modLoc("custom/bioengineering_workstation")));

        //ENTORIUM
        blockWithItem(ModBlocks.ENTORIUM_SHROOM);
        blockWithItem(ModBlocks.ENTORIUM_ORE);
        blockWithItem(ModBlocks.OVERGROWN_ENTORIUM_ORE);

        //HICKORY
        axisBlock(((RotatedPillarBlock) ModBlocks.HICKORY_LOG.get()), modLoc("block/hickory_log_side"), modLoc("block/hickory_log_top"));
        axisBlock(((RotatedPillarBlock) ModBlocks.STRIPPED_HICKORY_LOG.get()), modLoc("block/stripped_hickory_log_side"), modLoc("block/stripped_hickory_log_top"));
        axisBlock(((RotatedPillarBlock) ModBlocks.HICKORY_WOOD.get()), modLoc("block/hickory_log_side"), modLoc("block/hickory_log_side"));
        axisBlock(((RotatedPillarBlock) ModBlocks.STRIPPED_HICKORY_WOOD.get()), modLoc("block/stripped_hickory_log_side"), modLoc("block/stripped_hickory_log_side"));
        blockWithItem(ModBlocks.HICKORY_PLANKS);
        stairsBlock(((StairBlock)ModBlocks.HICKORY_STAIRS.get()), blockTexture(ModBlocks.HICKORY_PLANKS.get()));
        slabBlock(((SlabBlock)ModBlocks.HICKORY_SLAB.get()), blockTexture(ModBlocks.HICKORY_PLANKS.get()), blockTexture(ModBlocks.HICKORY_PLANKS.get()));
        buttonBlock(((ButtonBlock)ModBlocks.HICKORY_BUTTON.get()), blockTexture(ModBlocks.HICKORY_PLANKS.get()));
        pressurePlateBlock(((PressurePlateBlock)ModBlocks.HICKORY_PRESSURE_PLATE.get()), blockTexture(ModBlocks.HICKORY_PLANKS.get()));
        fenceBlock(((FenceBlock)ModBlocks.HICKORY_FENCE.get()), blockTexture(ModBlocks.HICKORY_PLANKS.get()));
        fenceGateBlock(((FenceGateBlock)ModBlocks.HICKORY_FENCE_GATE.get()), blockTexture(ModBlocks.HICKORY_PLANKS.get()));
        doorBlockWithRenderType(((DoorBlock)ModBlocks.HICKORY_DOOR.get()), modLoc("block/hickory_door_bottom"), modLoc("block/hickory_door_top"), "cutout");
        trapdoorBlockWithRenderType(((TrapDoorBlock)ModBlocks.HICKORY_TRAPDOOR.get()), modLoc("block/hickory_trapdoor"), true, "cutout");
        signBlock(((StandingSignBlock) ModBlocks.HICKORY_SIGN.get()), ((WallSignBlock) ModBlocks.HICKORY_WALL_SIGN.get()), blockTexture(ModBlocks.HICKORY_PLANKS.get()));
        hangingSignBlock((ModBlocks.HICKORY_HANGING_SIGN.get()), (ModBlocks.HICKORY_WALL_HANGING_SIGN.get()), blockTexture(ModBlocks.HICKORY_PLANKS.get()));

        leavesBlock(ModBlocks.HICKORY_LEAVES);
        leavesBlock(ModBlocks.RED_GLOWING_HICKORY_LEAVES);
        leavesBlock(ModBlocks.BROWN_GLOWING_HICKORY_LEAVES);
        leavesBlock(ModBlocks.YELLOW_GLOWING_HICKORY_LEAVES);
        leavesBlock(ModBlocks.GREEN_GLOWING_HICKORY_LEAVES);

        ResourceLocation parentModel = modLoc("custom/flat_block");


        ModelFile[][] models = new ModelFile[colours.length][3];
        for (int ci = 0; ci < colours.length; ci++) {
            String colName = colours[ci].getSerializedName();
            for (int count = 1; count <= 3; count++) {
                String modelName = String.format("fallen_%s_leaves_%d", colName, count);
                ResourceLocation tex = modLoc("block/" + modelName);
                models[ci][count-1] = models().withExistingParent(modelName, parentModel).texture("leaves", tex);
            }
        }

        getVariantBuilder(fallenLeaves).forAllStatesExcept(
                state -> {
                    int ci    = state.getValue(FallenHickoryLeavesBlock.COLOUR).ordinal();
                    int cnt   = Mth.clamp(state.getValue(FallenHickoryLeavesBlock.COUNT), 1, 3);
                    Direction face = state.getValue(FallenHickoryLeavesBlock.FACING);
                    int yRot = (int)face.toYRot();

                    ModelFile chosen = models[ci][cnt - 1];

                    ConfiguredModel[] model = ConfiguredModel.builder()
                            .modelFile(chosen)
                            .rotationY(yRot)
                            .build();

                    return model;
                },

                FallenHickoryLeavesBlock.LIGHT,
                FallenHickoryLeavesBlock.FIXED_LIGHTING
        );

        crossBlock(ModBlocks.HICKORY_SAPLING);
        crossBlock(ModBlocks.RED_GLOWING_HICKORY_SAPLING);
        crossBlock(ModBlocks.BROWN_GLOWING_HICKORY_SAPLING);
        crossBlock(ModBlocks.YELLOW_GLOWING_HICKORY_SAPLING);
        crossBlock(ModBlocks.GREEN_GLOWING_HICKORY_SAPLING);

        crossBlock(ModBlocks.SPOTTED_WINTERGREEN);
        crossBlock(ModBlocks.PINKSTER_FLOWER);

    }

    private void crossBlock(RegistryObject<Block> blockRegistryObject) {
        simpleBlock(blockRegistryObject.get(),
                models().cross(ForgeRegistries.BLOCKS.getKey(blockRegistryObject.get()).getPath(), blockTexture(blockRegistryObject.get())).renderType("cutout"));
    }

    public void hangingSignBlock(Block signBlock, Block wallSignBlock, ResourceLocation texture) {
        ModelFile sign = models().sign(name(signBlock), texture);
        hangingSignBlock(signBlock, wallSignBlock, sign);
    }

    public void hangingSignBlock(Block signBlock, Block wallSignBlock, ModelFile sign) {
        simpleBlock(signBlock, sign);
        simpleBlock(wallSignBlock, sign);
    }

    private String name(Block block) {
        return key(block).getPath();
    }

    private ResourceLocation key(Block block) {
        return ForgeRegistries.BLOCKS.getKey(block);
    }

    private void blockWithItem(RegistryObject<Block> blockRegistryObject) {
        simpleBlockWithItem(blockRegistryObject.get(), cubeAll(blockRegistryObject.get()));
    }

    private void translucentBlockWithItem(RegistryObject<Block> blockRegistryObject) {
        simpleBlockWithItem(blockRegistryObject.get(), translucentAll(blockRegistryObject.get()));
    }

    public ModelFile translucentAll(Block block) {
        return this.models().cubeAll(this.name(block), this.blockTexture(block)).renderType("translucent");
    }

    private void leavesBlock(RegistryObject<Block> blockRegistryObject) {
        simpleBlockWithItem(blockRegistryObject.get(),
                models().singleTexture(ForgeRegistries.BLOCKS.getKey(blockRegistryObject.get()).getPath(), new ResourceLocation("minecraft:block/leaves"),
                        "all", blockTexture(blockRegistryObject.get())));
    }


    private void makeFallenLeaves(Block block, String col, ResourceLocation parent) {
        String base = "fallen_" + col + "_hickory_leaves";
        ModelFile[] variants = new ModelFile[3];
        for (int i = 0; i < 3; i++) {
            String name = base + "_" + (i+1);
            ResourceLocation tex = modLoc("block/" + name);
            variants[i] = models().withExistingParent(name, parent).texture("leaves", tex);
        }

        getVariantBuilder(block).forAllStates(state -> {
            int count = Mth.clamp(state.getValue(FallenHickoryLeavesBlock.COUNT), 1, 3);
            Direction facing = state.getValue(FallenHickoryLeavesBlock.FACING);
            return ConfiguredModel.builder()
                    .modelFile(variants[count-1])
                    .rotationY((int) facing.toYRot())
                    .build();
        });
    }
}
