package net.farkas.wildaside.worldgen.feature.custom;

import com.mojang.serialization.Codec;
import net.farkas.wildaside.block.ModBlocks;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.List;

public class GlowingHickoryBushFeature extends Feature<NoneFeatureConfiguration> {
    public GlowingHickoryBushFeature(Codec<NoneFeatureConfiguration> pCodec) {
        super(pCodec);
    }

    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> pContext) {
        var level = pContext.level();
        var pos = pContext.origin();

        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                if (!(level.getBlockState(pos.offset(x, -1, z)).isSolid())) {
                    return false;
                }
            }
        }

        var random = pContext.random();

        var leavesOptions = List.of(
                ModBlocks.RED_GLOWING_HICKORY_LEAVES,
                ModBlocks.BROWN_GLOWING_HICKORY_LEAVES,
                ModBlocks.YELLOW_GLOWING_HICKORY_LEAVES,
                ModBlocks.GREEN_GLOWING_HICKORY_LEAVES);

        var leaves = leavesOptions.get(random.nextInt(leavesOptions.size())).get().defaultBlockState();

        level.setBlock(pos, ModBlocks.HICKORY_LOG.get().defaultBlockState(), 3);

        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                if (x == 0 || z == 0 || random.nextFloat() < 0.25f) {
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
