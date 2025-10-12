package net.farkas.wildaside.entity.ai.hickory;

import net.farkas.wildaside.entity.custom.hickory.HickoryTreantEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class HickoryTreantRootAttackGoal extends Goal {
    private final HickoryTreantEntity entity;
    private static final int minRange = 3;
    private static final int maxRange = 24;

    public HickoryTreantRootAttackGoal(HickoryTreantEntity entity) {
        this.entity = entity;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = entity.getTarget();
        if (target == null || !target.isAlive()) return false;
        double distance = entity.distanceTo(target);
        return entity.rootCooldown <= 0 && distance > minRange && distance <= maxRange;
    }

    @Override
    public void tick() {
        if (entity.level().isClientSide()) return;
        int phase = entity.getPhase();

        entity.doRootingAttack(phase);
        entity.rootCooldown = entity.getRootInterval(phase);
    }
}
