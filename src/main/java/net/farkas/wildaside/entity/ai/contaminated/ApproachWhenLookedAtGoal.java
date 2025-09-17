package net.farkas.wildaside.entity.ai.contaminated;

import net.farkas.wildaside.entity.custom.vibrion.ContaminatedCreeperEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;

public class ApproachWhenLookedAtGoal extends Goal {
    private final ContaminatedCreeperEntity creeper;
    private final double detectDistance;
    private final double triggerDistance;
    private final double speed;
    private final Level level;

    public ApproachWhenLookedAtGoal(ContaminatedCreeperEntity creeper, double speed, double detectDistance, double triggerDistance) {
        this.creeper = creeper;
        this.detectDistance = detectDistance;
        this.triggerDistance = triggerDistance;
        this.speed = speed;
        this.level = creeper.level();
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (creeper.getState() == ContaminatedCreeperEntity.STATE_BURROWED) return false;

        List<Player> players = level.getEntitiesOfClass(Player.class, creeper.getBoundingBox().inflate(detectDistance / 2));
        for (Player player : players) {
            if (player.isAttackable() && player.hasLineOfSight(creeper) && isLookingAt(player, creeper)) {
                creeper.setTarget(player);
                return true;
            }
        }
        return false;
    }

    @Override
    public void tick() {
        if (creeper.getTarget() == null) return;
        Player target = (Player) creeper.getTarget();

        creeper.getLookControl().setLookAt(creeper.getTarget());
        creeper.getNavigation().moveTo(target, speed);

        if (creeper.distanceTo(target) < triggerDistance) {
            creeper.setState(ContaminatedCreeperEntity.STATE_BURROWED);
            stop();
        }
    }

    @Override
    public void start() {
        super.start();
        creeper.setState(ContaminatedCreeperEntity.STATE_FOLLOWING);
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    private boolean isLookingAt(Player player, ContaminatedCreeperEntity creeper) {
        double dx = creeper.getX() - player.getX();
        double dy = creeper.getEyeY() - player.getEyeY();
        double dz = creeper.getZ() - player.getZ();
        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);

        if (dist > detectDistance) {
            return false;
        }

        dx /= dist;
        dy /= dist;
        dz /= dist;

        Vec3 look = player.getLookAngle().normalize();
        double dot = dx * look.x + dy * look.y + dz * look.z;

        return dot > 0.95D;
    }
}
