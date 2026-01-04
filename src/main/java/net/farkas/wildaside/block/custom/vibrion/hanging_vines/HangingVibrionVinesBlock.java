package net.farkas.wildaside.block.custom.vibrion.hanging_vines;

import net.farkas.wildaside.block.ModBlocks;
import net.farkas.wildaside.particle.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeepingVinesBlock;
import net.minecraft.world.level.block.state.BlockState;

public class HangingVibrionVinesBlock extends WeepingVinesBlock {
    public HangingVibrionVinesBlock(Properties p_154966_) {
        super(p_154966_);
    }

    @Override
    public boolean isLadder(BlockState state, LevelReader level, BlockPos pos, LivingEntity entity) {
        return true;
    }

    @Override
    protected Block getBodyBlock() {
        return ModBlocks.HANGING_VIBRION_VINES_PLANT.get();
    }

    @Override
    public void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {
        super.entityInside(pState, pLevel, pPos, pEntity);
        pLevel.addParticle(ModParticles.VIBRION_PARTICLE.get(), (pPos.getX() + Math.random()), (pPos.getY() + Math.random()), (pPos.getZ() + Math.random()), 0, 0, 0);
    }
}
