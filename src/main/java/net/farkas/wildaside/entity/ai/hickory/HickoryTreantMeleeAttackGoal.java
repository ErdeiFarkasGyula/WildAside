package net.farkas.wildaside.entity.ai.hickory;

import net.farkas.wildaside.entity.custom.hickory.HickoryTreantEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

import java.util.EnumSet;

public class HickoryTreantMeleeAttackGoal extends MeleeAttackGoal {
    private final HickoryTreantEntity entity;
    private static final int maxRange = 3;

    public HickoryTreantMeleeAttackGoal(PathfinderMob pMob, double pSpeedModifier, boolean pFollowingTargetEvenIfNotSeen) {
        super(pMob, pSpeedModifier, pFollowingTargetEvenIfNotSeen);
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
        this.entity = (HickoryTreantEntity) mob;
    }

    @Override
    public boolean canUse() {
        LivingEntity target = entity.getTarget();
        if (target == null || !target.isAlive()) return false;
        double distance = entity.distanceTo(target);
//        return distance >= 0 && distance <= maxRange;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return canUse();
    }

    //    @Override
//    protected void checkAndPerformAttack(LivingEntity enemy, double distSqr) {
//        double reach = this.mob.getBbWidth() * 2.0F + enemy.getBbWidth();
//        if (distSqr <= reach * reach) {
//            if (this.mob instanceof HickoryTreantEntity entity) {
//                entity.doRootingSlam(enemy);
//                this.resetAttackCooldown();
//            }
//        }
//    }
}
