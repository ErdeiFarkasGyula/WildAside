package net.farkas.wildaside.entity.custom.vibrion;

import net.farkas.wildaside.entity.ModEntityTypes;
import net.farkas.wildaside.item.ModItems;
import net.farkas.wildaside.particle.ModParticles;
import net.farkas.wildaside.util.ContaminationHandler;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public class SporeArrowEntity extends AbstractArrow {
    public SporeArrowEntity(EntityType<? extends AbstractArrow> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public SporeArrowEntity(Level pLevel, double pX, double pY, double pZ) {
        super(ModEntityTypes.SPORE_ARROW.get(), pX, pY, pZ, pLevel);
    }

    public SporeArrowEntity(Level pLevel, LivingEntity thrower) {
        super(ModEntityTypes.SPORE_ARROW.get(), thrower, pLevel);
    }

    public SporeArrowEntity(Level pLevel) {
        super(ModEntityTypes.SPORE_ARROW.get(), pLevel);
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        if (pResult.getEntity() instanceof LivingEntity entity && !level().isClientSide()) {
            ContaminationHandler.addDose(entity, Math.round(entity.getRandom().nextFloat() + 1) * 500);
        }
        super.onHitEntity(pResult);
    }

    @Override
    protected ItemStack getPickupItem() {
        return new ItemStack(ModItems.SPORE_ARROW.get());
    }

    public void tick() {
        super.tick();
        Level level = this.level();
        if (level.isClientSide && !inGround) {
            level.addParticle(ModParticles.VIBRION_PARTICLE.get(), this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
        }
    }
}
