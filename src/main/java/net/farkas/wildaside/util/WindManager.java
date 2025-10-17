package net.farkas.wildaside.util;

import net.farkas.wildaside.network.NetworkHandler;
import net.farkas.wildaside.network.WindData;
import net.farkas.wildaside.network.WindSavedData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

public class WindManager {
    private static final float RAIN_MULTIPLIER = 2.5f;
    private static final float THUNDER_MULTIPLIER = 5f;

    private static Vec3 direction = new Vec3(0.01, 0, 0.01);
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

    public static WindData calculateAndSetWind(ServerLevel serverLevel, boolean forceRecalc) {
        RandomSource randomSource = serverLevel.random;
        WindSavedData windSavedData = WindSavedData.get(serverLevel);

        boolean raining = serverLevel.isRaining();
        boolean thundering = serverLevel.isThundering();

        float correction;
        if (forceRecalc) {
            correction = getWeatherMultiplier(raining, thundering);
        } else {
            correction = getWindCorrection(windSavedData.wasRaining(), windSavedData.wasThundering(), raining, thundering);
        }

        double angle = randomSource.nextDouble() * 2 * Math.PI;
        Vec3 newDir = new Vec3(Math.cos(angle), (randomSource.nextFloat() - 0.5f) * 0.02f, Math.sin(angle)).scale(correction);
        float strength = (0.03f + randomSource.nextFloat() * 0.1f) * correction;

        WindManager.setWind(newDir, strength);
        windSavedData.setWeather(raining, thundering);
        windSavedData.setWind(newDir, strength);

        return new WindData(newDir, strength);
    }

    private static float getWeatherMultiplier(boolean raining, boolean thundering) {
        if (thundering) return THUNDER_MULTIPLIER;
        if (raining) return RAIN_MULTIPLIER;
        return 1f;
    }

    public static float getWindCorrection(boolean oldRaining, boolean oldThundering, boolean newRaining, boolean newThundering) {
        float oldMultiplier = 1f;
        if (oldRaining) oldMultiplier = RAIN_MULTIPLIER;
        if (oldThundering) oldMultiplier = THUNDER_MULTIPLIER;

        float newMultiplier = 1f;
        if (newRaining) newMultiplier = RAIN_MULTIPLIER;
        if (newThundering) newMultiplier = THUNDER_MULTIPLIER;

        return newMultiplier / oldMultiplier;
    }
}
