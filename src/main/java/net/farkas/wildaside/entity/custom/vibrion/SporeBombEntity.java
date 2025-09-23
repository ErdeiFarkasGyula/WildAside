package net.farkas.wildaside.entity.custom.vibrion;

import net.farkas.wildaside.entity.ModEntities;
import net.farkas.wildaside.item.ModItems;
import net.farkas.wildaside.particle.ModParticles;
import net.farkas.wildaside.util.AdvancementHandler;
import net.farkas.wildaside.util.ContaminationHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;

import java.util.List;

public class SporeBombEntity extends ThrowableItemProjectile {
    private final float charge;

    public SporeBombEntity(EntityType<? extends ThrowableItemProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.charge = 0;
    }

    public SporeBombEntity(Level pLevel) {
        super(ModEntities.SPORE_BOMB.get(), pLevel);
        this.charge = 0;
    }

    public SporeBombEntity(Level pLevel, LivingEntity livingEntity, float charge) {
        super(ModEntities.SPORE_BOMB.get(), livingEntity, pLevel);
        this.charge = charge;
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.SPORE_BOMB.get();
    }


    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        Level level = this.level();
        if (!level.isClientSide) {
            pResult.getEntity().hurt(damageSources().thrown(this, this.getOwner()), 2f);
            applySporeCloud((ServerLevel)level, pResult.getEntity().blockPosition(), charge);
            this.discard();
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        Level level = this.level();
        if (!level.isClientSide) {
            level.broadcastEntityEvent(this, ((byte)3));
            BlockPos position = this.blockPosition();
            applySporeCloud((ServerLevel)level, position, charge);
        }

        this.discard();
        super.onHitBlock(pResult);
    }

    private void applySporeCloud(ServerLevel level, BlockPos center, float charge) {
        int radius = Mth.ceil(1 + charge * 4);
        RandomSource random = level.getRandom();
        SimpleParticleType particle = ModParticles.VIBRION_PARTICLE.get();

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (dx * dx + dy * dy + dz * dz <= radius * radius) {
                        for (int i = 0; i < 3; i++) {
                            double x = center.getX() + 0.5 + dx + (random.nextDouble() - 0.5);
                            double y = center.getY() + 0.5 + dy + (random.nextDouble() - 0.5);
                            double z = center.getZ() + 0.5 + dz + (random.nextDouble() - 0.5);
                            level.sendParticles(particle, x, y, z,
                                    1, 0, 0, 0, 0.0);
                        }
                    }
                }
            }
        }

        AABB box = new AABB(center).inflate(radius);
        List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, box, e -> !e.isSpectator());

        int entityCount = 0;

        for (LivingEntity entity : list) {
            entityCount++;
            ContaminationHandler.addDose(entity, Math.round((charge + random.nextFloat()) * 1000));
            level.sendParticles(particle,
                    entity.getX(), entity.getY() + 0.5, entity.getZ(),
                    5, 0.2, 0.2, 0.2, 0.01);
        }

        if (entityCount >= 5) {
            if (this.getOwner() instanceof ServerPlayer serverPlayer) {
                AdvancementHandler.givePlayerAdvancement(serverPlayer, "weapons_of_mass_infection");
            }
        }
    }
}
