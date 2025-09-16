package net.farkas.wildaside.entity.ai.contaminated;

import net.farkas.wildaside.block.ModBlocks;
import net.farkas.wildaside.entity.custom.vibrion.ContaminatedCreeperEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

import java.util.EnumSet;

public class BurrowedGoal extends Goal {
    private final ContaminatedCreeperEntity creeper;
    private final double triggerDistance;
    private Player target;
    private int stuckTicks = 0;

    public BurrowedGoal(ContaminatedCreeperEntity creeper, double moveSpeed, double triggerDistance) {
        this.creeper = creeper;
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
    public void start() {
        stuckTicks = 0;
        creeper.noPhysics = true; // needed so it can "phase" into soil blocks
        creeper.getNavigation().stop();
    }

    @Override
    public void stop() {
        creeper.noPhysics = false;
        target = null;
    }

    @Override
    public void tick() {
        if (target == null || !target.isAlive()) {
            stop();
            return;
        }

        // Check if close enough to blow
        if (creeper.distanceTo(target) < triggerDistance) {
            creeper.level().playSound(null, creeper.blockPosition(),
                    SoundEvents.GRASS_BREAK, SoundSource.HOSTILE, 1.0F, 0.8F);
            creeper.explode();
            creeper.discard();
            return;
        }

        // Try to step toward target through burrowable blocks
        BlockPos creeperPos = creeper.blockPosition();
        BlockPos targetPos = target.blockPosition();

        Direction stepDir = Direction.getNearest(
                targetPos.getX() - creeperPos.getX(),
                targetPos.getY() - creeperPos.getY(),
                targetPos.getZ() - creeperPos.getZ()
        );

        BlockPos nextPos = creeperPos.relative(stepDir);
        BlockState nextState = creeper.level().getBlockState(nextPos);

        if (canBurrow(nextState)) {
            creeper.setPos(nextPos.getX() + 0.5, nextPos.getY(), nextPos.getZ() + 0.5);
            stuckTicks = 0; // reset stuck counter if we move
        } else {
            stuckTicks++;
            if (stuckTicks > 20) { // ~1 second stuck
                // fail-safe: surface and try normal AI
                creeper.setState(ContaminatedCreeperEntity.STATE_IDLE);
                creeper.noPhysics = false;
                stuckTicks = 0;
            }
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    private boolean canBurrow(BlockState state) {
        return state.is(ModBlocks.SUBSTILIUM_SOIL.get());
    }
}