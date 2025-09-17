package net.farkas.wildaside.entity.ai.contaminated;

import net.farkas.wildaside.entity.custom.vibrion.ContaminatedCreeperEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class BurrowedChaseGoal extends Goal {
    private final ContaminatedCreeperEntity creeper;
    private final double burrowSpeed;
    private final double triggerDistance;
    private final int maxBurrowTime;

    private int burrowTime;

    public BurrowedChaseGoal(ContaminatedCreeperEntity creeper, double burrowSpeed, double triggerDistance, int maxBurrowTime) {
        this.creeper = creeper;
        this.burrowSpeed = burrowSpeed;
        this.triggerDistance = triggerDistance;
        this.maxBurrowTime = maxBurrowTime;

        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = creeper.getTarget();
        if (target == null) return false;

        return target.distanceTo(creeper) > triggerDistance || creeper.getState() == ContaminatedCreeperEntity.STATE_CHASE;
    }

    @Override
    public void start() {
        creeper.setState(ContaminatedCreeperEntity.STATE_BURROWED);
        burrowTime = 0;
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = creeper.getTarget();
        if (target == null) return false;

        if (creeper.distanceTo(target) < triggerDistance) return false;
        if (burrowTime > maxBurrowTime) return false;
        if (!creeper.getNavigation().isInProgress()) return false;

        return true;
    }

    @Override
    public void tick() {
        burrowTime++;

        LivingEntity target = creeper.getTarget();
        if (target == null) return;

        creeper.getNavigation().moveTo(target, burrowSpeed);

        if (creeper.distanceTo(target) < triggerDistance) {
            creeper.setState(ContaminatedCreeperEntity.STATE_CHASE);
            creeper.explode();
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
}