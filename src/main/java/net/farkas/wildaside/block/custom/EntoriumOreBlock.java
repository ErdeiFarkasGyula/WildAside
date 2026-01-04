package net.farkas.wildaside.block.custom;

import net.farkas.wildaside.particle.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.IPlantable;

public class EntoriumOreBlock extends Block {
    public EntoriumOreBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        pLevel.addParticle(ModParticles.ENTORIUM_PARTICLE.get(), pPos.getX() + Math.random(), pPos.getY() + 1, pPos.getZ() + Math.random(), 0, 0, 0);
        super.animateTick(pState, pLevel, pPos, pRandom);
    }

    @Override
    public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, IPlantable plantable) {
        return true;
    }
}
