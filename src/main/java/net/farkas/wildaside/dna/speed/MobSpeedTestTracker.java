package net.farkas.wildaside.dna.speed;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MobSpeedTestTracker {
    private static final Map<String, Set<UUID>> activeTests = new ConcurrentHashMap<>();

    public static void registerMob(Mob mob, String testName) {
        activeTests.computeIfAbsent(testName, t -> ConcurrentHashMap.newKeySet()).add(mob.getUUID());
    }

    public static void onMobFinished(Mob mob, String testName) {
        Set<UUID> active = activeTests.get(testName);
        if (active == null) return;
        System.out.println("ACTIVE:" + active);

        active.remove(mob.getUUID());
        if (active.isEmpty()) {
            System.out.println("Test batch finished: " + testName);
        }

        boolean allEmpty = activeTests.values().stream().allMatch(Set::isEmpty);
        if (allEmpty) {
            System.out.println("Saving results.");
            MobSpeedResultStorage.save();
            MobSpeedResultStorage.load();
            if (mob.level() instanceof ServerLevel serverLevel) {
                MobSpeedTestManager.unloadTestLevelArea(serverLevel, new BlockPos(0, 16, 0), 150, MobSpeedTesting.entityCount * 4 + 2);
                System.out.println(serverLevel.isLoaded(new BlockPos(0, 5, 0)));
            }
        }
    }
}