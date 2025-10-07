package net.farkas.wildaside.worldgen;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.block.ModBlocks;
import net.farkas.wildaside.util.HickoryColour;
import net.farkas.wildaside.worldgen.modifier.LargePatchNoiseModifier;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;
import org.joml.Vector2f;

import java.util.EnumMap;
import java.util.List;
import java.util.stream.Stream;

public class ModPlacedFeatures {
    public static final ResourceKey<PlacedFeature> LARGE_SUBSTILIUM_MUSHROOM = registerKey("large_substilium_mushroom");

    public static final ResourceKey<PlacedFeature> VIBRION_GROWTH = registerKey("vibrion_growth");
    public static final ResourceKey<PlacedFeature> VIBRION_SPOREHOLDER = registerKey("vibrion_sporeholder");
    public static final ResourceKey<PlacedFeature> SUBSTILIUM_SPROUTS = registerKey("substilium_sprouts");

    public static final ResourceKey<PlacedFeature> HANGING_VIBRION_VINES = registerKey("hanging_vibrion_vines");
    public static final ResourceKey<PlacedFeature> HANGING_VIBRION_GEL = registerKey("hanging_vibrion_gel");
    public static final ResourceKey<PlacedFeature> HANGING_LIT_VIBRION_GEL = registerKey("hanging_lit_vibrion_gel");

    public static final ResourceKey<PlacedFeature> OVERGROWN_ENTORIUM_ORE = registerKey("overgrown_entorium_ore");

    public static final ResourceKey<PlacedFeature> SUBSTILIUM_COAL_ORE = registerKey("substilium_coal_ore");
    public static final ResourceKey<PlacedFeature> SUBSTILIUM_COPPER_ORE = registerKey("substilium_copper_ore");
    public static final ResourceKey<PlacedFeature> SUBSTILIUM_COPPER_ORE_LARGE = registerKey("substilium_copper_ore_large");
    public static final ResourceKey<PlacedFeature> SUBSTILIUM_LAPIS_ORE = registerKey("substilium_lapis_ore");
    public static final ResourceKey<PlacedFeature> SUBSTILIUM_LAPIS_ORE_BURIED = registerKey("substilium_lapis_ore_buried");
    public static final ResourceKey<PlacedFeature> SUBSTILIUM_IRON_ORE_MIDDLE = registerKey("substilium_iron_ore_middle");
    public static final ResourceKey<PlacedFeature> SUBSTILIUM_IRON_ORE_SMALL = registerKey("substilium_iron_ore_small");
    public static final ResourceKey<PlacedFeature> SUBSTILIUM_GOLD_ORE = registerKey("substilium_gold_ore");
    public static final ResourceKey<PlacedFeature> SUBSTILIUM_GOLD_ORE_LOWER = registerKey("substilium_gold_ore_lower");
    public static final ResourceKey<PlacedFeature> SUBSTILIUM_REDSTONE_ORE = registerKey("substilium_redstone_ore");
    public static final ResourceKey<PlacedFeature> SUBSTILIUM_REDSTONE_ORE_LOWER = registerKey("substilium_redstone_ore_lower");
    public static final ResourceKey<PlacedFeature> SUBSTILIUM_DIAMOND_ORE = registerKey("substilium_diamond_ore");
    public static final ResourceKey<PlacedFeature> SUBSTILIUM_DIAMOND_ORE_LARGE = registerKey("substilium_diamond_ore_large");
    public static final ResourceKey<PlacedFeature> SUBSTILIUM_DIAMOND_ORE_BURIED = registerKey("substilium_diamond_ore_buried");
    public static final ResourceKey<PlacedFeature> SUBSTILIUM_EMERALD_ORE = registerKey("substilium_emerald_ore");

    public static final ResourceKey<PlacedFeature> COMPRESSED_SUBSTILIUM_SOIL = registerKey("compressed_substilium_soil");

    public static final ResourceKey<PlacedFeature> NATURAL_SPORE_BLASTER = registerKey("natural_spore_blaster");

    public static final ResourceKey<PlacedFeature> SPOTTED_WINTERGREEN = registerKey("spotted_wintergreen");
    public static final ResourceKey<PlacedFeature> PINKSTER_FLOWER = registerKey("pinkster_flower");

    public static final ResourceKey<PlacedFeature> HICKORY_TREE = registerKey("hickory_tree");
    public static final ResourceKey<PlacedFeature> RED_GLOWING_HICKORY_TREE = registerKey("red_glowing_hickory_tree");
    public static final ResourceKey<PlacedFeature> BROWN_GLOWING_HICKORY_TREE = registerKey("brown_glowing_hickory_tree");
    public static final ResourceKey<PlacedFeature> YELLOW_GLOWING_HICKORY_TREE = registerKey("yellow_glowing_hickory_tree");
    public static final ResourceKey<PlacedFeature> GREEN_GLOWING_HICKORY_TREE = registerKey("green_glowing_hickory_tree");

    public static final ResourceKey<PlacedFeature> HICKORY_SAPLING = registerKey("hickory_sapling");
    public static final ResourceKey<PlacedFeature> RED_GLOWING_HICKORY_SAPLING = registerKey("red_glowing_hickory_sapling");
    public static final ResourceKey<PlacedFeature> BROWN_GLOWING_HICKORY_SAPLING = registerKey("brown_glowing_hickory_sapling");
    public static final ResourceKey<PlacedFeature> YELLOW_GLOWING_HICKORY_SAPLING = registerKey("yellow_glowing_hickory_sapling");
    public static final ResourceKey<PlacedFeature> GREEN_GLOWING_HICKORY_SAPLING = registerKey("green_glowing_hickory_sapling");

    public static final ResourceKey<PlacedFeature> HICKORY_BUSH = registerKey("hickory_bush");
    public static final ResourceKey<PlacedFeature> RED_GLOWING_HICKORY_BUSH = registerKey("red_glowing_hickory_bush");
    public static final ResourceKey<PlacedFeature> BROWN_GLOWING_HICKORY_BUSH = registerKey("brown_glowing_hickory_bush");
    public static final ResourceKey<PlacedFeature> YELLOW_GLOWING_HICKORY_BUSH = registerKey("yellow_glowing_hickory_bush");
    public static final ResourceKey<PlacedFeature> GREEN_GLOWING_HICKORY_BUSH = registerKey("green_glowing_hickory_bush");

    public static final ResourceKey<PlacedFeature> FALLEN_HICKORY_TREE = registerKey("fallen_hickory_tree");

    public static final ResourceKey<PlacedFeature> PODZOL_VEIN = registerKey("podzol_vein");


    public static final EnumMap<HickoryColour, Vector2f> HICKORY_NOISES = new EnumMap<>(HickoryColour.class);
    static {
        HICKORY_NOISES.put(HickoryColour.HICKORY, null);
        HICKORY_NOISES.put(HickoryColour.RED_GLOWING, new Vector2f(-1.0f, -0.45f));
        HICKORY_NOISES.put(HickoryColour.BROWN_GLOWING, new Vector2f(-0.5f, 0.0f));
        HICKORY_NOISES.put(HickoryColour.YELLOW_GLOWING, new Vector2f(-0.05f, 0.45f));
        HICKORY_NOISES.put(HickoryColour.GREEN_GLOWING, new Vector2f(0.4f, 1.0f));
    }

    public static final EnumMap<HickoryColour, ResourceKey<PlacedFeature>> HICKORY_TREES = new EnumMap<>(HickoryColour.class);
    static {
        HICKORY_TREES.put(HickoryColour.HICKORY, HICKORY_TREE);
        HICKORY_TREES.put(HickoryColour.RED_GLOWING, RED_GLOWING_HICKORY_TREE);
        HICKORY_TREES.put(HickoryColour.BROWN_GLOWING, BROWN_GLOWING_HICKORY_TREE);
        HICKORY_TREES.put(HickoryColour.YELLOW_GLOWING, YELLOW_GLOWING_HICKORY_TREE);
        HICKORY_TREES.put(HickoryColour.GREEN_GLOWING, GREEN_GLOWING_HICKORY_TREE);
    }

    public static final EnumMap<HickoryColour, ResourceKey<PlacedFeature>> HICKORY_SAPLINGS = new EnumMap<>(HickoryColour.class);
    static {
        HICKORY_SAPLINGS.put(HickoryColour.HICKORY, HICKORY_SAPLING);
        HICKORY_SAPLINGS.put(HickoryColour.RED_GLOWING, RED_GLOWING_HICKORY_SAPLING);
        HICKORY_SAPLINGS.put(HickoryColour.BROWN_GLOWING, BROWN_GLOWING_HICKORY_SAPLING );
        HICKORY_SAPLINGS.put(HickoryColour.YELLOW_GLOWING, YELLOW_GLOWING_HICKORY_SAPLING);
        HICKORY_SAPLINGS.put(HickoryColour.GREEN_GLOWING, GREEN_GLOWING_HICKORY_SAPLING);
    }

    public static final EnumMap<HickoryColour, ResourceKey<PlacedFeature>> HICKORY_BUSHES = new EnumMap<>(HickoryColour.class);
    static {
        HICKORY_BUSHES.put(HickoryColour.HICKORY, HICKORY_BUSH);
        HICKORY_BUSHES.put(HickoryColour.RED_GLOWING, RED_GLOWING_HICKORY_BUSH);
        HICKORY_BUSHES.put(HickoryColour.BROWN_GLOWING, BROWN_GLOWING_HICKORY_BUSH );
        HICKORY_BUSHES.put(HickoryColour.YELLOW_GLOWING, YELLOW_GLOWING_HICKORY_BUSH);
        HICKORY_BUSHES.put(HickoryColour.GREEN_GLOWING, GREEN_GLOWING_HICKORY_BUSH);
    }

    public static void bootstrap(BootstapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);
        HeightRangePlacement vibrionHivePlacement = HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(80));

        register(context, LARGE_SUBSTILIUM_MUSHROOM, configuredFeatures.getOrThrow(ModConfiguredFeatures.LARGE_SUBSTILIUM_MUSHROOM),
                List.of(PlacementUtils.countExtra(200, 0.5f, 56), InSquarePlacement.spread(), vibrionHivePlacement, BiomeFilter.biome()));

        register(context, VIBRION_GROWTH, configuredFeatures.getOrThrow(ModConfiguredFeatures.VIBRION_GROWTH),
                List.of(PlacementUtils.countExtra(128, 1f, 32), InSquarePlacement.spread(), vibrionHivePlacement, BiomeFilter.biome()));
        register(context, VIBRION_SPOREHOLDER, configuredFeatures.getOrThrow(ModConfiguredFeatures.VIBRION_SPOREHOLDER),
                List.of(PlacementUtils.countExtra(64, 0.5f, 32), InSquarePlacement.spread(), vibrionHivePlacement, BiomeFilter.biome()));
        register(context, SUBSTILIUM_SPROUTS, configuredFeatures.getOrThrow(ModConfiguredFeatures.SUBSTILIUM_SPROUTS),
                List.of(PlacementUtils.countExtra(128, 1f, 32), InSquarePlacement.spread(), vibrionHivePlacement, BiomeFilter.biome()));

        register(context, HANGING_VIBRION_VINES, configuredFeatures.getOrThrow(ModConfiguredFeatures.HANGING_VIBRION_VINES), List.of(
                CountPlacement.of(256), InSquarePlacement.spread(), vibrionHivePlacement,
                EnvironmentScanPlacement.scanningFor(Direction.UP, BlockPredicate.hasSturdyFace(Direction.DOWN),
                        BlockPredicate.ONLY_IN_AIR_PREDICATE, 32), RandomOffsetPlacement.vertical(ConstantInt.of(-1)), BiomeFilter.biome()));

        register(context, HANGING_VIBRION_GEL, configuredFeatures.getOrThrow(ModConfiguredFeatures.HANGING_VIBRION_GEL), List.of(
                CountPlacement.of(64), InSquarePlacement.spread(), vibrionHivePlacement,
                EnvironmentScanPlacement.scanningFor(Direction.UP, BlockPredicate.hasSturdyFace(Direction.DOWN),
                        BlockPredicate.ONLY_IN_AIR_PREDICATE, 32), RandomOffsetPlacement.vertical(ConstantInt.of(-1)), BiomeFilter.biome()));
        register(context, HANGING_LIT_VIBRION_GEL, configuredFeatures.getOrThrow(ModConfiguredFeatures.HANGING_LIT_VIBRION_GEL), List.of(
                CountPlacement.of(64), InSquarePlacement.spread(), vibrionHivePlacement,
                EnvironmentScanPlacement.scanningFor(Direction.UP, BlockPredicate.hasSturdyFace(Direction.DOWN),
                        BlockPredicate.ONLY_IN_AIR_PREDICATE, 32), RandomOffsetPlacement.vertical(ConstantInt.of(-1)), BiomeFilter.biome()));

        register(context, SUBSTILIUM_COAL_ORE, configuredFeatures.getOrThrow(ModConfiguredFeatures.SUBSTILIUM_COAL_ORE),
                ModOrePlacement.commonOrePlacement(20, HeightRangePlacement.triangle(VerticalAnchor.absolute(0), VerticalAnchor.absolute(192))));
        register(context, SUBSTILIUM_COPPER_ORE, configuredFeatures.getOrThrow(ModConfiguredFeatures.SUBSTILIUM_COPPER_ORE_SMALL),
                ModOrePlacement.commonOrePlacement(16, HeightRangePlacement.triangle(VerticalAnchor.absolute(-16), VerticalAnchor.absolute(112))));
        register(context, SUBSTILIUM_COPPER_ORE_LARGE, configuredFeatures.getOrThrow(ModConfiguredFeatures.SUBSTILIUM_COPPER_ORE_LARGE),
                ModOrePlacement.commonOrePlacement(16, HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(112))));
        register(context, SUBSTILIUM_LAPIS_ORE, configuredFeatures.getOrThrow(ModConfiguredFeatures.SUBSTILIUM_LAPIS_ORE),
                ModOrePlacement.commonOrePlacement(2, HeightRangePlacement.triangle(VerticalAnchor.absolute(-32), VerticalAnchor.absolute(64))));
        register(context, SUBSTILIUM_LAPIS_ORE_BURIED, configuredFeatures.getOrThrow(ModConfiguredFeatures.SUBSTILIUM_LAPIS_ORE_BURIED),
                ModOrePlacement.commonOrePlacement(4, HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(63))));
        register(context, SUBSTILIUM_IRON_ORE_MIDDLE, configuredFeatures.getOrThrow(ModConfiguredFeatures.SUBSTILIUM_IRON_ORE),
                ModOrePlacement.commonOrePlacement(10, HeightRangePlacement.triangle(VerticalAnchor.absolute(-24), VerticalAnchor.absolute(56))));
        register(context, SUBSTILIUM_IRON_ORE_SMALL, configuredFeatures.getOrThrow(ModConfiguredFeatures.SUBSTILIUM_IRON_ORE_SMALL),
                ModOrePlacement.commonOrePlacement(11, HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(72))));
        register(context, SUBSTILIUM_GOLD_ORE, configuredFeatures.getOrThrow(ModConfiguredFeatures.SUBSTILIUM_GOLD_ORE_BURIED),
                ModOrePlacement.commonOrePlacement(4, HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(32))));
        register(context, SUBSTILIUM_GOLD_ORE_LOWER, configuredFeatures.getOrThrow(ModConfiguredFeatures.SUBSTILIUM_GOLD_ORE_BURIED),
                ModOrePlacement.orePlacement(CountPlacement.of(UniformInt.of(0, 1)), HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(-48))));
        register(context, SUBSTILIUM_REDSTONE_ORE, configuredFeatures.getOrThrow(ModConfiguredFeatures.SUBSTILIUM_REDSTONE_ORE),
                ModOrePlacement.commonOrePlacement(4, HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(15))));
        register(context, SUBSTILIUM_REDSTONE_ORE_LOWER, configuredFeatures.getOrThrow(ModConfiguredFeatures.SUBSTILIUM_REDSTONE_ORE),
                ModOrePlacement.commonOrePlacement(8, HeightRangePlacement.triangle(VerticalAnchor.absolute(-32), VerticalAnchor.absolute(32))));
        register(context, SUBSTILIUM_DIAMOND_ORE, configuredFeatures.getOrThrow(ModConfiguredFeatures.SUBSTILIUM_DIAMOND_ORE_SMALL),
                ModOrePlacement.commonOrePlacement(7, HeightRangePlacement.triangle(VerticalAnchor.absolute(-80), VerticalAnchor.absolute(80))));
        register(context, SUBSTILIUM_DIAMOND_ORE_BURIED, configuredFeatures.getOrThrow(ModConfiguredFeatures.SUBSTILIUM_DIAMOND_ORE_LARGE),
                ModOrePlacement.commonOrePlacement(4, HeightRangePlacement.triangle(VerticalAnchor.absolute(-80), VerticalAnchor.absolute(80))));
        register(context, SUBSTILIUM_DIAMOND_ORE_LARGE, configuredFeatures.getOrThrow(ModConfiguredFeatures.SUBSTILIUM_DIAMOND_ORE_BURIED),
                ModOrePlacement.commonOrePlacement(9, HeightRangePlacement.triangle(VerticalAnchor.absolute(-80), VerticalAnchor.absolute(80))));
        register(context, SUBSTILIUM_EMERALD_ORE, configuredFeatures.getOrThrow(ModConfiguredFeatures.SUBSTILIUM_EMERALD_ORE),
                ModOrePlacement.commonOrePlacement(100, HeightRangePlacement.triangle(VerticalAnchor.absolute(-16), VerticalAnchor.absolute(480))));

        register(context, OVERGROWN_ENTORIUM_ORE, configuredFeatures.getOrThrow(ModConfiguredFeatures.OVERGROWN_ENTORIUM_ORE),
                ModOrePlacement.commonOrePlacement(10, HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(80))));

        register(context, COMPRESSED_SUBSTILIUM_SOIL, configuredFeatures.getOrThrow(ModConfiguredFeatures.COMPRESSED_SUBSTILIUM_SOIL),
                ModOrePlacement.commonOrePlacement(48, HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(80))));

        register(context, NATURAL_SPORE_BLASTER, configuredFeatures.getOrThrow(ModConfiguredFeatures.NATURAL_SPORE_BLASTER),
                ModOrePlacement.commonOrePlacement(256, HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(80))));

        register(context, SPOTTED_WINTERGREEN, configuredFeatures.getOrThrow(ModConfiguredFeatures.SPOTTED_EVERGREEN),
                List.of(RarityFilter.onAverageOnceEvery(16), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome()));
        register(context, PINKSTER_FLOWER, configuredFeatures.getOrThrow(ModConfiguredFeatures.PINKSTER_FLOWER),
                List.of(RarityFilter.onAverageOnceEvery(16), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome()));

        register(context, HICKORY_TREE, configuredFeatures.getOrThrow(ModConfiguredFeatures.HICKORY_TREE),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(32, 0.25f, 16), ModBlocks.HICKORY_SAPLING.get()));
        register(context, HICKORY_SAPLING, configuredFeatures.getOrThrow(ModConfiguredFeatures.HICKORY_SAPLING),
                List.of(RarityFilter.onAverageOnceEvery(1), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome()));
        register(context, HICKORY_BUSH, configuredFeatures.getOrThrow(ModConfiguredFeatures.HICKORY_BUSH),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(48, 0.5f, 16)));


        for (HickoryColour colour : HickoryColour.values()) {
            if (colour != HickoryColour.HICKORY) {
                Vector2f noise = HICKORY_NOISES.get(colour);
                registerGlowingHickoryTree(context, configuredFeatures, colour, noise.x, noise.y);
                registerGlowingHickorySapling(context, configuredFeatures, colour, noise.x, noise.y);
                registerGlowingHickoryBush(context, configuredFeatures, colour, noise.x, noise.y);
            }
        }

        register(context, FALLEN_HICKORY_TREE, configuredFeatures.getOrThrow(ModConfiguredFeatures.FALLEN_HICKORY_TREE),
                List.of(RarityFilter.onAverageOnceEvery(8), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome()));

        register(context, PODZOL_VEIN, configuredFeatures.getOrThrow(ModConfiguredFeatures.PODZOL_VEIN),
                ModOrePlacement.commonOrePlacement(16, HeightRangePlacement.triangle(VerticalAnchor.absolute(50), VerticalAnchor.absolute(100))));
    }

    private static void registerGlowingHickoryTree(BootstapContext<PlacedFeature> context, HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures, HickoryColour colour, float minNoise, float maxNoise) {
        int count = 16;
        List<PlacementModifier> treeMods = VegetationPlacements.treePlacement(PlacementUtils.countExtra(count, 0.25f, 8), ModBlocks.HICKORY_SAPLINGS.get(colour).get());
        List<PlacementModifier> patchMods = List.of(new LargePatchNoiseModifier(minNoise, maxNoise));

        List<PlacementModifier> allMods = Stream.concat(treeMods.stream(), patchMods.stream()).toList();

        register(context, HICKORY_TREES.get(colour), configuredFeatures.getOrThrow(ModConfiguredFeatures.HICKORY_TREES.get(colour)), allMods);
    }


    private static void registerGlowingHickorySapling(BootstapContext<PlacedFeature> context, HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures, HickoryColour colour, float minNoise, float maxNoise) {
        int chance = 4;
        List<PlacementModifier> saplingMods = List.of(RarityFilter.onAverageOnceEvery(chance), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
        List<PlacementModifier> patchMods = List.of(new LargePatchNoiseModifier(minNoise, maxNoise));

        List<PlacementModifier> allMods = Stream.concat(saplingMods.stream(), patchMods.stream()).toList();

        register(context, HICKORY_SAPLINGS.get(colour), configuredFeatures.getOrThrow(ModConfiguredFeatures.HICKORY_SAPLINGS.get(colour)), allMods);
    }

    private static void registerGlowingHickoryBush(BootstapContext<PlacedFeature> context, HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures, HickoryColour colour, float minNoise, float maxNoise) {
        int value = 8;

        List<PlacementModifier> bushMods = VegetationPlacements.treePlacement(PlacementUtils.countExtra(value, 0.5f, 4));
        List<PlacementModifier> patchMods = List.of(new LargePatchNoiseModifier(minNoise, maxNoise));

        List<PlacementModifier> allMods = Stream.concat(bushMods.stream(), patchMods.stream()).toList();

        register(context, HICKORY_BUSHES.get(colour), configuredFeatures.getOrThrow(ModConfiguredFeatures.HICKORY_BUSHES.get(colour)), allMods);
    }

    private static ResourceKey<PlacedFeature> registerKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(WildAside.MOD_ID, name));
    }

    private static void register(BootstapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key, Holder<ConfiguredFeature<?, ?>> configuration, List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }
}