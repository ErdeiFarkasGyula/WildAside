package net.farkas.wildaside.worldgen.feature.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.farkas.wildaside.util.LargeMushroomCapShape;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

import java.util.List;
import java.util.Map;

public record LargeMushroomConfiguration(int minHeight, int maxHeight, int maxBranches,
                                         BlockStateProvider capBlock, BlockStateProvider stemBlock,
                                         BlockStateProvider woodBlock, BlockStateProvider hangingVinesBlock,
                                         List<BlockStateProvider> validBaseBlocks, List<BlockStateProvider> decoratorBlocks,
                                         Map<LargeMushroomCapShape, Float> capShapeWeights) implements FeatureConfiguration {
    public static final Codec<LargeMushroomConfiguration> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("minHeight").forGetter(cfg -> cfg.minHeight),
                    Codec.INT.fieldOf("maxHeight").forGetter(cfg -> cfg.maxHeight),
                    Codec.INT.fieldOf("maxBranches").forGetter(cfg -> cfg.maxBranches),
                    BlockStateProvider.CODEC.fieldOf("capBlock").forGetter(cfg -> cfg.capBlock),
                    BlockStateProvider.CODEC.fieldOf("stemBlock").forGetter(cfg -> cfg.stemBlock),
                    BlockStateProvider.CODEC.fieldOf("woodBlock").forGetter(cfg -> cfg.woodBlock),
                    BlockStateProvider.CODEC.fieldOf("hangingVinesBlock").forGetter(cfg -> cfg.hangingVinesBlock),
                    BlockStateProvider.CODEC.listOf().fieldOf("validBaseBlocks").forGetter(cfg -> cfg.validBaseBlocks),
                    BlockStateProvider.CODEC.listOf().fieldOf("decoratorBlocks").forGetter(cfg -> cfg.decoratorBlocks),
                    Codec.unboundedMap(LargeMushroomCapShape.CODEC, Codec.FLOAT).fieldOf("capShapeWeights").forGetter(cfg -> cfg.capShapeWeights)
            ).apply(instance, LargeMushroomConfiguration::new)
    );
}