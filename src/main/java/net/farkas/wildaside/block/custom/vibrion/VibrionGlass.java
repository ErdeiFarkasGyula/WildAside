package net.farkas.wildaside.block.custom.vibrion;

import net.farkas.wildaside.util.ModTags;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.state.BlockState;

public class VibrionGlass extends TransparentBlock {
    public VibrionGlass(Properties p_53640_) {
        super(p_53640_);
    }

    @Override
    public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
        return adjacentBlockState.is(ModTags.Blocks.VIBRION_FULL_GLASSES) || super.skipRendering(state, adjacentBlockState, side);
    }
}
