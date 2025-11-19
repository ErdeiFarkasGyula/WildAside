package net.farkas.wildaside.capability.syringe;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.INBTSerializable;

public interface ISyringeAnim extends INBTSerializable<CompoundTag> {
    boolean getActive();
    void setActive(boolean active);
    boolean getInwards();
    void setInwards(boolean inwards);
    int getSlot();
    void setSlot(int slot);
    int getTick();
    void setTick(int tick);
    int getDuration();
    void setDuration(int duration);

    float getProgress();

    void start(boolean inwards, int slot);
    void stop();
    void tickClient(Player player);
}
