package net.farkas.wildaside.worldgen.dimension;

import net.farkas.wildaside.WildAside;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.*;

import java.util.Collections;
import java.util.OptionalLong;

public class ModDimensions {
    public static final ResourceKey<LevelStem> TEST_LEVEL_STEM = ResourceKey.create(Registries.LEVEL_STEM,
            new ResourceLocation(WildAside.MOD_ID, "test_level"));
    public static final ResourceKey<Level> TEST_LEVEL = ResourceKey.create(Registries.DIMENSION,
            new ResourceLocation(WildAside.MOD_ID, "test_level"));
    public static final ResourceKey<DimensionType> TEST_DIMENSION_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE,
            new ResourceLocation(WildAside.MOD_ID, "test_dimension"));


    public static void bootstrapType(BootstapContext<DimensionType> context) {
        context.register(TEST_DIMENSION_TYPE, new DimensionType(
                OptionalLong.of(18000), // fixedTime
                false, // hasSkylight
                false, // hasCeiling
                false, // ultraWarm
                false, // natural
                1.0, // coordinateScale
                false, // bedWorks
                false, // respawnAnchorWorks
                0, // minY
                16, // height
                16, // logicalHeight
                BlockTags.INFINIBURN_OVERWORLD, // infiniburn
                BuiltinDimensionTypes.OVERWORLD_EFFECTS, // effectsLocation
                1.0f, // ambientLight
                new DimensionType.MonsterSettings(false, false, ConstantInt.of(0), 0)));
    }

    public static void bootstrapStem(BootstapContext<LevelStem> context) {
        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
        HolderGetter<DimensionType> dimTypes = context.lookup(Registries.DIMENSION_TYPE);

        NoiseSettings noiseSettings = new NoiseSettings(
                0, 16, 1, 2
        );

        NoiseRouter router = new NoiseRouter(
                DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(),
                DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(),
                DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(),
                DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(),
                DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero()
        );

        NoiseGeneratorSettings generatorSettings = new NoiseGeneratorSettings(
                noiseSettings,
                Blocks.AIR.defaultBlockState(), // base block
                Blocks.AIR.defaultBlockState(), // fluid
                router,
                SurfaceRules.sequence(
                        SurfaceRules.ifTrue(SurfaceRules.abovePreliminarySurface(),
                                SurfaceRules.state(Blocks.AIR.defaultBlockState()))
                ),
                Collections.emptyList(),
                1, // seaLevel
                true, // disableMobGeneration
                false, // aquifersEnabled
                false, // oreVeinsEnabled
                false  // useLegacyRandomSource
        );

        // Wrap in a Noise-based chunk generator
        NoiseBasedChunkGenerator generator = new NoiseBasedChunkGenerator(
                new FixedBiomeSource(biomes.getOrThrow(Biomes.THE_VOID)),
                Holder.direct(generatorSettings)
        );

        LevelStem stem = new LevelStem(dimTypes.getOrThrow(TEST_DIMENSION_TYPE), generator);

        context.register(TEST_LEVEL_STEM, stem);
    }
}