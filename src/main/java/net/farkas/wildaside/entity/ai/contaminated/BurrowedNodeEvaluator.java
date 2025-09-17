package net.farkas.wildaside.entity.ai.contaminated;

import net.farkas.wildaside.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.FlyNodeEvaluator;

public class BurrowedNodeEvaluator extends FlyNodeEvaluator {
    @Override
    public BlockPathTypes getBlockPathType(BlockGetter pLevel, int pX, int pY, int pZ) {
        BlockState state = level.getBlockState(new BlockPos(pX, pY, pZ));
        if (state.is(ModBlocks.SUBSTILIUM_SOIL.get())) {
            return BlockPathTypes.OPEN;
        }
        return BlockPathTypes.BLOCKED;
    }

    public BlockPathTypes getBlockPathType(BlockGetter level, int x, int y, int z, Mob mob) {
        return getBlockPathType(level, x, y, z);
    }
}
