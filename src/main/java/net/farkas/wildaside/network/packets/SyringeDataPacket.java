package net.farkas.wildaside.network.packets;

import net.farkas.wildaside.network.SyringeClientData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyringeDataPacket {
    private final int slot;
    private final float progress;
    private final boolean animating;
    private final boolean inwards;

    public SyringeDataPacket(int slot, float progress, boolean animating, boolean inwards) {
        this.slot = slot;
        this.progress = progress;
        this.animating = animating;
        this.inwards = inwards;
    }

    public static SyringeDataPacket decode(FriendlyByteBuf buf) {
        int slot = buf.readInt();
        float progress = buf.readFloat();
        boolean animating = buf.readBoolean();
        boolean inwards = buf.readBoolean();

        return new SyringeDataPacket(slot, progress, animating, inwards);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(slot);
        buf.writeFloat(progress);
        buf.writeBoolean(animating);
        buf.writeBoolean(inwards);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            SyringeClientData.handleSyringeProgress(this);
        });
        ctx.get().setPacketHandled(true);
        return true;
    }

    public int getSlot() { return slot; }
    public float getProgress() { return progress; }
    public boolean isAnimating() { return animating; }
    public boolean isInwards() { return inwards; }
}