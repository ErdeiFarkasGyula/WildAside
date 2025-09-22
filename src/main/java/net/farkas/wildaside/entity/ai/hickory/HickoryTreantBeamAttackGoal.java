package net.farkas.wildaside.entity.ai.hickory;

import net.farkas.wildaside.entity.custom.hickory.HickoryTreantEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class HickoryTreantBeamAttackGoal extends Goal {
    private final HickoryTreantEntity entity;
    private static final int minRange = 3;
    private static final int maxRange = 24;

    public HickoryTreantBeamAttackGoal(HickoryTreantEntity entity) {
        this.entity = entity;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = entity.getTarget();
        if (target == null || !target.isAlive()) return false;
        double distance = entity.distanceTo(target);
        return entity.beamCooldown <= 0 && distance > minRange && distance <= maxRange;
    }

    @Override
    public void tick() {
        int phase = entity.getPhase();

        entity.doBeamAttack(phase);
        entity.beamCooldown = entity.getBeamInterval(phase);
    }
}
