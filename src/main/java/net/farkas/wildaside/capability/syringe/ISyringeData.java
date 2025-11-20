package net.farkas.wildaside.capability.syringe;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;

public interface ISyringeData extends INBTSerializable<CompoundTag> {
    float getProgress();
    void setProgress(float v);

    boolean isAnimating();
    void setAnimating(boolean v);

    boolean isInwards();
    void setInwards(boolean v);

    int getSlot();
    void setSlot(int s);

    void reset();
    void tick(ItemStack stack, Level level);
}