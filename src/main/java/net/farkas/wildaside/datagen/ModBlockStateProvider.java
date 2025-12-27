package net.farkas.wildaside.datagen;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.block.ModBlocks;
import net.farkas.wildaside.block.custom.FallenHickoryLeavesBlock;
import net.farkas.wildaside.block.custom.IncubatorBlock;
import net.farkas.wildaside.block.custom.RootBushBlock;
import net.farkas.wildaside.util.HickoryColour;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, WildAside.MOD_ID, exFileHelper);
    }

    private final HickoryColour[] colours = HickoryColour.values();
    private final Block fallenLeaves = ModBlocks.FALLEN_HICKORY_LEAVES.get();

    @Override
    protected void registerStatesAndModels() {
        //BLOCK WITH ITEM
        blockWithItem(ModBlocks.VIBRION_BLOCK);
        blockWithItem(ModBlocks.COMPRESSED_VIBRION_BLOCK);
        blockWithItem(ModBlocks.SPORE_AIR);

        blockWithItem(ModBlocks.SUBSTILIUM_SOIL);
        blockWithItem(ModBlocks.COMPRESSED_SUBSTILIUM_SOIL);
        blockWithItem(ModBlocks.SMOOTH_SUBSTILIUM_SOIL);
        blockWithItem(ModBlocks.SUBSTILIUM_TILES);
        blockWithItem(ModBlocks.CRACKED_SUBSTILIUM_TILES);
        blockWithItem(ModBlocks.CHISELED_SUBSTILIUM_SOIL);
        blockWithItem(ModBlocks.ENTORIUM_ORE);
        blockWithItem(ModBlocks.OVERGROWN_ENTORIUM_ORE);
        blockWithItem(ModBlocks.ENTORIUM_SHROOM);

        blockWithItem(ModBlocks.BIOFREEZER);

        blockWithItem(ModBlocks.SUBSTILIUM_COAL_ORE);
        blockWithItem(ModBlocks.SUBSTILIUM_COPPER_ORE);
        blockWithItem(ModBlocks.SUBSTILIUM_LAPIS_ORE);
        blockWithItem(ModBlocks.SUBSTILIUM_IRON_ORE);
        blockWithItem(ModBlocks.SUBSTILIUM_GOLD_ORE);
        blockWithItem(ModBlocks.SUBSTILIUM_REDSTONE_ORE);
        blockWithItem(ModBlocks.SUBSTILIUM_DIAMOND_ORE);
        blockWithItem(ModBlocks.SUBSTILIUM_EMERALD_ORE);

        blockWithItem(ModBlocks.SUBSTILIUM_PLANKS);

        blockWithItem(ModBlocks.HICKORY_PLANKS);

        //TRANSLUCENT
        translucentBlockWithItem(ModBlocks.VIBRION_GEL);
        translucentBlockWithItem(ModBlocks.LIT_VIBRION_GEL);
        translucentBlockWithItem(ModBlocks.VIBRION_GLASS);
        simpleBlockWithItem(ModBlocks.LIT_VIBRION_GLASS.get(), translucentAll(ModBlocks.VIBRION_GLASS.get()));

        //PANE
        paneBlockWithRenderType(((IronBarsBlock) ModBlocks.VIBRION_GLASS_PANE.get()), modLoc("block/vibrion_glass"), modLoc("block/vibrion_glass"), "translucent");
        paneBlockWithRenderType(((IronBarsBlock) ModBlocks.LIT_VIBRION_GLASS_PANE.get()), modLoc("block/vibrion_glass"), modLoc("block/vibrion_glass"), "translucent");

        //CROSS
        crossBlock(ModBlocks.VIBRION_GROWTH);
        crossBlock(ModBlocks.HANGING_VIBRION_VINES);
        crossBlock(ModBlocks.HANGING_VIBRION_VINES_PLANT);
        crossBlock(ModBlocks.SUBSTILIUM_SPROUTS);
        crossBlock(ModBlocks.HICKORY_SAPLING);
        crossBlock(ModBlocks.RED_GLOWING_HICKORY_SAPLING);
        crossBlock(ModBlocks.BROWN_GLOWING_HICKORY_SAPLING);
        crossBlock(ModBlocks.YELLOW_GLOWING_HICKORY_SAPLING);
        crossBlock(ModBlocks.GREEN_GLOWING_HICKORY_SAPLING);
        crossBlock(ModBlocks.SPOTTED_WINTERGREEN);
        crossBlock(ModBlocks.PINKSTER_FLOWER);

        //POTTED
        pottedBlock(ModBlocks.POTTED_VIBRION_GROWTH);
        pottedBlock(ModBlocks.POTTED_SPOTTED_WINTERGREEN);
        pottedBlock(ModBlocks.POTTED_PINKSTER_FLOWER);

        //DIRECTIONAL
        axisBlock(((RotatedPillarBlock) ModBlocks.NATURAL_SPORE_BLASTER.get()), modLoc("block/substilium_soil"), modLoc("block/natural_spore_blaster"));
        simpleBlockItem(ModBlocks.NATURAL_SPORE_BLASTER.get(), new ModelFile.UncheckedModelFile(modLoc("custom/natural_spore_blaster")));

        directionalBlock(ModBlocks.SPORE_BLASTER.get(), new ModelFile.UncheckedModelFile(modLoc("custom/spore_blaster")));
        simpleBlockItem(ModBlocks.SPORE_BLASTER.get(), new ModelFile.UncheckedModelFile(modLoc("custom/spore_blaster")));

        directionalBlock(ModBlocks.POTION_BLASTER.get(), new ModelFile.UncheckedModelFile(modLoc("custom/potion_blaster")));
        simpleBlockItem(ModBlocks.POTION_BLASTER.get(), new ModelFile.UncheckedModelFile(modLoc("custom/potion_blaster")));

        directionalBlock(ModBlocks.WIND_BLASTER.get(), new ModelFile.UncheckedModelFile(modLoc("custom/wind_blaster")));
        simpleBlockItem(ModBlocks.WIND_BLASTER.get(), new ModelFile.UncheckedModelFile(modLoc("custom/wind_blaster")));

        axisBlock(((RotatedPillarBlock) ModBlocks.SUBSTILIUM_STEM.get()), modLoc("block/substilium_stem_side"), modLoc("block/substilium_stem_top"));
        axisBlock(((RotatedPillarBlock) ModBlocks.STRIPPED_SUBSTILIUM_STEM.get()), modLoc("block/stripped_substilium_stem_side"), modLoc("block/stripped_substilium_stem_top"));
        axisBlock(((RotatedPillarBlock) ModBlocks.SUBSTILIUM_WOOD.get()), modLoc("block/substilium_stem_side"), modLoc("block/substilium_stem_side"));
        axisBlock(((RotatedPillarBlock) ModBlocks.STRIPPED_SUBSTILIUM_WOOD.get()), modLoc("block/stripped_substilium_stem_side"), modLoc("block/stripped_substilium_stem_side"));

        axisBlock(((RotatedPillarBlock) ModBlocks.HICKORY_LOG.get()), modLoc("block/hickory_log_side"), modLoc("block/hickory_log_top"));
        axisBlock(((RotatedPillarBlock) ModBlocks.STRIPPED_HICKORY_LOG.get()), modLoc("block/stripped_hickory_log_side"), modLoc("block/stripped_hickory_log_top"));
        axisBlock(((RotatedPillarBlock) ModBlocks.HICKORY_WOOD.get()), modLoc("block/hickory_log_side"), modLoc("block/hickory_log_side"));
        axisBlock(((RotatedPillarBlock) ModBlocks.STRIPPED_HICKORY_WOOD.get()), modLoc("block/stripped_hickory_log_side"), modLoc("block/stripped_hickory_log_side"));

        //STAIRS
        stairsBlock(((StairBlock) ModBlocks.SMOOTH_SUBSTILIUM_SOIL_STAIRS.get()), blockTexture(ModBlocks.SMOOTH_SUBSTILIUM_SOIL.get()));
        stairsBlock(((StairBlock) ModBlocks.SUBSTILIUM_TILE_STAIRS.get()), blockTexture(ModBlocks.SUBSTILIUM_TILES.get()));
        stairsBlock(((StairBlock) ModBlocks.SUBSTILIUM_STAIRS.get()), blockTexture(ModBlocks.SUBSTILIUM_PLANKS.get()));
        stairsBlock(((StairBlock) ModBlocks.HICKORY_STAIRS.get()), blockTexture(ModBlocks.HICKORY_PLANKS.get()));

        //SLAB
        slabBlock(((SlabBlock) ModBlocks.SMOOTH_SUBSTILIUM_SOIL_SLAB.get()), blockTexture(ModBlocks.SMOOTH_SUBSTILIUM_SOIL.get()), blockTexture(ModBlocks.SMOOTH_SUBSTILIUM_SOIL.get()));
        slabBlock(((SlabBlock) ModBlocks.SUBSTILIUM_TILE_SLAB.get()), blockTexture(ModBlocks.SUBSTILIUM_TILES.get()), blockTexture(ModBlocks.SUBSTILIUM_TILES.get()));
        slabBlock(((SlabBlock) ModBlocks.SUBSTILIUM_SLAB.get()), blockTexture(ModBlocks.SUBSTILIUM_PLANKS.get()), blockTexture(ModBlocks.SUBSTILIUM_PLANKS.get()));
        slabBlock(((SlabBlock) ModBlocks.HICKORY_SLAB.get()), blockTexture(ModBlocks.HICKORY_PLANKS.get()), blockTexture(ModBlocks.HICKORY_PLANKS.get()));

        //WALLS
        wallBlock((WallBlock) ModBlocks.SMOOTH_SUBSTILIUM_SOIL_WALLS.get(), blockTexture(ModBlocks.SMOOTH_SUBSTILIUM_SOIL.get()));
        wallBlock((WallBlock) ModBlocks.SUBSTILIUM_TILE_WALLS.get(), blockTexture(ModBlocks.SUBSTILIUM_TILES.get()));

        //FENCE
        fenceBlock(((FenceBlock) ModBlocks.SUBSTILIUM_FENCE.get()), blockTexture(ModBlocks.SUBSTILIUM_PLANKS.get()));
        fenceBlock(((FenceBlock) ModBlocks.HICKORY_FENCE.get()), blockTexture(ModBlocks.HICKORY_PLANKS.get()));

        //FENCEGATE
        fenceGateBlock(((FenceGateBlock) ModBlocks.SUBSTILIUM_FENCE_GATE.get()), blockTexture(ModBlocks.SUBSTILIUM_PLANKS.get()));
        fenceGateBlock(((FenceGateBlock) ModBlocks.HICKORY_FENCE_GATE.get()), blockTexture(ModBlocks.HICKORY_PLANKS.get()));

        //PRESSUREPLATE
        pressurePlateBlock(((PressurePlateBlock) ModBlocks.SMOOTH_SUBSTILIUM_SOIL_PRESSURE_PLATE.get()), blockTexture(ModBlocks.SMOOTH_SUBSTILIUM_SOIL.get()));
        pressurePlateBlock(((PressurePlateBlock) ModBlocks.SUBSTILIUM_TILE_PRESSURE_PLATE.get()), blockTexture(ModBlocks.SUBSTILIUM_TILES.get()));
        pressurePlateBlock(((PressurePlateBlock) ModBlocks.SUBSTILIUM_PRESSURE_PLATE.get()), blockTexture(ModBlocks.SUBSTILIUM_PLANKS.get()));
        pressurePlateBlock(((PressurePlateBlock) ModBlocks.HICKORY_PRESSURE_PLATE.get()), blockTexture(ModBlocks.HICKORY_PLANKS.get()));

        //BUTTON
        buttonBlock(((ButtonBlock) ModBlocks.SMOOTH_SUBSTILIUM_SOIL_BUTTON.get()), blockTexture(ModBlocks.SMOOTH_SUBSTILIUM_SOIL.get()));
        buttonBlock(((ButtonBlock) ModBlocks.SUBSTILIUM_TILE_BUTTON.get()), blockTexture(ModBlocks.SUBSTILIUM_TILES.get()));
        buttonBlock(((ButtonBlock) ModBlocks.SUBSTILIUM_BUTTON.get()), blockTexture(ModBlocks.SUBSTILIUM_PLANKS.get()));
        buttonBlock(((ButtonBlock) ModBlocks.HICKORY_BUTTON.get()), blockTexture(ModBlocks.HICKORY_PLANKS.get()));

        //DOOR
        doorBlockWithRenderType(((DoorBlock) ModBlocks.SUBSTILIUM_DOOR.get()), modLoc("block/substilium_door_bottom"), modLoc("block/substilium_door_top"), "cutout");
        doorBlockWithRenderType(((DoorBlock) ModBlocks.HICKORY_DOOR.get()), modLoc("block/hickory_door_bottom"), modLoc("block/hickory_door_top"), "cutout");

        //TRAPDOOR
        trapdoorBlockWithRenderType(((TrapDoorBlock) ModBlocks.SUBSTILIUM_TRAPDOOR.get()), modLoc("block/substilium_trapdoor"), true, "cutout");
        trapdoorBlockWithRenderType(((TrapDoorBlock) ModBlocks.HICKORY_TRAPDOOR.get()), modLoc("block/hickory_trapdoor"), true, "cutout");

        //SIGN
        signBlock(((StandingSignBlock) ModBlocks.SUBSTILIUM_SIGN.get()), ((WallSignBlock) ModBlocks.SUBSTILIUM_WALL_SIGN.get()), blockTexture(ModBlocks.SUBSTILIUM_PLANKS.get()));
        hangingSignBlock((ModBlocks.SUBSTILIUM_HANGING_SIGN.get()), (ModBlocks.SUBSTILIUM_WALL_HANGING_SIGN.get()), blockTexture(ModBlocks.SUBSTILIUM_PLANKS.get()));
        signBlock(((StandingSignBlock) ModBlocks.HICKORY_SIGN.get()), ((WallSignBlock) ModBlocks.HICKORY_WALL_SIGN.get()), blockTexture(ModBlocks.HICKORY_PLANKS.get()));
        hangingSignBlock((ModBlocks.HICKORY_HANGING_SIGN.get()), (ModBlocks.HICKORY_WALL_HANGING_SIGN.get()), blockTexture(ModBlocks.HICKORY_PLANKS.get()));

        //LEAVES
        leavesBlock(ModBlocks.HICKORY_LEAVES);
        leavesBlock(ModBlocks.RED_GLOWING_HICKORY_LEAVES);
        leavesBlock(ModBlocks.BROWN_GLOWING_HICKORY_LEAVES);
        leavesBlock(ModBlocks.YELLOW_GLOWING_HICKORY_LEAVES);
        leavesBlock(ModBlocks.GREEN_GLOWING_HICKORY_LEAVES);

        //CUSTOM
        ResourceLocation parentModel = modLoc("custom/flat_block");
        ResourceLocation tintedModel = modLoc("custom/flat_tinted_block");

        simpleBlockWithItem(ModBlocks.VIBRION_SPOREHOLDER.get(), new ModelFile.UncheckedModelFile(modLoc("custom/vibrion_sporeholder")));
        simpleBlock(ModBlocks.BIOENGINEERING_WORKSTATION.get(), new ModelFile.UncheckedModelFile(modLoc("custom/bioengineering_workstation")));
        simpleBlockItem(ModBlocks.BIOENGINEERING_WORKSTATION.get(), new ModelFile.UncheckedModelFile(modLoc("custom/bioengineering_workstation")));

        ModelFile[][] models = new ModelFile[colours.length][3];
        for (int ci = 0; ci < colours.length; ci++) {
            String colName = colours[ci].getSerializedName();
            for (int count = 1; count <= 3; count++) {
                String modelName = String.format("fallen_%s_leaves_%d", colName, count);
                ResourceLocation tex = modLoc("block/" + modelName);
                ResourceLocation chosenModel = colName == "hickory" ? tintedModel : parentModel;
                models[ci][count - 1] = models().withExistingParent(modelName, chosenModel).texture("leaves", tex);
            }
        }

        getVariantBuilder(fallenLeaves).forAllStatesExcept(
                state -> {
                    int ci = state.getValue(FallenHickoryLeavesBlock.COLOUR).ordinal();
                    int cnt = Mth.clamp(state.getValue(FallenHickoryLeavesBlock.COUNT), 1, 3);
                    Direction face = state.getValue(FallenHickoryLeavesBlock.FACING);
                    int yRot = (int) face.toYRot();

                    ModelFile chosen = models[ci][cnt - 1];

                    return ConfiguredModel.builder()
                            .modelFile(chosen)
                            .rotationY(yRot)
                            .build();
                },

                FallenHickoryLeavesBlock.LIGHT,
                FallenHickoryLeavesBlock.FIXED_LIGHTING
        );

        getVariantBuilder(ModBlocks.HICKORY_ROOT_BUSH.get()).forAllStates(blockState -> {
            int age = blockState.getValue(RootBushBlock.AGE);
            String name = "block/hickory_root_bush_" + age;
            ModelFile model = models().cross(name, modLoc(name));
            return ConfiguredModel.builder().modelFile(model).build();
        });

        incubatorBlock();
    }

    private void crossBlock(RegistryObject<Block> blockRegistryObject) {
        simpleBlock(blockRegistryObject.get(),
                models().cross(ForgeRegistries.BLOCKS.getKey(blockRegistryObject.get()).getPath(), blockTexture(blockRegistryObject.get())).renderType("cutout"));
    }

    private void pottedBlock(RegistryObject<Block> block) {
        simpleBlockWithItem(block.get(), models().singleTexture(ForgeRegistries.BLOCKS.getKey(block.get()).getPath(), new ResourceLocation("flower_pot_cross"), "plant", blockTexture(block.get())).renderType("cutout"));
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

    private void incubatorBlock() {
        var incubatorLower = models().withExistingParent("incubator_lower", mcLoc("block/block"))
                .texture("down", modLoc("block/incubator_lower_side"))
                .texture("up", modLoc("block/incubator_lower_top"))
                .texture("north", modLoc("block/incubator_lower_front"))
                .texture("south", modLoc("block/incubator_lower_side"))
                .texture("west", modLoc("block/incubator_lower_side"))
                .texture("east", modLoc("block/incubator_lower_side"))
                .texture("particle", modLoc("block/incubator_lower_side"))
                .renderType("opaque");

        incubatorLower.element()
                .from(0, 0, 0).to(16, 16, 16)
                .face(Direction.DOWN).texture("#down").end()
                .face(Direction.UP).texture("#up").cullface(null).end()
                .face(Direction.NORTH).texture("#north").end()
                .face(Direction.SOUTH).texture("#south").end()
                .face(Direction.WEST).texture("#west").end()
                .face(Direction.EAST).texture("#east").end();

        var incubatorUpper = models().withExistingParent("incubator_upper", mcLoc("block/block"))
                .texture("down", modLoc("block/incubator_lower_top"))
                .texture("up", modLoc("block/incubator_upper_top"))
                .texture("north", modLoc("block/incubator_upper_front"))
                .texture("south", modLoc("block/incubator_upper_side"))
                .texture("west", modLoc("block/incubator_upper_side"))
                .texture("east", modLoc("block/incubator_upper_side"))
                .texture("particle", modLoc("block/incubator_upper_side"))
                .renderType("cutout");

        incubatorUpper.element()
                .from(0, 0, 0).to(16, 16, 16)
                .face(Direction.DOWN).texture("#down").cullface(null).end()
                .face(Direction.UP).texture("#up").end()
                .face(Direction.NORTH).texture("#north").end()  // window
                .face(Direction.SOUTH).texture("#south").end()
                .face(Direction.WEST).texture("#west").end()
                .face(Direction.EAST).texture("#east").end();

        incubatorUpper.element()
                .from(0, 0, 15.99f).to(16, 16, 16)
                .face(Direction.NORTH).texture("#south").cullface(null).end();
        incubatorUpper.element()
                .from(0, 0, 0).to(0.01f, 16, 16)
                .face(Direction.EAST).texture("#west").cullface(null).end();
        incubatorUpper.element()
                .from(15.99f, 0, 0).to(16, 16, 16)
                .face(Direction.WEST).texture("#east").cullface(null).end();
        incubatorUpper.element()
                .from(0, 15.99f, 0).to(16, 16, 16)
                .face(Direction.DOWN).texture("#up").cullface(null).end();

        var incubatorUpperOpen = models().withExistingParent("incubator_upper_open", mcLoc("block/block"))
                .texture("down", modLoc("block/incubator_lower_top"))
                .texture("up", modLoc("block/incubator_upper_top"))
                .texture("north", modLoc("block/incubator_upper_front_open"))
                .texture("south", modLoc("block/incubator_upper_side"))
                .texture("west", modLoc("block/incubator_upper_side"))
                .texture("east", modLoc("block/incubator_upper_side"))
                .texture("particle", modLoc("block/incubator_upper_side"))
                .renderType("cutout");

        incubatorUpperOpen.element()
                .from(0, 0, 0).to(16, 16, 16)
                .face(Direction.DOWN).texture("#down").cullface(null).end()
                .face(Direction.UP).texture("#up").end()
                .face(Direction.NORTH).texture("#north").end()
                .face(Direction.SOUTH).texture("#south").end()
                .face(Direction.WEST).texture("#west").end()
                .face(Direction.EAST).texture("#east").end();

        incubatorUpperOpen.element()
                .from(0, 0, 15.99f).to(16, 16, 16)
                .face(Direction.NORTH).texture("#south").cullface(null).end();
        incubatorUpperOpen.element()
                .from(0, 0, 0).to(0.01f, 16, 16)
                .face(Direction.EAST).texture("#west").cullface(null).end();
        incubatorUpperOpen.element()
                .from(15.99f, 0, 0).to(16, 16, 16)
                .face(Direction.WEST).texture("#east").cullface(null).end();
        incubatorUpperOpen.element()
                .from(0, 15.99f, 0).to(16, 16, 16)
                .face(Direction.DOWN).texture("#up").cullface(null).end();

        getVariantBuilder(ModBlocks.INCUBATOR.get()).forAllStates(state -> {
            Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            DoubleBlockHalf half = state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF);
            boolean open = state.getValue(IncubatorBlock.OPEN);
            boolean lower = half == DoubleBlockHalf.LOWER;
            ModelFile model = lower
                    ? incubatorLower
                    : (open ? incubatorUpperOpen : incubatorUpper);
            return ConfiguredModel.builder()
                    .modelFile(model)
                    .rotationY(((int) facing.toYRot() + 180) % 360)
                    .build();
        });
    }
}
