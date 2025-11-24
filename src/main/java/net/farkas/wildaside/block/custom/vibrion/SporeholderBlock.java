package net.farkas.wildaside.block.custom.vibrion;

import net.farkas.wildaside.particle.ModParticles;
import net.farkas.wildaside.util.ContaminationHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public class SporeholderBlock extends SaplingBlock {
    public SporeholderBlock(AbstractTreeGrower pTreeGrower, Properties pProperties) {
        super(pTreeGrower, pProperties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        Vec3 offset = state.getOffset(world, pos);
        return box(2, 0, 2, 14, 2, 14).move(offset.x, 0, offset.z);
    }

    @Override
    public void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {
        super.entityInside(pState, pLevel, pPos, pEntity);
        pLevel.addParticle(ModParticles.VIBRION_PARTICLE.get(), (pPos.getX() + 0.5), pPos.getY(), (pPos.getZ() + 0.5), 0, 0, 0);
        if (!pLevel.isClientSide()) {
            ContaminationHandler.addDose(pEntity, 40);
        }
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
        if (!pLevel.isClientSide()) {
            applySporeCloud((ServerLevel) pLevel, pPos);
        }
    }

    private void applySporeCloud(ServerLevel level, BlockPos center) {
        int radius = 2;
        RandomSource rand = level.getRandom();
        SimpleParticleType particle = ModParticles.VIBRION_PARTICLE.get();

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (dx * dx + dy * dy + dz * dz <= radius * radius) {
                        for (int i = 0; i < 3; i++) {
                            double x = center.getX() + 0.5 + dx + (rand.nextDouble() - 0.5);
                            double y = center.getY() + 0.5 + dy + (rand.nextDouble() - 0.5);
                            double z = center.getZ() + 0.5 + dz + (rand.nextDouble() - 0.5);
                            level.sendParticles(particle, x, y, z,
                                    1, 0, 0, 0, 0.0);
                        }
                    }
                }
            }
        }

        AABB box = new AABB(center).inflate(radius);
        List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, box, e -> !e.isSpectator());

        for (LivingEntity entity : list) {
            ContaminationHandler.addDose(entity, 1000 + rand.nextInt(0, 1000));
            level.sendParticles(particle,
                    entity.getX(), entity.getY() + 0.5, entity.getZ(),
                    5, 0.2, 0.2, 0.2, 0.01);
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextFloat() < 0.5) return;
        level.addParticle(ModParticles.VIBRION_PARTICLE.get(), pos.getX() + random.nextFloat(), pos.getY() + random.nextFloat() / 5, pos.getZ() + random.nextFloat(), 0, 0, 0);
    }
}
