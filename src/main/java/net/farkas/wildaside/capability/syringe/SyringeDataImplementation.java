package net.farkas.wildaside.capability.syringe;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SyringeDataImplementation implements ISyringeData {
    private float progress = 0f;
    private boolean animating = false;
    private boolean inwards = true;
    private int slot = 0;

    @Override
    public float getProgress() { return progress; }
    @Override
    public void setProgress(float v) { this.progress = v; }

    @Override
    public boolean isAnimating() { return animating; }
    @Override
    public void setAnimating(boolean v) { this.animating = v; }

    @Override
    public boolean isInwards() { return inwards; }
    @Override
    public void setInwards(boolean v) { this.inwards = v; }

    @Override
    public int getSlot() { return slot; }
    @Override
    public void setSlot(int s) { this.slot = s; }

    @Override
    public void reset() {
        this.progress = 0f;
        this.animating = false;
        this.inwards = true;
        this.slot = 0;
    }

    @Override
    public void tick(ItemStack stack, Level level) {
        if (level.isClientSide()) return;

    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag t = new CompoundTag();
        t.putFloat("progress", progress);
        t.putBoolean("animating", animating);
        t.putBoolean("inwards", inwards);
        t.putInt("slot", slot);
        return t;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.progress = nbt.getFloat("progress");
        this.animating = nbt.getBoolean("animating");
        this.inwards = nbt.getBoolean("inwards");
        this.slot = nbt.getInt("slot");
    }
}