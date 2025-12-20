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
    private static final int WINDUP_DURATION = 7;
    private final Mob mob;
    private final MucellithEntity entity;
    @Nullable
    private LivingEntity target;
    private int attackTime = 60;
    private final int attackTimeMax;
    private final float attackRadius;
    private int windupTicks;

    public MucellithAttackGoal(MucellithEntity entity, int pAttackInterval, float pAttackRadius) {
        this.entity = entity;
        this.mob = entity;
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

    public void shoot() {
        --this.attackTime;
        this.mob.getLookControl().setLookAt(this.target, 30.0F, 30.0F);

        if (attackTime <= 0) {
            if (!mob.getSensing().hasLineOfSight(target)) return;

            entity.setAttacking(true);
            entity.attackAnimation.start(entity.tickCount);
            entity.attackAnimationTimeout = entity.attackAnimationMax;

            this.attackTime = attackTimeMax;
            this.windupTicks = WINDUP_DURATION;
        }

        if (windupTicks > 0) {
            --windupTicks;
            if (windupTicks == 0) {
                float dist = (float) Math.sqrt(mob.distanceToSqr(target)) / this.attackRadius;
                float power = Mth.clamp(dist, 0.1F, 1.0F);
                this.entity.performRangedAttack(this.target, power);
            }
        }

        if (entity.isAttacking()) {
            if (--entity.attackAnimationTimeout <= 0) {
                entity.attackAnimation.stop();
                entity.setAttacking(false);
            }
        }
    }
}
