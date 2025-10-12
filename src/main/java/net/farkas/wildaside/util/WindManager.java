package net.farkas.wildaside.util;

import net.farkas.wildaside.network.NetworkHandler;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

public class WindManager {
    private static Vec3 direction = new Vec3(1, 0, 0);
    private static float strength = 0.1f;

    public static void setWind(Vec3 dir, float s) {
        direction = dir.normalize();
        strength = s;
        NetworkHandler.sendWindUpdateToAll(direction, strength);
    }

    public static void setWind(WindData windData) {
        direction = windData.direction().normalize();
        strength = windData.strength();
        NetworkHandler.sendWindUpdateToAll(direction, strength);
    }

    public static Vec3 getDirection() { return direction; }
    public static float getStrength() { return strength; }
    public static WindData getWindData() { return new WindData(getDirection(), getStrength()); }

    public static WindData calculateWind(RandomSource randomSource) {
        double angle = randomSource.nextDouble() * 2 * Math.PI;
        Vec3 newDir = new Vec3(Math.cos(angle), 0, Math.sin(angle));
        float strength = 0.05f + randomSource.nextFloat() * 0.15f;
        WindManager.setWind(newDir, strength);
        return new WindData(direction, strength);
    }
}