package net.farkas.wildaside.dna.speed;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

public class MobSpeedTestGoal extends Goal {
    private final Mob mob;
    private final Direction dir;
    private final int length;
    private final String testName;

    private static final float TEST_LENGTH = 60;

    private Vec3 startPosition;
    private Vec3 endPosition;
    private double distanceTraveled;
    private int ticks;

    public MobSpeedTestGoal(Mob mob, Direction dir, int length, String testName) {
        this.mob = mob;
        this.dir = dir;
        this.length = length;
        this.testName = testName;
    }

    @Override
    public void start() {
        super.start();
        ticks = 0;
        startPosition = mob.position();
    }

    @Override
    public void stop() {
        super.stop();
        endPosition = mob.position();
        String env = mob.isInWater() ? "water" : "ground";
        distanceTraveled = startPosition.distanceTo(endPosition);
        double blocksPerSecond = distanceTraveled / TEST_LENGTH * 20.0;
        MobSpeedResultStorage.record(mob.getType(), env, blocksPerSecond);
        MobSpeedTestTracker.onMobFinished(mob, testName);
        mob.discard();
    }

    @Override
    public boolean canUse() {
        return ticks <= TEST_LENGTH;
    }

    @Override
    public void tick() {
        ++ticks;

        double dx = 0, dz = 0;
        switch(dir) {
            case EAST -> dx = length;
            case WEST -> dx = -length;
            case NORTH -> dz = length;
            case SOUTH   -> dz = -length;
        }

        mob.getNavigation().moveTo(
                mob.getX() + dx,
                mob.getY(),
                mob.getZ() + dz,
                1.0
        );
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
}