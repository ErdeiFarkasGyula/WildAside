package net.farkas.wildaside.capability.gene_editor;

import net.minecraft.nbt.CompoundTag;

public class GeneEditorStateImplementation implements IGeneEditorState {
    private int sidebarWidth = 150;
    private int infoPanelWidth = 160;
    private double scrollAMat = 0;
    private double scrollAPat = 0;
    private double scrollBMat = 0;
    private double scrollBPat = 0;
    private boolean lockGenomeA = true;
    private boolean lockGenomeB = true;
    private boolean lockPairs = true;

    @Override
    public int getSidebarWidth() {
        return sidebarWidth;
    }

    @Override
    public void setSidebarWidth(int width) {
        this.sidebarWidth = width;
    }

    @Override
    public int getInfoPanelWidth() {
        return infoPanelWidth;
    }

    @Override
    public void setInfoPanelWidth(int width) {
        this.infoPanelWidth = width;
    }

    @Override
    public double getScrollAMat() {
        return scrollAMat;
    }

    @Override
    public void setScrollAMat(double scroll) {
        this.scrollAMat = scroll;
    }

    @Override
    public double getScrollAPat() {
        return scrollAPat;
    }

    @Override
    public void setScrollAPat(double scroll) {
        this.scrollAPat = scroll;
    }

    @Override
    public double getScrollBMat() {
        return scrollBMat;
    }

    @Override
    public void setScrollBMat(double scroll) {
        this.scrollBMat = scroll;
    }

    @Override
    public double getScrollBPat() {
        return scrollBPat;
    }

    @Override
    public void setScrollBPat(double scroll) {
        this.scrollBPat = scroll;
    }

    @Override
    public boolean isLockGenomeA() {
        return lockGenomeA;
    }

    @Override
    public void setLockGenomeA(boolean lock) {
        this.lockGenomeA = lock;
    }

    @Override
    public boolean isLockGenomeB() {
        return lockGenomeB;
    }

    @Override
    public void setLockGenomeB(boolean lock) {
        this.lockGenomeB = lock;
    }

    @Override
    public boolean isLockPairs() {
        return lockPairs;
    }

    @Override
    public void setLockPairs(boolean lock) {
        this.lockPairs = lock;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("sidebarWidth", sidebarWidth);
        tag.putInt("infoPanelWidth", infoPanelWidth);
        tag.putDouble("scrollAMat", scrollAMat);
        tag.putDouble("scrollAPat", scrollAPat);
        tag.putDouble("scrollBMat", scrollBMat);
        tag.putDouble("scrollBPat", scrollBPat);
        tag.putBoolean("lockGenomeA", lockGenomeA);
        tag.putBoolean("lockGenomeB", lockGenomeB);
        tag.putBoolean("lockPairs", lockPairs);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        if (tag.contains("sidebarWidth")) sidebarWidth = tag.getInt("sidebarWidth");
        if (tag.contains("infoPanelWidth")) infoPanelWidth = tag.getInt("infoPanelWidth");
        if (tag.contains("scrollAMat")) scrollAMat = tag.getDouble("scrollAMat");
        if (tag.contains("scrollAPat")) scrollAPat = tag.getDouble("scrollAPat");
        if (tag.contains("scrollBMat")) scrollBMat = tag.getDouble("scrollBMat");
        if (tag.contains("scrollBPat")) scrollBPat = tag.getDouble("scrollBPat");
        if (tag.contains("lockGenomeA")) lockGenomeA = tag.getBoolean("lockGenomeA");
        if (tag.contains("lockGenomeB")) lockGenomeB = tag.getBoolean("lockGenomeB");
        if (tag.contains("lockPairs")) lockPairs = tag.getBoolean("lockPairs");
    }
}