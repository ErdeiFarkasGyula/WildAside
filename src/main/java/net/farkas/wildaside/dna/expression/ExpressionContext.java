package net.farkas.wildaside.dna.expression;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class ExpressionContext {
    @Nullable
    private final LivingEntity entity;
    @Nullable
    private final Level level;

    public ExpressionContext(LivingEntity entity) {
        this.entity = entity;
        this.level = entity == null ? null : entity.level();
    }

    public float getHealthPercent() {
        if (entity == null) return 1.0f;
        return entity.getHealth() / entity.getMaxHealth();
    }

    public boolean isOnFire() {
        if (entity == null) return false;
        return entity.isOnFire();
    }

    public boolean isInWater() {
        if (entity == null) return false;
        return entity.isInWater();
    }

    public boolean isInOverworld() {
        if (level == null) return false;
        return level.dimension() == Level.OVERWORLD;
    }

    public boolean isInNether() {
        if (level == null) return false;
        return level.dimension() == Level.NETHER;
    }

    public boolean isInEnd() {
        if (level == null) return false;
        return level.dimension() == Level.END;
    }

    public boolean isNight() {
        if (level == null) return false;
        return !level.isDay();
    }

    public boolean isDay() {
        if (level == null) return true;
        return level.isDay();
    }

    public boolean canSeeSky() {
        if (level == null || entity == null) return true;
        return level.canSeeSky(entity.blockPosition());
    }

    public boolean isInCombat() {
        if (entity == null) return false;
        return entity.getLastHurtByMob() != null && entity.getLastHurtByMobTimestamp() > entity.tickCount - 100;
    }

    public boolean isSprinting() {
        if (entity == null) return false;
        return entity.isSprinting();
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public Level getLevel() {
        return level;
    }
}
