package net.farkas.wildaside.capability.gene_editor;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public interface IGeneEditorState extends INBTSerializable<CompoundTag> {
    int getSidebarWidth();
    void setSidebarWidth(int width);

    int getInfoPanelWidth();
    void setInfoPanelWidth(int width);

    double getScrollAMat();
    void setScrollAMat(double scroll);

    double getScrollAPat();
    void setScrollAPat(double scroll);

    double getScrollBMat();
    void setScrollBMat(double scroll);

    double getScrollBPat();
    void setScrollBPat(double scroll);

    boolean isLockGenomeA();
    void setLockGenomeA(boolean lock);

    boolean isLockGenomeB();
    void setLockGenomeB(boolean lock);

    boolean isLockPairs();
    void setLockPairs(boolean lock);
}