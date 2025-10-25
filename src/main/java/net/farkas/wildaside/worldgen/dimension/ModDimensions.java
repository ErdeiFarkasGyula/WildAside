package net.farkas.wildaside.worldgen.dimension;

import com.ibm.icu.impl.Pair;
import net.farkas.wildaside.WildAside;
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
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.flat.FlatLayerInfo;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorPresets;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;

import java.util.List;
import java.util.Optional;
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

        FlatLevelGeneratorSettings generatorSettings = new FlatLevelGeneratorSettings(
                Optional.empty(),
                biomes.getOrThrow(Biomes.PLAINS),
                List.of()
        );

        generatorSettings.getLayersInfo().add(new FlatLayerInfo(1, Blocks.DIRT));

        var generator = new FlatLevelSource(generatorSettings);

        LevelStem stem = new LevelStem(dimTypes.getOrThrow(TEST_DIMENSION_TYPE), generator);

        context.register(TEST_LEVEL_STEM, stem);
    }
}