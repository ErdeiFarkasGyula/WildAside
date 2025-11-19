package net.farkas.wildaside.capability.syringe;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public class SyringeAnimImplementation implements ISyringeAnim {
    public boolean active = false;
    public boolean inwards = true;
    public int slot = 0;
    public int tick = 0;
    public int duration = 20;

    @Override
    public boolean getActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean getInwards() {
        return inwards;
    }

    @Override
    public void setInwards(boolean inwards) {
        this.inwards = inwards;
    }

    @Override
    public int getSlot() {
        return slot;
    }

    @Override
    public void setSlot(int slot) {
        this.slot = slot;
    }

    @Override
    public int getTick() {
        return tick;
    }

    @Override
    public void setTick(int tick) {
        this.tick = tick;
    }

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public void start(boolean inwards, int slot) {
        this.active = true;
        this.inwards = inwards;
        this.slot = slot;
        this.tick = 0;
    }

    @Override
    public void stop() {
        this.active = false;
        this.tick = 0;
    }

    @Override
    public void tickClient(Player player) {
        if (!active) return;
        tick++;
        if (tick >= duration) {
            this.stop();
        }
    }

    @Override
    public float getProgress() {
        return Math.min(1f, (float) tick / (float) duration);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();

        tag.putBoolean("active", active);
        tag.putBoolean("inwards", inwards);
        tag.putInt("slot", slot);
        tag.putInt("tick", tick);
        tag.putInt("duration", duration);

        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.active = nbt.getBoolean("active");
        this.inwards = nbt.getBoolean("inwards");
        this.slot = nbt.getInt("slot");
        this.tick = nbt.getInt("tick");
        this.duration = nbt.getInt("duration");
    }
}
