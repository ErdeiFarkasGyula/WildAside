package net.farkas.wildaside.worldgen.feature.tree.hickory;

import net.farkas.wildaside.util.HickoryColour;
import net.farkas.wildaside.worldgen.ModConfiguredFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import org.jetbrains.annotations.Nullable;

public class HickoryTreeGrower extends AbstractTreeGrower {
    private final HickoryColour colour;

    public HickoryTreeGrower(HickoryColour colour) {
        this.colour = colour;
    }

    @Override
    protected @Nullable ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource randomSource, boolean b) {
        return ModConfiguredFeatures.HICKORY_TREES.get(colour);
    }
}
