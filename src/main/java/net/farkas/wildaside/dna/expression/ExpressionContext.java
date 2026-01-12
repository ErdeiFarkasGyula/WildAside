package net.farkas.wildaside.dna.expression;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class ExpressionContext {
    private final LivingEntity entity;
    private final Level level;

    public ExpressionContext(LivingEntity entity) {
        this.entity = entity;
        this.level = entity.level();
    }

    public float getHealthPercent() {
        return entity.getHealth() / entity.getMaxHealth();
    }

    public boolean isOnFire() {
        return entity.isOnFire();
    }

    public boolean isInWater() {
        return entity.isInWater();
    }

    public boolean isInNether() {
        return level.dimension() == Level.NETHER;
    }

    public boolean isInEnd() {
        return level.dimension() == Level.END;
    }

    public boolean isNight() {
        return !level.isDay();
    }

    public boolean isDay() {
        return level.isDay();
    }

    public boolean canSeeSky() {
        return level.canSeeSky(entity.blockPosition());
    }

    public boolean isInCombat() {
        return entity.getLastHurtByMob() != null &&
               entity.getLastHurtByMobTimestamp() > entity.tickCount - 100;
    }

    public boolean isSprinting() {
        return entity.isSprinting();
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public Level getLevel() {
        return level;
    }
}
