package net.farkas.wildaside.capability.contamination;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public interface IContamination extends INBTSerializable<CompoundTag> {
    int getDose();
    int maxAmp();
    void setDose(int value);
    void addDose(int value);
}