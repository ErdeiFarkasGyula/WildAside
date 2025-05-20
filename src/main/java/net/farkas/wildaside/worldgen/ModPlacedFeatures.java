package net.farkas.wildaside.worldgen;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.block.ModBlocks;
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

import java.util.List;

public class ModPlacedFeatures {
    public static final ResourceKey<PlacedFeature> REDLIKE_SUBSTILIUM_MUSHROOM_PLACED = registerKey("redlike_substilium_mushroom");
    public static final ResourceKey<PlacedFeature> BROWNLIKE_SUBSTILIUM_MUSHROOM_PLACED = registerKey("brownlike_substilium_mushroom");

    public static final ResourceKey<PlacedFeature> VIBRION_GROWTH_PLACED = registerKey("vibrion_growth");
    public static final ResourceKey<PlacedFeature> VIBRION_SPOREHOLDER_PLACED = registerKey("vibrion_sporeholder");
    public static final ResourceKey<PlacedFeature> SUBSTILIUM_SPROUTS_PLACED_KEY = registerKey("substilium_sprouts");

    public static final ResourceKey<PlacedFeature> HANGING_VIBRION_VINES = registerKey("hanging_vibrion_vines");
    public static final ResourceKey<PlacedFeature> HANGING_VIBRION_GEL = registerKey("hanging_vibrion_gel");
    public static final ResourceKey<PlacedFeature> HANGING_LIT_VIBRION_GEL = registerKey("hanging_lit_vibrion_gel");

    public static final ResourceKey<PlacedFeature> OVERGROWN_ENTORIUM_ORE_PLACED = registerKey("overgrown_entorium_ore");

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

    public static final ResourceKey<PlacedFeature> COMPRESSED_SUBSTILIUM_SOIL_PLACED = registerKey("compressed_substilium_soil");

    public static final ResourceKey<PlacedFeature> NATURAL_SPORE_BLASTER_PLACED = registerKey("natural_spore_blaster");

    public static final ResourceKey<PlacedFeature> SPOTTED_WINTERGREEN_PLACED = registerKey("spotted_wintergreen");
    public static final ResourceKey<PlacedFeature> PINKSTER_FLOWER_PLACED = registerKey("pinkster_flower");

    public static final ResourceKey<PlacedFeature> HICKORY_TREE_PLACED = registerKey("hickory_tree");
    public static final ResourceKey<PlacedFeature> RED_GLOWING_HICKORY_TREE_PLACED = registerKey("red_glowing_hickory_tree");
    public static final ResourceKey<PlacedFeature> BROWN_GLOWING_HICKORY_TREE_PLACED = registerKey("brown_glowing_hickory_tree");
    public static final ResourceKey<PlacedFeature> YELLOW_GLOWING_HICKORY_TREE_PLACED = registerKey("yellow_glowing_hickory_tree");
    public static final ResourceKey<PlacedFeature> GREEN_GLOWING_HICKORY_TREE_PLACED = registerKey("green_glowing_hickory_tree");

    public static final ResourceKey<PlacedFeature> HICKORY_SAPLING_PLACED = registerKey("hickory_sapling");
    public static final ResourceKey<PlacedFeature> RED_GLOWING_HICKORY_SAPLING_PLACED = registerKey("red_glowing_hickory_sapling");
    public static final ResourceKey<PlacedFeature> BROWN_GLOWING_HICKORY_SAPLING_PLACED = registerKey("brown_glowing_hickory_sapling");
    public static final ResourceKey<PlacedFeature> YELLOW_GLOWING_HICKORY_SAPLING_PLACED = registerKey("yellow_glowing_hickory_sapling");
    public static final ResourceKey<PlacedFeature> GREEN_GLOWING_HICKORY_SAPLING_PLACED = registerKey("green_glowing_hickory_sapling");

    public static void bootstrap(BootstapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);
        HeightRangePlacement VibrionHivePlacement = HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(80));

        register(context, REDLIKE_SUBSTILIUM_MUSHROOM_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.REDLIKE_SUBSTILIUM_MUSHROOM),
                List.of(PlacementUtils.countExtra(128, 0.5f, 32), InSquarePlacement.spread(), VibrionHivePlacement, BiomeFilter.biome()));
        register(context, BROWNLIKE_SUBSTILIUM_MUSHROOM_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.BROWNLIKE_SUBSTILIUM_MUSHROOM),
                List.of(PlacementUtils.countExtra(128, 0.5f, 32), InSquarePlacement.spread(), VibrionHivePlacement, BiomeFilter.biome()));

        register(context, VIBRION_GROWTH_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.VIBRION_GROWTH),
                List.of(PlacementUtils.countExtra(128, 1f, 32), InSquarePlacement.spread(), VibrionHivePlacement, BiomeFilter.biome()));
        register(context, VIBRION_SPOREHOLDER_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.VIBRION_SPOREHOLDER),
                List.of(PlacementUtils.countExtra(64, 0.5f, 32), InSquarePlacement.spread(), VibrionHivePlacement, BiomeFilter.biome()));
        register(context, SUBSTILIUM_SPROUTS_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.SUBSTILIUM_SPROUTS),
                List.of(PlacementUtils.countExtra(128, 1f, 32), InSquarePlacement.spread(), VibrionHivePlacement, BiomeFilter.biome()));

        register(context, HANGING_VIBRION_VINES, configuredFeatures.getOrThrow(ModConfiguredFeatures.HANGING_VIBRION_VINES), List.of(
                CountPlacement.of(256), InSquarePlacement.spread(), VibrionHivePlacement,
                EnvironmentScanPlacement.scanningFor(Direction.UP, BlockPredicate.hasSturdyFace(Direction.DOWN),
                        BlockPredicate.ONLY_IN_AIR_PREDICATE, 32), RandomOffsetPlacement.vertical(ConstantInt.of(-1)), BiomeFilter.biome()));
        register(context, HANGING_VIBRION_GEL, configuredFeatures.getOrThrow(ModConfiguredFeatures.HANGING_VIBRION_GEL), List.of(
                CountPlacement.of(256), InSquarePlacement.spread(), VibrionHivePlacement,
                EnvironmentScanPlacement.scanningFor(Direction.UP, BlockPredicate.hasSturdyFace(Direction.DOWN),
                        BlockPredicate.ONLY_IN_AIR_PREDICATE, 32), RandomOffsetPlacement.vertical(ConstantInt.of(-1)), BiomeFilter.biome()));
        register(context, HANGING_LIT_VIBRION_GEL, configuredFeatures.getOrThrow(ModConfiguredFeatures.HANGING_LIT_VIBRION_GEL), List.of(
                CountPlacement.of(256), InSquarePlacement.spread(), VibrionHivePlacement,
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

        register(context, OVERGROWN_ENTORIUM_ORE_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.OVERGROWN_ENTORIUM_ORE),
                ModOrePlacement.commonOrePlacement(10, HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(80))));

        register(context, COMPRESSED_SUBSTILIUM_SOIL_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.COMPRESSED_SUBSTILIUM_SOIL),
                ModOrePlacement.commonOrePlacement(48, HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(80))));

        register(context, NATURAL_SPORE_BLASTER_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.NATURAL_SPORE_BLASTER),
                ModOrePlacement.commonOrePlacement(128, HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(80))));

        register(context, SPOTTED_WINTERGREEN_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.SPOTTED_EVERGREEN),
                List.of(RarityFilter.onAverageOnceEvery(16), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome()));
        register(context, PINKSTER_FLOWER_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.PINKSTER_FLOWER),
                List.of(RarityFilter.onAverageOnceEvery(16), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome()));

        register(context, HICKORY_TREE_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.HICKORY_TREE),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(8, 0.1f, 2), ModBlocks.HICKORY_SAPLING.get()));
        register(context, RED_GLOWING_HICKORY_TREE_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.RED_GLOWING_HICKORY_TREE),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(2, 0.1f, 2), ModBlocks.RED_GLOWING_HICKORY_SAPLING.get()));
        register(context, BROWN_GLOWING_HICKORY_TREE_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.BROWN_GLOWING_HICKORY_TREE),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(2, 0.1f, 2), ModBlocks.BROWN_GLOWING_HICKORY_SAPLING.get()));
        register(context, YELLOW_GLOWING_HICKORY_TREE_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.YELLOW_GLOWING_HICKORY_TREE),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(2, 0.1f, 2), ModBlocks.YELLOW_GLOWING_HICKORY_SAPLING.get()));
        register(context, GREEN_GLOWING_HICKORY_TREE_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.GREEN_GLOWING_HICKORY_TREE),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(2, 0.1f, 2), ModBlocks.GREEN_GLOWING_HICKORY_SAPLING.get()));

        register(context, HICKORY_SAPLING_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.HICKORY_SAPLING),
                List.of(RarityFilter.onAverageOnceEvery(2), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome()));
        register(context, RED_GLOWING_HICKORY_SAPLING_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.RED_GLOWING_HICKORY_SAPLING),
                List.of(RarityFilter.onAverageOnceEvery(8), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome()));
        register(context, BROWN_GLOWING_HICKORY_SAPLING_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.BROWN_GLOWING_HICKORY_SAPLING),
                List.of(RarityFilter.onAverageOnceEvery(8), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome()));
        register(context, YELLOW_GLOWING_HICKORY_SAPLING_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.YELLOW_GLOWING_HICKORY_SAPLING),
                List.of(RarityFilter.onAverageOnceEvery(8), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome()));
        register(context, GREEN_GLOWING_HICKORY_SAPLING_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.GREEN_GLOWING_HICKORY_SAPLING),
                List.of(RarityFilter.onAverageOnceEvery(8), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome()));
    }

    private static ResourceKey<PlacedFeature> registerKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(WildAside.MOD_ID, name));
    }

    private static void register(BootstapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key, Holder<ConfiguredFeature<?, ?>> configuration,
                                 List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }
}