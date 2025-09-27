package net.farkas.wildaside.worldgen.feature.custom;

import com.mojang.serialization.Codec;
import net.farkas.wildaside.block.ModBlocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;

public class HickoryBushFeature extends Feature<SimpleBlockConfiguration> {
    public HickoryBushFeature(Codec<SimpleBlockConfiguration> pCodec) {
        super(pCodec);
    }

    @Override
    public boolean place(FeaturePlaceContext<SimpleBlockConfiguration> context) {
        var level = context.level();
        var pos = context.origin();

        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                if (!(level.getBlockState(pos.offset(x, -1, z)).isSolid())) {
                    return false;
                }
            }
        }

        BlockState leaves = context.config().toPlace().getState(context.random(), pos);

        level.setBlock(pos, ModBlocks.HICKORY_LOG.get().defaultBlockState(), 3);

        var random = context.random();

        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                if (x == 0 || z == 0 || random.nextFloat() < 0.5f) {
                    var newPos = pos.offset(x, 0, z);
                    if (!level.getBlockState(newPos).isSolid()) {
                        level.setBlock(newPos, leaves, 3);
                        level.scheduleTick(newPos, level.getBlockState(newPos).getBlock(), 0);
                    }
                }
            }
        }

        var topPos = pos.above();
        if (!level.getBlockState(topPos).isSolid()) {
            level.setBlock(topPos, leaves, 3);
            level.scheduleTick(topPos, level.getBlockState(topPos).getBlock(), 0);

        }

        return true;
    }
}
