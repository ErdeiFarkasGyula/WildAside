package net.farkas.wildaside.network;

import net.farkas.wildaside.util.WindData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.Vec3;

public class WindSavedData extends SavedData {
    private boolean lastRaining = false;
    private boolean lastThundering = false;

    private double windX = 0;
    private double windY = 0;
    private double windZ = 0;
    private float windStrength = 0f;

    public WindSavedData(CompoundTag tag) {
        this.lastRaining = tag.getBoolean("lastRaining");
        this.lastThundering = tag.getBoolean("lastThundering");

        this.windX = tag.getDouble("windX");
        this.windY = tag.getDouble("windY");
        this.windZ = tag.getDouble("windZ");
        this.windStrength = tag.getFloat("windStrength");
    }

    public WindSavedData() {}

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putBoolean("lastRaining", lastRaining);
        tag.putBoolean("lastThundering", lastThundering);

        tag.putDouble("windX", windX);
        tag.putDouble("windY", windY);
        tag.putDouble("windZ", windZ);
        tag.putFloat("windStrength", windStrength);

        return tag;
    }

    public boolean wasRaining() { return lastRaining; }
    public boolean wasThundering() { return lastThundering; }

    public void setWeather(boolean raining, boolean thundering) {
        this.lastRaining = raining;
        this.lastThundering = thundering;
        setDirty();
    }

    public void setWind(Vec3 direction, float strength) {
        this.windX = direction.x;
        this.windY = direction.y;
        this.windZ = direction.z;
        this.windStrength = strength;
        setDirty();
    }

    public Vec3 getWindDirection() {
        return new Vec3(windX, windY, windZ);
    }

    public float getWindStrength() {
        return windStrength;
    }

    public WindData getWindData() {
        return new WindData(getWindDirection(), getWindStrength());
    }

    public static WindSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                WindSavedData::new,
                WindSavedData::new,
                "wildaside_weather"
        );
    }
}