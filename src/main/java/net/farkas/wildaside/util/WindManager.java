package net.farkas.wildaside.util;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.network.NetworkHandler;
import net.minecraft.world.phys.Vec3;

public class WindManager {
    private static Vec3 direction = new Vec3(1, 0, 0);
    private static float strength = 0.1f;

    public static void setWind(Vec3 dir, float s) {
        direction = dir.normalize();
        strength = s;
        NetworkHandler.sendWindUpdateToAll(direction, strength);
    }

    public static Vec3 getDirection() { return direction; }
    public static float getStrength() { return strength; }
}