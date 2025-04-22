package net.farkas.wildaside.block.custom.vibrion;

import net.farkas.wildaside.block.ModBlocks;
import net.farkas.wildaside.particle.ModParticles;
import net.farkas.wildaside.util.ContaminationHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class NaturalSporeBlaster extends RotatedPillarBlock {
    private final int maxTimer = 40;
    private int changePowerTimer = maxTimer;
    private int power1 = 0;
    private int power2 = 0;
    BlockState spore_air = ModBlocks.SPORE_AIR.get().defaultBlockState();

    public NaturalSporeBlaster(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction) {
        return true;
    }

    @Override
    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pNeighborBlock, BlockPos pNeighborPos, boolean pMovedByPiston) {
        if (!pLevel.isClientSide) {
            pLevel.scheduleTick(pPos, this, 2);
        }
        super.neighborChanged(pState, pLevel, pPos, pNeighborBlock, pNeighborPos, pMovedByPiston);
    }

    @Override
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pMovedByPiston) {
        if (!pLevel.isClientSide) {
            pLevel.scheduleTick(pPos, this, 2);
        }
        super.onPlace(pState, pLevel, pPos, pOldState, pMovedByPiston);
    }

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        changePowerTimer++;
        if (changePowerTimer >= maxTimer) {
            changePowerTimer = 0;
            power1 = pRandom.nextIntBetweenInclusive(0, 15);
            power2 = pRandom.nextIntBetweenInclusive(0, 15);
        }

        int x = pLevel.getBlockState(pPos).getValue(AXIS).equals(Direction.Axis.X) ? 1 : 0;
        int y = pLevel.getBlockState(pPos).getValue(AXIS).equals(Direction.Axis.Y) ? 1 : 0;
        int z = pLevel.getBlockState(pPos).getValue(AXIS).equals(Direction.Axis.Z) ? 1 : 0;

        infectAlongLine(pLevel, pPos, pRandom, power1, x, y, z);
        infectAlongLine(pLevel, pPos, pRandom, power2, -x, -y, -z);
        pLevel.scheduleTick(pPos, this, 2);
        super.tick(pState, pLevel, pPos, pRandom);
    }

    private void infectAlongLine(ServerLevel world, BlockPos origin, RandomSource random, int power, int x, int y, int z) {
        for (int i = 1; i <= power; i++) {
            var position = origin.offset(x * i, y * i, z * i);
            var nextBlock = world.getBlockState(position);

            if (nextBlock.isCollisionShapeFullBlock(world, position)) break;

            AABB area = new AABB(position);
            List<LivingEntity> hits = world.getEntitiesOfClass(LivingEntity.class, area, e -> !e.isSpectator());

            SimpleParticleType particle = ModParticles.VIBRION_PARTICLE.get();

            for (int j = 0; j < 2; j++) {
                double particleX = position.getX() + random.nextDouble();
                double particleY = position.getY() + random.nextDouble();
                double particleZ = position.getZ() + random.nextDouble();
                world.sendParticles(particle, particleX, particleY, particleZ,1,
                        0.02 * x,
                        0.02 * y,
                        0.02 * z,
                        0.0
                );
            }

            for (LivingEntity entity : hits) {
                ContaminationHandler.applyContamination(entity, 20);
                world.sendParticles(particle,
                        entity.getX(), entity.getY() + 0.5, entity.getZ(),
                        5, 0.2, 0.2, 0.2, 0.01);
            }
        }
    }
}
