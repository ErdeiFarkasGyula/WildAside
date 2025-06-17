package net.farkas.wildaside.block;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.block.custom.*;
import net.farkas.wildaside.block.custom.sign.ModHangingSignBlock;
import net.farkas.wildaside.block.custom.sign.ModStandingSignBlock;
import net.farkas.wildaside.block.custom.sign.ModWallHangingSignBlock;
import net.farkas.wildaside.block.custom.sign.ModWallSignBlock;
import net.farkas.wildaside.block.custom.vibrion.*;
import net.farkas.wildaside.block.custom.vibrion.hanging_vines.HangingVibrionVines;
import net.farkas.wildaside.block.custom.vibrion.hanging_vines.HangingVibrionVinesPlant;
import net.farkas.wildaside.block.custom.vibrion.SporeBlaster;
import net.farkas.wildaside.block.custom.BioengineeringWorkstation;
import net.farkas.wildaside.item.ModItems;
import net.farkas.wildaside.particle.ModParticles;
import net.farkas.wildaside.util.ParticleUtils;
import net.farkas.wildaside.util.ModWoodTypes;
import net.farkas.wildaside.worldgen.feature.tree.hickory.*;
import net.farkas.wildaside.worldgen.feature.tree.substilium.SubstiliumMushroomGrower;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, WildAside.MOD_ID);

    //VIBRION
    public static final RegistryObject<Block> VIBRION_BLOCK = registerBlock("vibrion_block",
            () ->  new DropExperienceBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_YELLOW)
                    .sound(SoundType.SHROOMLIGHT)
                    .strength(2F,  1F)
                    .lightLevel(l -> 7),  UniformInt.of(1, 2)));

    public static final RegistryObject<Block> COMPRESSED_VIBRION_BLOCK = registerBlock("compressed_vibrion_block",
            () ->  new DropExperienceBlock(BlockBehaviour.Properties.copy(VIBRION_BLOCK.get()).lightLevel(s -> 15), UniformInt.of(4, 8)));

    public static final RegistryObject<Block> VIBRION_GEL = registerBlock("vibrion_gel",
            () ->  new VibrionGel(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_YELLOW)
                    .sound(SoundType.HONEY_BLOCK)
                    .strength(0.1F,  0F)
                    .noCollission()
                    .noOcclusion()
                    .isRedstoneConductor((bs, br, bp) -> false)
                    .speedFactor(0.2f)
                    .jumpFactor(0.6f)
                    .noOcclusion()));

    public static final RegistryObject<Block> LIT_VIBRION_GEL = registerBlock("lit_vibrion_gel",
            () ->  new VibrionGel(BlockBehaviour.Properties.copy(VIBRION_GEL.get()).lightLevel(s -> 7)));

    public static final RegistryObject<Block> VIBRION_GLASS = registerBlock("vibrion_glass",
            () ->  new VibrionGlass(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_YELLOW)
                    .sound(SoundType.GLASS)
                    .strength(0.4F,  0.3F)
                    .noOcclusion()
                    .isRedstoneConductor((bs, br, bp) -> false)
                    .instrument(NoteBlockInstrument.HAT)));

    public static final RegistryObject<Block> LIT_VIBRION_GLASS = registerBlock("lit_vibrion_glass",
            () ->  new VibrionGlass(BlockBehaviour.Properties.copy(VIBRION_GLASS.get()).lightLevel(s -> 7)));

    public static final RegistryObject<Block> VIBRION_GLASS_PANE = registerBlock("vibrion_glass_pane",
            () ->  new IronBarsBlock(BlockBehaviour.Properties.copy(VIBRION_GLASS.get())));

    public static final RegistryObject<Block> LIT_VIBRION_GLASS_PANE = registerBlock("lit_vibrion_glass_pane",
            () ->  new IronBarsBlock(BlockBehaviour.Properties.copy(VIBRION_GLASS_PANE.get()).lightLevel(s -> 7)));

    public static final RegistryObject<Block> VIBRION_GROWTH = registerBlock("vibrion_growth",
            () ->  new FlowerBlock(() -> MobEffects.POISON, 5, BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_YELLOW)
                    .lightLevel(l -> 3)
                    .sound(SoundType.ROOTS)
                    .noCollission()
                    .noOcclusion()
                    .replaceable()
                    .instabreak()
                    .offsetType(BlockBehaviour.OffsetType.XZ)
                    .pushReaction(PushReaction.DESTROY)));

    public static final RegistryObject<Block> POTTED_VIBRION_GROWTH = BLOCKS.register("potted_vibrion_growth",
            () ->  new FlowerPotBlock(() -> (FlowerPotBlock)Blocks.FLOWER_POT, ModBlocks.VIBRION_GROWTH, BlockBehaviour.Properties.copy(Blocks.POTTED_CRIMSON_ROOTS)
                    .mapColor(MapColor.COLOR_YELLOW)
                    .instabreak()
                    .pushReaction(PushReaction.DESTROY)));

    public static final RegistryObject<Block> VIBRION_SPOREHOLDER = registerBlock("vibrion_sporeholder",
            () ->  new Sporeholder(new SubstiliumMushroomGrower(), BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_YELLOW)
                    .sound(SoundType.FUNGUS)
                    .lightLevel(l -> 3)
                    .noCollission()
                    .noOcclusion()
                    .strength(1.5f,  2f)
                    .offsetType(BlockBehaviour.OffsetType.XZ)
                    .pushReaction(PushReaction.DESTROY)));

    public static final RegistryObject<Block> HANGING_VIBRION_VINES = registerBlock("hanging_vibrion_vines",
            () ->  new HangingVibrionVines(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_YELLOW)
                    .sound(SoundType.WEEPING_VINES)
                    .lightLevel(l -> 3)
                    .noCollission()
                    .noOcclusion()
                    .replaceable()
                    .isRedstoneConductor((bs, br, bp) -> false)
                    .strength(0.3f, 0.5f)
                    .pushReaction(PushReaction.DESTROY)));

    public static final RegistryObject<Block> HANGING_VIBRION_VINES_PLANT = registerBlock("hanging_vibrion_vines_plant",
            () ->  new HangingVibrionVinesPlant(BlockBehaviour.Properties.copy(ModBlocks.HANGING_VIBRION_VINES.get())));

    public static final RegistryObject<Block> SPORE_AIR = registerBlock("spore_air",
            () ->  new SporeAir(BlockBehaviour.Properties.copy(Blocks.AIR).mapColor(MapColor.COLOR_YELLOW).noLootTable()));

    //ENTORIUM
    public static final RegistryObject<Block> ENTORIUM_SHROOM = registerBlock("entorium_shroom",
            () ->  new HugeMushroomBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE)
                    .sound(SoundType.WART_BLOCK)
                    .strength(1.2F,  1.5F)
                    .requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> OVERGROWN_ENTORIUM_ORE = registerBlock("overgrown_entorium_ore",
            () ->  new OvergrownEntoriumOre(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_CYAN)
                    .sound(SoundType.NETHER_ORE)
                    .strength(3.5f,  12)));

    public static final RegistryObject<Block> ENTORIUM_ORE = registerBlock("entorium_ore",
            () ->  new EntoriumOre(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE)
                    .sound(SoundType.NETHER_ORE)
                    .strength(3,  12)));

    public static final RegistryObject<Block> SUBSTILIUM_COAL_ORE = registerBlock("substilium_coal_ore",
            () ->  new SubstiliumSoil(BlockBehaviour.Properties.copy(Blocks.COAL_ORE).sound(SoundType.ROOTED_DIRT), UniformInt.of(0, 3)));
    public static final RegistryObject<Block> SUBSTILIUM_COPPER_ORE = registerBlock("substilium_copper_ore",
            () ->  new SubstiliumSoil(BlockBehaviour.Properties.copy(Blocks.COPPER_ORE).sound(SoundType.ROOTED_DIRT), UniformInt.of(0, 1)));
    public static final RegistryObject<Block> SUBSTILIUM_LAPIS_ORE = registerBlock("substilium_lapis_ore",
            () ->  new SubstiliumSoil(BlockBehaviour.Properties.copy(Blocks.LAPIS_ORE).sound(SoundType.ROOTED_DIRT), UniformInt.of(2, 6)));
    public static final RegistryObject<Block> SUBSTILIUM_IRON_ORE = registerBlock("substilium_iron_ore",
            () ->  new SubstiliumSoil(BlockBehaviour.Properties.copy(Blocks.IRON_ORE).sound(SoundType.ROOTED_DIRT), UniformInt.of(0, 1)));
    public static final RegistryObject<Block> SUBSTILIUM_GOLD_ORE = registerBlock("substilium_gold_ore",
            () ->  new SubstiliumSoil(BlockBehaviour.Properties.copy(Blocks.GOLD_ORE).sound(SoundType.ROOTED_DIRT), UniformInt.of(0, 1)));
    public static final RegistryObject<Block> SUBSTILIUM_REDSTONE_ORE = registerBlock("substilium_redstone_ore",
            () ->  new SubstiliumRedstoneOre(BlockBehaviour.Properties.copy(Blocks.REDSTONE_ORE).sound(SoundType.ROOTED_DIRT), UniformInt.of(0, 1)));
    public static final RegistryObject<Block> SUBSTILIUM_DIAMOND_ORE = registerBlock("substilium_diamond_ore",
            () ->  new SubstiliumSoil(BlockBehaviour.Properties.copy(Blocks.DIAMOND_ORE).sound(SoundType.ROOTED_DIRT), UniformInt.of(3, 8)));
    public static final RegistryObject<Block> SUBSTILIUM_EMERALD_ORE = registerBlock("substilium_emerald_ore",
            () ->  new SubstiliumSoil(BlockBehaviour.Properties.copy(Blocks.EMERALD_ORE).sound(SoundType.ROOTED_DIRT), UniformInt.of(3, 8)));


    //SUBSTILIUM
    public static final RegistryObject<Block> SUBSTILIUM_SOIL = registerBlock("substilium_soil",
            () ->  new SubstiliumSoil(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_CYAN)
                    .sound(SoundType.ROOTED_DIRT)
                    .strength(1,  2), UniformInt.of(0, 1)));

    public static final RegistryObject<Block> COMPRESSED_SUBSTILIUM_SOIL = registerBlock("compressed_substilium_soil",
            () ->  new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_CYAN)
                    .sound(SoundType.DEEPSLATE)
                    .requiresCorrectToolForDrops()
                    .strength(2,  12)) {
                @Override
                public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, IPlantable plantable) {
                    return true;
                }
            });

    public static final RegistryObject<Block> SPORE_BLASTER = registerBlock("spore_blaster",
            () ->  new SporeBlaster(BlockBehaviour.Properties.copy(ModBlocks.COMPRESSED_SUBSTILIUM_SOIL.get())
                    .noOcclusion().strength(2f, 12f).requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> POTION_BLASTER = registerBlock("potion_blaster",
            () ->  new PotionBlaster(BlockBehaviour.Properties.copy(ModBlocks.SPORE_BLASTER.get())));

    public static final RegistryObject<Block> NATURAL_SPORE_BLASTER = registerBlock("natural_spore_blaster",
            () ->  new NaturalSporeBlaster(BlockBehaviour.Properties.copy(ModBlocks.COMPRESSED_SUBSTILIUM_SOIL.get())
                    .noOcclusion().strength(2f, 12f).requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> SMOOTH_SUBSTILIUM_SOIL = registerBlock("smooth_substilium_soil",
            () ->  new Block(BlockBehaviour.Properties.copy(COMPRESSED_SUBSTILIUM_SOIL.get())));

    public static final RegistryObject<Block> CHISELED_SUBSTILIUM_SOIL = registerBlock("chiseled_substilium_soil",
            () ->  new Block(BlockBehaviour.Properties.copy(COMPRESSED_SUBSTILIUM_SOIL.get())));

    public static final RegistryObject<Block> SUBSTILIUM_TILES = registerBlock("substilium_tiles",
            () ->  new Block(BlockBehaviour.Properties.copy(COMPRESSED_SUBSTILIUM_SOIL.get())));

    public static final RegistryObject<Block> CRACKED_SUBSTILIUM_TILES = registerBlock("cracked_substilium_tiles",
            () ->  new Block(BlockBehaviour.Properties.copy(COMPRESSED_SUBSTILIUM_SOIL.get())));

    public static final RegistryObject<Block> SUBSTILIUM_TILE_STAIRS = registerBlock("substilium_tile_stairs",
            () ->  new StairBlock(() -> ModBlocks.SUBSTILIUM_TILES.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(SUBSTILIUM_TILES.get())));

    public static final RegistryObject<Block> SUBSTILIUM_TILE_SLAB = registerBlock("substilium_tile_slab",
            () ->  new SlabBlock(BlockBehaviour.Properties.copy(SUBSTILIUM_TILES.get())));

    public static final RegistryObject<Block> SUBSTILIUM_TILE_BUTTON = registerBlock("substilium_tile_button",
            () ->  new ButtonBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_CYAN)
                    .sound(SoundType.DEEPSLATE)
                    .strength(1,  12), BlockSetType.STONE, 30, true));

    public static final RegistryObject<Block> SUBSTILIUM_TILE_PRESSURE_PLATE = registerBlock("substilium_tile_pressure_plate",
            () ->  new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING,BlockBehaviour.Properties.copy(SUBSTILIUM_TILE_BUTTON.get()),
                    BlockSetType.STONE));

    public static final RegistryObject<Block> SUBSTILIUM_TILE_WALLS = registerBlock("substilium_tile_walls",
            () ->  new WallBlock(BlockBehaviour.Properties.copy(SUBSTILIUM_TILES.get())));

    public static final RegistryObject<Block> SUBSTILIUM_STEM = registerBlock("substilium_stem",
            () ->  new ModFlammableRotatedPillarBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_CYAN)
                    .ignitedByLava()
                    .instrument(NoteBlockInstrument.BASS)
                    .sound(SoundType.STEM)
                    .strength(2,  2)));

    public static final RegistryObject<Block> STRIPPED_SUBSTILIUM_STEM = registerBlock("stripped_substilium_stem",
            () ->  new ModFlammableRotatedPillarBlock(BlockBehaviour.Properties.copy(ModBlocks.SUBSTILIUM_STEM.get())));

    public static final RegistryObject<Block> SUBSTILIUM_WOOD = registerBlock("substilium_wood",
            () ->  new ModFlammableRotatedPillarBlock(BlockBehaviour.Properties.copy(SUBSTILIUM_STEM.get())));

    public static final RegistryObject<Block> STRIPPED_SUBSTILIUM_WOOD = registerBlock("stripped_substilium_wood",
            () ->  new ModFlammableRotatedPillarBlock(BlockBehaviour.Properties.copy(ModBlocks.SUBSTILIUM_WOOD.get())));

    public static final RegistryObject<Block> SUBSTILIUM_PLANKS = registerBlock("substilium_planks",
            () ->  new ModFlammableBlock(BlockBehaviour.Properties.copy(SUBSTILIUM_STEM.get()).sound(SoundType.WOOD), 10, 5));

    public static final RegistryObject<Block> SUBSTILIUM_STAIRS = registerBlock("substilium_stairs",
            () ->  new StairBlock(() -> ModBlocks.SUBSTILIUM_PLANKS.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(SUBSTILIUM_PLANKS.get())));

    public static final RegistryObject<Block> SUBSTILIUM_SLAB = registerBlock("substilium_slab",
            () ->  new SlabBlock(BlockBehaviour.Properties.copy(SUBSTILIUM_PLANKS.get())));

    public static final RegistryObject<Block> SUBSTILIUM_BUTTON = registerBlock("substilium_button",
            () ->  new ButtonBlock(BlockBehaviour.Properties.copy(SUBSTILIUM_PLANKS.get()).strength(1, 1), BlockSetType.OAK, 5, true));

    public static final RegistryObject<Block> SUBSTILIUM_PRESSURE_PLATE = registerBlock("substilium_pressure_plate",
            () ->  new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING,BlockBehaviour.Properties.copy(SUBSTILIUM_BUTTON.get()),
                    BlockSetType.OAK));

    public static final RegistryObject<Block> SUBSTILIUM_FENCE = registerBlock("substilium_fence",
            () ->  new FenceBlock(BlockBehaviour.Properties.copy(SUBSTILIUM_PLANKS.get())));

    public static final RegistryObject<Block> SUBSTILIUM_FENCE_GATE = registerBlock("substilium_fence_gate",
            () ->  new FenceGateBlock(BlockBehaviour.Properties.copy(SUBSTILIUM_PLANKS.get()),
                    SoundEvents.FENCE_GATE_OPEN, SoundEvents.FENCE_GATE_CLOSE));

    public static final RegistryObject<Block> SUBSTILIUM_DOOR = registerBlock("substilium_door",
            () ->  new DoorBlock(BlockBehaviour.Properties.copy(SUBSTILIUM_PLANKS.get()), BlockSetType.OAK));

    public static final RegistryObject<Block> SUBSTILIUM_TRAPDOOR = registerBlock("substilium_trapdoor",
            () ->  new TrapDoorBlock(BlockBehaviour.Properties.copy(SUBSTILIUM_PLANKS.get()).noOcclusion(), BlockSetType.OAK));

    public static final RegistryObject<Block> SUBSTILIUM_SIGN = BLOCKS.register("substilium_sign",
            () ->  new ModStandingSignBlock(BlockBehaviour.Properties.copy(SUBSTILIUM_PLANKS.get()).strength(1).forceSolidOn().noCollission(), ModWoodTypes.SUBSTILIUM));
    public static final RegistryObject<Block> SUBSTILIUM_WALL_SIGN = BLOCKS.register("substilium_wall_sign",
            () ->  new ModWallSignBlock(BlockBehaviour.Properties.copy(SUBSTILIUM_PLANKS.get()).strength(1).forceSolidOn().noCollission(), ModWoodTypes.SUBSTILIUM));

    public static final RegistryObject<Block> SUBSTILIUM_HANGING_SIGN = BLOCKS.register("substilium_hanging_sign",
            () ->  new ModHangingSignBlock(BlockBehaviour.Properties.copy(SUBSTILIUM_PLANKS.get()).strength(1).forceSolidOn().noCollission(), ModWoodTypes.SUBSTILIUM));
    public static final RegistryObject<Block> SUBSTILIUM_WALL_HANGING_SIGN = BLOCKS.register("substilium_hanging_wall_sign",
            () ->  new ModWallHangingSignBlock(BlockBehaviour.Properties.copy(SUBSTILIUM_PLANKS.get()).strength(1).forceSolidOn().noCollission(),  ModWoodTypes.SUBSTILIUM));

    public static final RegistryObject<Block> SUBSTILIUM_SPROUTS = registerBlock("substilium_sprouts",
            () ->  new FlowerBlock(() -> MobEffects.CONFUSION, 5, BlockBehaviour.Properties.copy(VIBRION_GROWTH.get())
                    .mapColor(MapColor.COLOR_CYAN)
                    .lightLevel(l -> 0)));

    public static final RegistryObject<Block> BIOENGINEERING_WORKSTATION = registerBlock("bioengineering_workstation",
            () ->  new BioengineeringWorkstation(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));


    //HICKORY
    public static final RegistryObject<Block> HICKORY_LOG = registerBlock("hickory_log",
            () ->  new ModFlammableRotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LOG)
                    .mapColor(MapColor.TERRACOTTA_ORANGE)
                    .strength(2.5f, 4)));

    public static final RegistryObject<Block> HICKORY_WOOD = registerBlock("hickory_wood",
            () ->  new ModFlammableRotatedPillarBlock(BlockBehaviour.Properties.copy(HICKORY_LOG.get())));

    public static final RegistryObject<Block> STRIPPED_HICKORY_LOG = registerBlock("stripped_hickory_log",
            () ->  new ModFlammableRotatedPillarBlock(BlockBehaviour.Properties.copy(ModBlocks.HICKORY_LOG.get())));

    public static final RegistryObject<Block> STRIPPED_HICKORY_WOOD = registerBlock("stripped_hickory_wood",
            () ->  new ModFlammableRotatedPillarBlock(BlockBehaviour.Properties.copy(ModBlocks.HICKORY_LOG.get())));

    public static final RegistryObject<Block> HICKORY_PLANKS = registerBlock("hickory_planks",
            () ->  new ModFlammableBlock(BlockBehaviour.Properties.copy(HICKORY_LOG.get()), 10, 5));

    public static final RegistryObject<Block> HICKORY_STAIRS = registerBlock("hickory_stairs",
            () ->  new StairBlock(() -> ModBlocks.HICKORY_PLANKS.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(HICKORY_PLANKS.get())));

    public static final RegistryObject<Block> HICKORY_SLAB = registerBlock("hickory_slab",
            () ->  new SlabBlock(BlockBehaviour.Properties.copy(HICKORY_PLANKS.get())));

    public static final RegistryObject<Block> HICKORY_BUTTON = registerBlock("hickory_button",
            () ->  new ButtonBlock(BlockBehaviour.Properties.copy(HICKORY_PLANKS.get()).strength(1, 1), BlockSetType.OAK, 5, true));

    public static final RegistryObject<Block> HICKORY_PRESSURE_PLATE = registerBlock("hickory_pressure_plate",
            () ->  new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING,BlockBehaviour.Properties.copy(HICKORY_BUTTON.get()),
                    BlockSetType.OAK));

    public static final RegistryObject<Block> HICKORY_FENCE = registerBlock("hickory_fence",
            () ->  new FenceBlock(BlockBehaviour.Properties.copy(HICKORY_PLANKS.get())));

    public static final RegistryObject<Block> HICKORY_FENCE_GATE = registerBlock("hickory_fence_gate",
            () ->  new FenceGateBlock(BlockBehaviour.Properties.copy(HICKORY_PLANKS.get()),
                    SoundEvents.FENCE_GATE_OPEN, SoundEvents.FENCE_GATE_CLOSE));

    public static final RegistryObject<Block> HICKORY_DOOR = registerBlock("hickory_door",
            () ->  new DoorBlock(BlockBehaviour.Properties.copy(HICKORY_PLANKS.get()), BlockSetType.OAK));

    public static final RegistryObject<Block> HICKORY_TRAPDOOR = registerBlock("hickory_trapdoor",
            () ->  new TrapDoorBlock(BlockBehaviour.Properties.copy(HICKORY_PLANKS.get()).noOcclusion(), BlockSetType.OAK));

    public static final RegistryObject<Block> HICKORY_SIGN = BLOCKS.register("hickory_sign",
            () ->  new ModStandingSignBlock(BlockBehaviour.Properties.copy(HICKORY_PLANKS.get()).strength(1).forceSolidOn().noCollission(), ModWoodTypes.HICKORY));
    public static final RegistryObject<Block> HICKORY_WALL_SIGN = BLOCKS.register("hickory_wall_sign",
            () ->  new ModWallSignBlock(BlockBehaviour.Properties.copy(HICKORY_PLANKS.get()).strength(1).forceSolidOn().noCollission(), ModWoodTypes.HICKORY));

    public static final RegistryObject<Block> HICKORY_HANGING_SIGN = BLOCKS.register("hickory_hanging_sign",
            () ->  new ModHangingSignBlock(BlockBehaviour.Properties.copy(HICKORY_PLANKS.get()).strength(1).forceSolidOn().noCollission(), ModWoodTypes.HICKORY));
    public static final RegistryObject<Block> HICKORY_WALL_HANGING_SIGN = BLOCKS.register("hickory_hanging_wall_sign",
            () ->  new ModWallHangingSignBlock(BlockBehaviour.Properties.copy(HICKORY_PLANKS.get()).strength(1).forceSolidOn().noCollission(),  ModWoodTypes.HICKORY));

    public static final RegistryObject<Block> SPOTTED_WINTERGREEN = registerBlock("spotted_wintergreen",
            () ->  new FlowerBlock(() -> MobEffects.MOVEMENT_SPEED, 10, BlockBehaviour.Properties.copy(Blocks.RED_TULIP)));
    public static final RegistryObject<Block> PINKSTER_FLOWER = registerBlock("pinkster_flower",
            () ->  new FlowerBlock(() -> MobEffects.CONFUSION, 10, BlockBehaviour.Properties.copy(Blocks.RED_TULIP)));


    public static final RegistryObject<Block> HICKORY_LEAVES = registerBlock("hickory_leaves",
            () ->  new LeavesBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LEAVES)) {
                @Override
                public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
                    super.animateTick(pState, pLevel, pPos, pRandom);
                    if (!pLevel.getBlockState(pPos.below()).isAir()) return;
                    if (pRandom.nextFloat() < 0.02f) {
                        ParticleUtils.spawnHickoryParticles(pLevel, pPos, pRandom, ModParticles.HICKORY_PARTICLE.get());
                    }
                }
            });

    public static final RegistryObject<Block> RED_GLOWING_HICKORY_LEAVES = registerBlock("red_glowing_hickory_leaves",
            () ->  new GlowingLeavesBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LEAVES)));
    public static final RegistryObject<Block> BROWN_GLOWING_HICKORY_LEAVES = registerBlock("brown_glowing_hickory_leaves",
            () ->  new GlowingLeavesBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LEAVES)));
    public static final RegistryObject<Block> YELLOW_GLOWING_HICKORY_LEAVES = registerBlock("yellow_glowing_hickory_leaves",
            () ->  new GlowingLeavesBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LEAVES)));
    public static final RegistryObject<Block> GREEN_GLOWING_HICKORY_LEAVES = registerBlock("green_glowing_hickory_leaves",
            () ->  new GlowingLeavesBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LEAVES)));

    public static final RegistryObject<Block> FALLEN_HICKORY_LEAVES = registerBlock("fallen_hickory_leaves",
            () ->  new FallenHickoryLeavesBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LEAVES)));

    public static final RegistryObject<Block> HICKORY_SAPLING = registerBlock("hickory_sapling",
            () ->  new SaplingBlock(new HickoryTreeGrower(), BlockBehaviour.Properties.copy(Blocks.OAK_SAPLING)));
    public static final RegistryObject<Block> RED_GLOWING_HICKORY_SAPLING = registerBlock("red_glowing_hickory_sapling",
            () ->  new GlowingSaplingBlock(new RedGlowingHickoryTreeGrower(), BlockBehaviour.Properties.copy(Blocks.OAK_SAPLING)));
    public static final RegistryObject<Block> BROWN_GLOWING_HICKORY_SAPLING = registerBlock("brown_glowing_hickory_sapling",
            () ->  new GlowingSaplingBlock(new BrownGlowingHickoryTreeGrower(), BlockBehaviour.Properties.copy(RED_GLOWING_HICKORY_SAPLING.get())));
    public static final RegistryObject<Block> YELLOW_GLOWING_HICKORY_SAPLING = registerBlock("yellow_glowing_hickory_sapling",
            () ->  new GlowingSaplingBlock(new YellowGlowingHickoryTreeGrower(), BlockBehaviour.Properties.copy(RED_GLOWING_HICKORY_SAPLING.get())));
    public static final RegistryObject<Block> GREEN_GLOWING_HICKORY_SAPLING = registerBlock("green_glowing_hickory_sapling",
            () ->  new GlowingSaplingBlock(new GreenGlowingHickoryTreeGrower(), BlockBehaviour.Properties.copy(RED_GLOWING_HICKORY_SAPLING.get())));


    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> blockObj = BLOCKS.register(name, block);
        registerBlockItem(name, blockObj);
        return blockObj;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
        return ModItems.ITEMS.register(name, ()  -> new BlockItem(block.get(), new Item.Properties()));
    }

    private static <T extends Block> RegistryObject<T> registerBlockWithStackLimit(String name, Supplier<T> block, int stackLimit) {
        RegistryObject<T> blockObj = BLOCKS.register(name, block);
        registerBlockItemWithStackLimit(name, blockObj, stackLimit);
        return blockObj;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItemWithStackLimit(String name, RegistryObject<T> block, int stackLimit) {
        return ModItems.ITEMS.register(name, ()  -> new BlockItem(block.get(), new Item.Properties().stacksTo(stackLimit)));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
