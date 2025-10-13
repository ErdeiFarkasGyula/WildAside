package net.farkas.wildaside.client;

import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientWindData {
    private static Vec3 windDir = Vec3.ZERO;
    private static float strength = 0f;

    public static void setWind(Vec3 dir, float s) {
        windDir = dir.normalize();
        strength = s;
    }

    public static Vec3 getWind() {
        return windDir.scale(strength);
    }

    public static Vec3 getRawWind() {
        return windDir;
    }

    public static float getStrength() {
        return strength;
    }
}