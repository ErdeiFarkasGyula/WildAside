package net.farkas.wildaside.entity.ai.vibrion.bacillus_blob;

import net.farkas.wildaside.entity.custom.vibrion.BacillusBlobEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class BlobLeapAtTargetGoal extends Goal {
    private final BacillusBlobEntity blob;
    private LivingEntity target;

    private static final float LEAP_RANGE = 3.5f;
    private static final float MIN_RANGE = 0.8f;
    private static final double HORIZONTAL_VELOCITY = 0.6;
    private static final double VERTICAL_VELOCITY = 0.4;

    private int cooldown = 0;

    public BlobLeapAtTargetGoal(BacillusBlobEntity blob) {
        this.blob = blob;
        this.setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (blob.isAttached()) return false;
        if (!blob.onGround()) return false;
        if (cooldown > 0) {
            cooldown--;
            return false;
        }

        target = blob.getTarget();
        if (target == null) return false;
        if (target.isDeadOrDying()) return false;

        double distance = blob.distanceTo(target);
        return distance <= LEAP_RANGE && distance > MIN_RANGE;
    }

    @Override
    public boolean canContinueToUse() {
        return false;
    }

    @Override
    public void start() {
        if (target == null) return;

        double dx = target.getX() - blob.getX();
        double dy = (target.getY() + target.getBbHeight() * 0.5) - blob.getY();
        double dz = target.getZ() - blob.getZ();

        double horizontalDist = Math.sqrt(dx * dx + dz * dz);
        if (horizontalDist < 0.01) return;

        double nx = dx / horizontalDist;
        double nz = dz / horizontalDist;

        double verticalBoost = Math.max(0.3, Math.min(0.6, VERTICAL_VELOCITY + dy * 0.15));

        blob.setDeltaMovement(
                nx * HORIZONTAL_VELOCITY,
                verticalBoost,
                nz * HORIZONTAL_VELOCITY
        );

        cooldown = 15;
    }
}