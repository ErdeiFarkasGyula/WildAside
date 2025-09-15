package net.farkas.wildaside.entity.ai.contaminated;

import net.farkas.wildaside.entity.custom.vibrion.ContaminatedCreeperEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;

public class BurrowedGoal extends Goal {
    private final ContaminatedCreeperEntity creeper;
    private final double moveSpeed;
    private final double triggerDistance;
    private Player target;

    public BurrowedGoal(ContaminatedCreeperEntity creeper, double moveSpeed, double triggerDistance) {
        this.creeper = creeper;
        this.moveSpeed = moveSpeed;
        this.triggerDistance = triggerDistance;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (creeper.getState() == ContaminatedCreeperEntity.STATE_BURROWED) {
            target = (Player) creeper.getTarget();
            return target != null;
        }
        return false;
    }

    @Override
    public void stop() {
        super.stop();
        creeper.noPhysics = false;
    }

    @Override
    public void start() {
        super.start();
        creeper.noPhysics = true;
        creeper.getNavigation().stop();
    }

    @Override
    public void tick() {
        if (creeper.getTarget() == null) return;
        Player target = (Player) creeper.getTarget();

        creeper.getMoveControl().setWantedPosition(target.getX(), target.getY(), target.getZ(), moveSpeed);

        if (creeper.distanceTo(creeper.getTarget()) < triggerDistance) {
            creeper.level().playSound(null, creeper.blockPosition(), SoundEvents.GRASS_BREAK, SoundSource.HOSTILE, 1.0F, 0.8F);
            creeper.explode();
            creeper.discard();
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
}
