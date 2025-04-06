package net.farkas.wildaside.worldgen.feature.custom;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.ReplaceBlockFeature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ReplaceBlockConfiguration;

public class UpdateReplaceSingleBlockFeature extends ReplaceBlockFeature {
    public UpdateReplaceSingleBlockFeature(Codec<ReplaceBlockConfiguration> p_66651_) {
        super(p_66651_);
    }

    public boolean place(FeaturePlaceContext<ReplaceBlockConfiguration> context) {
        WorldGenLevel worldgenlevel = context.level();
        BlockPos blockpos = context.origin();
        ReplaceBlockConfiguration replaceblockconfiguration = context.config();

        for (OreConfiguration.TargetBlockState oreconfiguration$targetblockstate : replaceblockconfiguration.targetStates) {
            if (oreconfiguration$targetblockstate.target.test(worldgenlevel.getBlockState(blockpos), context.random())) {
                worldgenlevel.setBlock(blockpos, oreconfiguration$targetblockstate.state, 3);
                worldgenlevel.scheduleTick(blockpos, worldgenlevel.getBlockState(blockpos).getBlock(), 10);
                break;
            }
        }

        return true;
    }
}
