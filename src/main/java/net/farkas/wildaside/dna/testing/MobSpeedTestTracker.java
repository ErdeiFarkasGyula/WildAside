package net.farkas.wildaside.dna.testing;

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
            System.out.println("✅ Test batch finished: " + testName);
        }

        boolean allEmpty = activeTests.values().stream().allMatch(Set::isEmpty);
        if (allEmpty) {
            System.out.println("🎉 All test conditions finished! Saving all results...");
            MobSpeedResultStorage.save();
            MobSpeedResultStorage.load();
        }
    }
}