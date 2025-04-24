package net.farkas.wildaside.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
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
        int currentLight = pLevel.getBlockState(pPos).getValue(LIGHT);
        int newLight = 0;

        if (time > 22000) {
            newLight = Math.round(7 - (maxLight * ((time - 22000f) / 2000f)));
        } else
            if (time > 12000 && time < 14000) {
                newLight = Math.round(maxLight * ((time - 12000f) / 2000f));
            } else
                if (time > 14000) {
                    newLight = 7;
                } else
                    if (time < 12000) {
                        newLight = 0;
                    }

        newLight = Math.min(Math.max(0, newLight), 7);

        if (newLight != currentLight) {
            pLevel.setBlockAndUpdate(pPos, pLevel.getBlockState(pPos).setValue(LIGHT, newLight));
        }

        pLevel.scheduleTick(pPos, this, 100);

    }
}
