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
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;

import java.util.List;

public class ModPlacedFeatures {
    public static final ResourceKey<PlacedFeature> REDLIKE_SUBSTILIUM_MUSHROOM_PLACED = registerKey("redlike_substilium_mushroom_placed");
    public static final ResourceKey<PlacedFeature> BROWNLIKE_SUBSTILIUM_MUSHROOM_PLACED = registerKey("brownlike_substilium_mushroom_placed");

    public static final ResourceKey<PlacedFeature> VIBRION_GROWTH_PLACED = registerKey("vibrion_growth_placed");
    public static final ResourceKey<PlacedFeature> VIBRION_SPOREHOLDER_PLACED = registerKey("vibrion_sporeholder_placed");
    public static final ResourceKey<PlacedFeature> SUBSTILIUM_SPROUTS_PLACED_KEY = registerKey("substilium_sprouts_placed");

    public static final ResourceKey<PlacedFeature> HANGING_VIBRION_VINES = registerKey("hanging_vibrion_vines");
    public static final ResourceKey<PlacedFeature> HANGING_VIBRION_GEL = registerKey("hanging_vibrion_gel");
    public static final ResourceKey<PlacedFeature> HANGING_LIT_VIBRION_GEL = registerKey("hanging_lit_vibrion_gel");

    public static final ResourceKey<PlacedFeature> OVERGROWN_ENTORIUM_ORE_PLACED = registerKey("overgrown_entorium_ore_placed");
    public static final ResourceKey<PlacedFeature> ENTORIUM_ORE_PLACED = registerKey("entorium_ore_placed");

    public static final ResourceKey<PlacedFeature> COMPRESSED_SUBSTILIUM_SOIL_PLACED = registerKey("compressed_substilium_soil_placed");

    public static final ResourceKey<PlacedFeature> NATURAL_SPORE_BLASTER_X_PLACED = registerKey("natural_spore_blaster_x_placed");
    public static final ResourceKey<PlacedFeature> NATURAL_SPORE_BLASTER_Y_PLACED = registerKey("natural_spore_blaster_y_placed");
    public static final ResourceKey<PlacedFeature> NATURAL_SPORE_BLASTER_Z_PLACED = registerKey("natural_spore_blaster_z_placed");

    public static final ResourceKey<PlacedFeature> SPOTTED_WINTERGREEN_PLACED = registerKey("spotted_wintergreen_placed");
    public static final ResourceKey<PlacedFeature> PINKSTER_FLOWER_PLACED = registerKey("pinkster_flower_placed");

    public static final ResourceKey<PlacedFeature> HICKORY_TREE_PLACED = registerKey("hickory_tree_placed");
    public static final ResourceKey<PlacedFeature> RED_GLOWING_HICKORY_TREE_PLACED = registerKey("red_glowing_hickory_tree_placed");
    public static final ResourceKey<PlacedFeature> BROWN_GLOWING_HICKORY_TREE_PLACED = registerKey("brown_glowing_hickory_tree_placed");
    public static final ResourceKey<PlacedFeature> YELLOW_GLOWING_HICKORY_TREE_PLACED = registerKey("yellow_glowing_hickory_tree_placed");
    public static final ResourceKey<PlacedFeature> GREEN_GLOWING_HICKORY_TREE_PLACED = registerKey("green_glowing_hickory_tree_placed");

    public static final ResourceKey<PlacedFeature> HICKORY_SAPLING_PLACED = registerKey("hickory_sapling_placed");
    public static final ResourceKey<PlacedFeature> RED_GLOWING_HICKORY_SAPLING_PLACED = registerKey("red_glowing_hickory_sapling_placed");
    public static final ResourceKey<PlacedFeature> BROWN_GLOWING_HICKORY_SAPLING_PLACED = registerKey("brown_glowing_hickory_sapling_placed");
    public static final ResourceKey<PlacedFeature> YELLOW_GLOWING_HICKORY_SAPLING_PLACED = registerKey("yellow_glowing_hickory_sapling_placed");
    public static final ResourceKey<PlacedFeature> GREEN_GLOWING_HICKORY_SAPLING_PLACED = registerKey("green_glowing_hickory_sapling_placed");

    public static void bootstrap(BootstapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        register(context, REDLIKE_SUBSTILIUM_MUSHROOM_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.REDLIKE_SUBSTILIUM_MUSHROOM),
                List.of(PlacementUtils.countExtra(128, 1, 32), InSquarePlacement.spread(), PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT, BiomeFilter.biome()));
        register(context, BROWNLIKE_SUBSTILIUM_MUSHROOM_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.BROWNLIKE_SUBSTILIUM_MUSHROOM),
                List.of(PlacementUtils.countExtra(128, 1, 32), InSquarePlacement.spread(), PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT, BiomeFilter.biome()));

        register(context, VIBRION_GROWTH_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.VIBRION_GROWTH),
                List.of(PlacementUtils.countExtra(128, 1f, 16), InSquarePlacement.spread(), PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT, BiomeFilter.biome()));
        register(context, VIBRION_SPOREHOLDER_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.VIBRION_SPOREHOLDER),
                List.of(PlacementUtils.countExtra(64, 0.5f, 32), InSquarePlacement.spread(), PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT, BiomeFilter.biome()));
        register(context, SUBSTILIUM_SPROUTS_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.SUBSTILIUM_SPROUTS),
                List.of(PlacementUtils.countExtra(128, 1f, 16), InSquarePlacement.spread(), PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT, BiomeFilter.biome()));

        register(context, HANGING_VIBRION_VINES, configuredFeatures.getOrThrow(ModConfiguredFeatures.HANGING_VIBRION_VINES), List.of(
                CountPlacement.of(256), InSquarePlacement.spread(), PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT,
                EnvironmentScanPlacement.scanningFor(Direction.UP, BlockPredicate.hasSturdyFace(Direction.DOWN),
                        BlockPredicate.ONLY_IN_AIR_PREDICATE, 32), RandomOffsetPlacement.vertical(ConstantInt.of(-1)), BiomeFilter.biome()));
        register(context, HANGING_VIBRION_GEL, configuredFeatures.getOrThrow(ModConfiguredFeatures.HANGING_VIBRION_GEL), List.of(
                CountPlacement.of(256), InSquarePlacement.spread(), PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT,
                EnvironmentScanPlacement.scanningFor(Direction.UP, BlockPredicate.hasSturdyFace(Direction.DOWN),
                        BlockPredicate.ONLY_IN_AIR_PREDICATE, 32), RandomOffsetPlacement.vertical(ConstantInt.of(-1)), BiomeFilter.biome()));
        register(context, HANGING_LIT_VIBRION_GEL, configuredFeatures.getOrThrow(ModConfiguredFeatures.HANGING_LIT_VIBRION_GEL), List.of(
                CountPlacement.of(256), InSquarePlacement.spread(), PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT,
                EnvironmentScanPlacement.scanningFor(Direction.UP, BlockPredicate.hasSturdyFace(Direction.DOWN),
                        BlockPredicate.ONLY_IN_AIR_PREDICATE, 32), RandomOffsetPlacement.vertical(ConstantInt.of(-1)), BiomeFilter.biome()));

        register(context, OVERGROWN_ENTORIUM_ORE_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.OVERGROWN_ENTORIUM_ORE),
                ModOrePlacement.commonOrePlacement(48, HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(80))));
        register(context, ENTORIUM_ORE_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.ENTORIUM_ORE),
                ModOrePlacement.commonOrePlacement(16, HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(80))));

        register(context, COMPRESSED_SUBSTILIUM_SOIL_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.COMPRESSED_SUBSTILIUM_SOIL),
                ModOrePlacement.commonOrePlacement(48, HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(80))));

        register(context, NATURAL_SPORE_BLASTER_X_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.NATURAL_SPORE_BLASTER_X),
                ModOrePlacement.commonOrePlacement(128, HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(80))));
        register(context, NATURAL_SPORE_BLASTER_Y_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.NATURAL_SPORE_BLASTER_Y),
                ModOrePlacement.commonOrePlacement(128, HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(80))));
        register(context, NATURAL_SPORE_BLASTER_Z_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.NATURAL_SPORE_BLASTER_Z),
                ModOrePlacement.commonOrePlacement(128, HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(80))));

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