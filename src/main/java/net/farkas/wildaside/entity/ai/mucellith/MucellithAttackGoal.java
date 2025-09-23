package net.farkas.wildaside.entity.ai.mucellith;

import net.farkas.wildaside.entity.custom.vibrion.MucellithEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class MucellithAttackGoal extends Goal {
    private final Mob mob;
    private final MucellithEntity entity;
    @Nullable
    private LivingEntity target;
    private int attackTime = 60;
    private final int attackTimeMax;
    private final float attackRadius;

    public MucellithAttackGoal(MucellithEntity entity, int pAttackInterval, float pAttackRadius) {
        this.entity = entity;
        this.mob = (Mob)entity;
        this.attackTimeMax = pAttackInterval;
        this.attackRadius = pAttackRadius;
        this.setFlags(EnumSet.of(Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (entity.isDefending()) return false;

        LivingEntity livingentity = this.mob.getTarget();
        if (livingentity != null && livingentity.isAlive() && !(livingentity instanceof MucellithEntity)) {
            this.target = livingentity;
            return true;
        }

        return false;
    }

    @Override
    public void stop() {
        entity.setAttacking(false);
        this.attackTime = attackTimeMax;
        this.target = null;
    }

    @Override
    public void start() {
        this.attackTime = attackTimeMax;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        if (target instanceof Player player) {
            if (player.isInvulnerable()) {
                target = null;
            }
        }

        if (target == null) {
            stop();
            return;
        }

        shoot();
    }

    private void shoot() {
        double d0 = this.mob.distanceToSqr(this.target.getX(), this.target.getY(), this.target.getZ());
        boolean flag = this.mob.getSensing().hasLineOfSight(this.target);

        this.mob.getLookControl().setLookAt(this.target, 30.0F, 30.0F);

        --this.attackTime;

        if (attackTime <= 0) {
            if (!flag) {
                return;
            }

            float f = (float) Math.sqrt(d0) / this.attackRadius;
            float f1 = Mth.clamp(f, 0.1F, 1.0F);
            this.entity.performRangedAttack(this.target, f1);
            this.attackTime = attackTimeMax;
        } else
            if (attackTime == 7) {
                entity.setAttacking(true);
                entity.attackAnimation.start(entity.tickCount);
                entity.attackAnimationTimeout = entity.attackAnimationMax;
            }
    }
}
