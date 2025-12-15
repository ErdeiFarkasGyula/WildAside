package net.farkas.wildaside.screen.bioengineering_workstation;

public enum VisualStrand {
    TOP(-40f, 0f, -10),
    CENTER(0f, (float) (Math.PI / 2f), 0),
    BOTTOM(40f, (float) Math.PI, 10);

    public final float baseY;
    public final float phase;
    public final int xOffset;

    VisualStrand(float baseY, float phase,  int xOffset) {
        this.baseY = baseY;
        this.phase = phase;
        this.xOffset = xOffset;
    }
}