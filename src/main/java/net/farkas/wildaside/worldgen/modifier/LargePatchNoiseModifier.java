package net.farkas.wildaside.worldgen.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.farkas.wildaside.config.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;

import java.util.List;
import java.util.stream.Stream;

public class LargePatchNoiseModifier extends PlacementModifier {
    private static final PerlinSimplexNoise NOISE = new PerlinSimplexNoise(RandomSource.create(Config.HICKORY_COLOUR_NOISE_SEED.get()), List.of(0));

    private final double scale;
    private final float min;
    private final float max;

    public static final Codec<LargePatchNoiseModifier> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.DOUBLE.fieldOf("scale").forGetter(m -> m.scale),
                    Codec.FLOAT.fieldOf("min").forGetter(m -> m.min),
                    Codec.FLOAT.fieldOf("max").forGetter(m -> m.max)
            ).apply(instance, LargePatchNoiseModifier::new));

    public LargePatchNoiseModifier(double scale, float min, float max) {
        this.scale = scale;
        this.min = min;
        this.max = max;
    }

    @Override
    public Stream<BlockPos> getPositions(PlacementContext context, RandomSource random, BlockPos pos) {
        double n = NOISE.getValue(pos.getX() * scale, pos.getZ() * scale, false);
        return (n >= min && n < max) ? Stream.of(pos) : Stream.empty();
    }

    @Override
    public PlacementModifierType<?> type() {
        return ModPlacementModifiers.LARGE_PATCH_NOISE.get();
    }
}