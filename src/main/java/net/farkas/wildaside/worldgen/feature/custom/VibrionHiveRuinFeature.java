package net.farkas.wildaside.worldgen.feature.custom;

import com.mojang.serialization.Codec;
import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public class VibrionHiveRuinFeature extends Feature {
    public VibrionHiveRuinFeature(Codec pCodec) {
        super(pCodec);
    }

    @Override
    public boolean place(FeaturePlaceContext context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();

        if (level.isEmptyBlock(origin) && level.getBlockState(origin.below()).is(ModBlocks.SUBSTILIUM_SOIL.get())) {
            level.setBlock(origin, Blocks.CHEST.defaultBlockState(), 3);
            if (level.getBlockEntity(origin) instanceof ChestBlockEntity chest) {
                ResourceLocation lootTable = new ResourceLocation(WildAside.MOD_ID, "chests/vibrion_hive_chest");
                chest.setLootTable(lootTable, context.random().nextLong());
            }

            return true;
        }


        return false;
    }
}
