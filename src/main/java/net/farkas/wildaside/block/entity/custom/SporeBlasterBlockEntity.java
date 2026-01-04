package net.farkas.wildaside.block.entity.custom;

import net.farkas.wildaside.block.custom.vibrion.SporeBlasterBlock;
import net.farkas.wildaside.block.entity.ModBlockEntities;
import net.farkas.wildaside.particle.ModParticles;
import net.farkas.wildaside.util.BlasterUtils;
import net.farkas.wildaside.util.ContaminationHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class SporeBlasterBlockEntity extends BlasterBlockEntity {
    private int power = 0;

    public SporeBlasterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SPORE_BLASTER.get(), pos, state);
    }

    public void tickServer() {
        Level level = getLevel();
        int power = level.getBestNeighborSignal(worldPosition);
        if (power > 0) {
            infectAlongLine((ServerLevel) level, worldPosition, getBlockState().getValue(SporeBlasterBlock.FACING), power, level.random);
        }
        level.scheduleTick(worldPosition, getBlockState().getBlock(), 2);
    }

    private void infectAlongLine(ServerLevel world, BlockPos origin, Direction dir, int range, RandomSource rand) {
        SimpleParticleType particle = ModParticles.VIBRION_PARTICLE.get();
        for (int i = 1; i <= range; i++) {
            if (shouldBreakNext) {
                shouldBreakNext = false;
                break;
            }

            BlockState originBlock = world.getBlockState(origin);
            BlockPos step = origin.relative(dir, i);
            BlockState nextBlock = world.getBlockState(step);

            if (world.getBlockState(step).isCollisionShapeFullBlock(world, step)) break;
            if (!BlasterUtils.canTraverse(dir, nextBlock, originBlock, this)) break;

            double x = step.getX() + rand.nextDouble();
            double y = step.getY() + rand.nextDouble();
            double z = step.getZ() + rand.nextDouble();
            world.sendParticles(particle, x, y, z,1,
                    0.02 * dir.getStepX(),
                    0.02 * dir.getStepY(),
                    0.02 * dir.getStepZ(),
                    0.0);

            List<LivingEntity> hits = world.getEntitiesOfClass(LivingEntity.class, new AABB(step), e -> !e.isSpectator());
            for (LivingEntity entity : hits) {
                ContaminationHandler.addDose(entity, 50);
                world.sendParticles(particle, entity.getX(), entity.getY() + 0.5, entity.getZ(), 5, 0.2, 0.2, 0.2, 0.01);
            }
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        power = tag.getInt("power");
        shouldBreakNext = tag.getBoolean("shouldBreakNext");
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("power", power);
        tag.putBoolean("shouldBreakNext", shouldBreakNext);
    }
}
