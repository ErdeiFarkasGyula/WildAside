package net.farkas.wildaside.block.custom;

import net.farkas.wildaside.block.ModBlocks;
import net.farkas.wildaside.particle.ModParticles;
import net.farkas.wildaside.particle.ParticleUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Math;

public class GlowingSaplingBlock extends SaplingBlock {
    public static final IntegerProperty STAGE = BlockStateProperties.STAGE;
    protected static final float AABB_OFFSET = 6.0F;
    protected static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 12.0D, 14.0D);

    private static final int minLight = 0;
    private static final int maxLight = 7;
    public static final IntegerProperty LIGHT = IntegerProperty.create("light", minLight, maxLight);

    public GlowingSaplingBlock(AbstractTreeGrower pTreeGrower, Properties pProperties) {
        super(pTreeGrower, pProperties.lightLevel(s -> s.getValue(LIGHT)));
        this.registerDefaultState(this.stateDefinition.any().setValue(LIGHT, 0).setValue(STAGE, Integer.valueOf(0)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(STAGE, LIGHT);
    }

    @Override
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pMovedByPiston) {
        pLevel.scheduleTick(pPos, this, 0);
        super.onPlace(pState, pLevel, pPos, pOldState, pMovedByPiston);
    }

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        super.tick(pState, pLevel, pPos, pRandom);

        int time = (int)pLevel.dayTime();

        if (time > 22000) {
            int lightLevel = Math.round(7 - (maxLight * ((time - 22000f) / 2000f)));
            if (lightLevel < minLight) lightLevel = minLight;
            pLevel.setBlockAndUpdate(pPos, pLevel.getBlockState(pPos).setValue(LIGHT, lightLevel));
        } else
        if (time > 12000 && time < 14000) {
            int lightLevel = Math.round(maxLight * ((time - 12000f) / 2000f));
            if (lightLevel > maxLight) lightLevel = maxLight;
            pLevel.setBlockAndUpdate(pPos, pLevel.getBlockState(pPos).setValue(LIGHT, lightLevel));
        } else
        if (time > 14000) {
            pLevel.setBlockAndUpdate(pPos, pLevel.getBlockState(pPos).setValue(LIGHT, 7));
        } else
        if (time < 12000) {
            pLevel.setBlockAndUpdate(pPos, pLevel.getBlockState(pPos).setValue(LIGHT, 0));
        }

        pLevel.scheduleTick(pPos, this, pRandom.nextInt(10, 20));

    }
}
