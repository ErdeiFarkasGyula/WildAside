package net.farkas.wildaside.block.custom.vibrion.hanging_vines;

import net.farkas.wildaside.block.ModBlocks;
import net.farkas.wildaside.particle.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.WeepingVinesPlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;

public class HangingVibrionVinesPlantBlock extends WeepingVinesPlantBlock {
    public HangingVibrionVinesPlantBlock(Properties p_154975_) {
        super(p_154975_);
    }

    @Override
    public boolean isLadder(BlockState state, LevelReader level, BlockPos pos, LivingEntity entity) {
        return true;
    }

    @Override
    protected GrowingPlantHeadBlock getHeadBlock() {
        return (GrowingPlantHeadBlock) ModBlocks.HANGING_VIBRION_VINES.get();
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
        return new ItemStack(ModBlocks.HANGING_VIBRION_VINES.get());
    }

    @Override
    public void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {
        super.entityInside(pState, pLevel, pPos, pEntity);
        pLevel.addParticle(ModParticles.VIBRION_PARTICLE.get(), (pPos.getX() + Math.random()), (pPos.getY() + Math.random()), (pPos.getZ() + Math.random()), 0, 0, 0);
    }
}
