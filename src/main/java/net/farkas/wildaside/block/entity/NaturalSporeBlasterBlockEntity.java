package net.farkas.wildaside.block.entity;

import net.farkas.wildaside.particle.ModParticles;
import net.farkas.wildaside.util.BlasterUtil;
import net.farkas.wildaside.util.BlasterUtils;
import net.farkas.wildaside.util.ContaminationHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.phys.AABB;
import net.minecraft.nbt.CompoundTag;
import java.util.List;
import net.minecraft.core.Direction;

public class NaturalSporeBlasterBlockEntity extends BlasterBlockEntity {
    private static final int MAX_TIMER = 40;
    private int changePowerTimer = MAX_TIMER;
    private int power1 = 0;
    private int power2 = 0;

    public NaturalSporeBlasterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.NATURAL_SPORE_BLASTER.get(), pos, state);
    }

    public void tickServer() {
        Level level = getLevel();
        if (!(level instanceof ServerLevel world)) return;
        RandomSource rand = world.getRandom();

        if (++changePowerTimer >= MAX_TIMER) {
            changePowerTimer = 0;
            power1 = rand.nextFloat() > 0.25 ? rand.nextIntBetweenInclusive(0, 15) : 0;
            power2 = rand.nextFloat() > 0.25 ? rand.nextIntBetweenInclusive(0, 15) : 0;
        }

        Direction.Axis axis = getBlockState().getValue(RotatedPillarBlock.AXIS);
        int dx = axis == Direction.Axis.X ? 1 : 0;
        int dy = axis == Direction.Axis.Y ? 1 : 0;
        int dz = axis == Direction.Axis.Z ? 1 : 0;

        infectAlongLine(world, worldPosition, rand, power1, dx, dy, dz);
        infectAlongLine(world, worldPosition, rand, power2, -dx, -dy, -dz);

        world.scheduleTick(worldPosition, getBlockState().getBlock(), 2);
    }

    private void infectAlongLine(ServerLevel world, BlockPos origin, RandomSource random, int power, int x, int y, int z) {
        SimpleParticleType particle = ModParticles.VIBRION_PARTICLE.get();
        for (int i = 1; i <= power; i++) {
            if (shouldBreakNext) {
                shouldBreakNext = false;
                break;
            }

            BlockPos pos = origin.offset(x * i, y * i, z * i);
            BlockState next = world.getBlockState(pos);

            if (next.isCollisionShapeFullBlock(world, pos)) break;
            if (!BlasterUtils.canTraverse(Direction.fromDelta(x * i, y * i, z * i), next, world.getBlockState(origin), this)) break;

            world.sendParticles(particle,
                    pos.getX() + random.nextDouble(),
                    pos.getY() + random.nextDouble(),
                    pos.getZ() + random.nextDouble(),
                    1, x * 0.02, y * 0.02, z * 0.02, 0.0);

            List<LivingEntity> hits = world.getEntitiesOfClass(LivingEntity.class, new AABB(pos), e -> !e.isSpectator());
            for (LivingEntity e : hits) {
                ContaminationHandler.addDose(e, 50);
                world.sendParticles(particle, e.getX(), e.getY() + 0.5, e.getZ(), 5, 0.2, 0.2, 0.2, 0.01);
            }
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        changePowerTimer = tag.getInt("changeTimer");
        power1 = tag.getInt("power1");
        power2 = tag.getInt("power2");
        shouldBreakNext = tag.getBoolean("shouldBreakNext");
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("changeTimer", changePowerTimer);
        tag.putInt("power1", power1);
        tag.putInt("power2", power2);
        tag.putBoolean("shouldBreakNext", shouldBreakNext);
    }
}
