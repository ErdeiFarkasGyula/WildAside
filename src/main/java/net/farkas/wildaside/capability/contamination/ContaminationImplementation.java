package net.farkas.wildaside.capability.contamination;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

public class ContaminationImplementation implements IContamination {
    private int dose = 0;

    @Override
    public int getDose() {
        return dose;
    }

    @Override
    public int maxAmp() {
        return 5;
    }

    @Override
    public void setDose(int value) {
        this.dose = value;
    }

    @Override
    public void addDose(int value) {
        this.dose = Math.max(0, this.dose + value);
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider registryAccess) {
        final CompoundTag tag = new CompoundTag();
        tag.putInt("contamination_dose", this.dose);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider registryAccess, CompoundTag nbt) {
        this.dose = nbt.getInt("contamination_dose");
    }
}
