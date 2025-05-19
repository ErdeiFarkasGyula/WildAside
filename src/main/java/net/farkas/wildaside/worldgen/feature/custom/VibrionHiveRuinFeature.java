package net.farkas.wildaside.worldgen.feature.custom;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.ReplaceBlockConfiguration;

public class VibrionHiveRuinFeature extends Feature {
    public VibrionHiveRuinFeature(Codec pCodec) {
        super(pCodec);
    }

    @Override
    public boolean place(FeaturePlaceContext context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();

        level.setBlock()
    }
}
