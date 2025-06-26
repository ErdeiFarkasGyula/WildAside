package net.farkas.wildaside.block.custom.vibrion;

import net.farkas.wildaside.particle.ModParticles;
import net.farkas.wildaside.util.ContaminationHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.pathfinder.PathType;
import org.jetbrains.annotations.Nullable;

public class SporeAir extends AirBlock {
    private int AGE = 0;
    public static final DirectionProperty FACING = DirectionalBlock.FACING;

    public SporeAir(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {
        ContaminationHandler.giveContaminationDose(pEntity, 20);
        super.entityInside(pState, pLevel, pPos, pEntity);
    }

    @Override
    public @Nullable PathType getBlockPathType(BlockState state, BlockGetter level, BlockPos pos, @Nullable Mob mob) {
        return PathType.DANGER_OTHER;
    }

    @Override
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pMovedByPiston) {
        if (!pLevel.isClientSide) {
            pLevel.scheduleTick(pPos, this, 20);
        }
        super.onPlace(pState, pLevel, pPos, pOldState, pMovedByPiston);
    }

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        AGE += pRandom.nextInt(1, 2);
        if (AGE > 4) pLevel.removeBlock(pPos, false);

        pLevel.scheduleTick(pPos, this, 20);

        super.tick(pState, pLevel, pPos, pRandom);
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        for (int i = 0; i < pRandom.nextInt(5, 10); i++) {
            pLevel.addParticle(ModParticles.VIBRION_PARTICLE.get(), (pPos.getX() + pRandom.nextFloat()), (pPos.getY() + pRandom.nextFloat()), (pPos.getZ() + pRandom.nextFloat()), 0, 0, 0);
        }
        super.animateTick(pState, pLevel, pPos, pRandom);
    }
}
