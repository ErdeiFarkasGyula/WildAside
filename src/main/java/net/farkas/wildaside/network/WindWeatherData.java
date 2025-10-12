package net.farkas.wildaside.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

public class WindWeatherData extends SavedData {
    private boolean lastRaining = false;
    private boolean lastThundering = false;

    public WindWeatherData(CompoundTag tag) {
        this.lastRaining = tag.getBoolean("lastRaining");
        this.lastThundering = tag.getBoolean("lastThundering");
    }

    public WindWeatherData() {
        this.lastRaining = false;
        this.lastThundering = false;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putBoolean("lastRaining", lastRaining);
        tag.putBoolean("lastThundering", lastThundering);
        return tag;
    }

    public boolean wasRaining() { return lastRaining; }
    public boolean wasThundering() { return lastThundering; }

    public void set(boolean raining, boolean thundering) {
        lastRaining = raining;
        lastThundering = thundering;
        setDirty();
    }

    public static WindWeatherData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                WindWeatherData::new,
                WindWeatherData::new,
                "wildaside_weather"
        );
    }
}