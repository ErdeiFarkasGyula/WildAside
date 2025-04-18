package net.farkas.wildaside.entity.custom;

import net.farkas.wildaside.block.ModBlocks;
import net.farkas.wildaside.entity.ModEntities;
import net.farkas.wildaside.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class SporeBombEntity extends ThrowableItemProjectile {
    public SporeBombEntity(EntityType<? extends ThrowableItemProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public SporeBombEntity(Level pLevel) {
        super(ModEntities.SPORE_BOMB.get(), pLevel);
    }

    public SporeBombEntity(Level pLevel, LivingEntity livingEntity) {
        super(ModEntities.SPORE_BOMB.get(), livingEntity, pLevel);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.SPORE_BOMB.get();
    }


    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        if (!this.level().isClientSide) {
            pResult.getEntity().hurt(damageSources().thrown(this, this.getOwner()), 4.0F);
            spawnSporeAir(pResult.getEntity().level(), pResult.getEntity().blockPosition(), 1);
            this.discard();
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        Level level = this.level();
        if (!level.isClientSide()) {
            level.broadcastEntityEvent(this, ((byte)3));
            BlockPos position = this.blockPosition();
            spawnSporeAir(level, position, 2);
        }

        this.discard();
        super.onHitBlock(pResult);

    }

    private void spawnSporeAir(Level level, BlockPos position, int r) {
        for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
                for (int z = -r; z <= r; z++) {
                    boolean absX = Math.abs(x) == r;
                    boolean absY = Math.abs(y) == r;
                    boolean absZ = Math.abs(z) == r;

                    if (!((absX && absZ) || (absY && (absX || absZ)))) {
                        BlockPos pos = position.offset(x, y, z);
                        if (level.isEmptyBlock(pos)) {
                            level.setBlockAndUpdate(pos, ModBlocks.SPORE_AIR.get().defaultBlockState());
                            level.scheduleTick(pos, level.getBlockState(pos).getBlock(), 20);
                        }
                    }
                }
            }
        }
    }
}
