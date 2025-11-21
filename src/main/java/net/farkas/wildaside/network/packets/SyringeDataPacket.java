package net.farkas.wildaside.network.packets;

import net.farkas.wildaside.item.custom.Syringe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class SyringeDataPacket {
    private final UUID playerId;
    private final int slot;
    private final float progress;
    private final float blood;
    private final boolean animating;
    private final boolean inwards;

    public SyringeDataPacket(UUID playerId, int slot, float progress, float blood, boolean animating, boolean inwards) {
        this.playerId = playerId;
        this.slot = slot;
        this.progress = progress;
        this.blood = blood;
        this.animating = animating;
        this.inwards = inwards;
    }

    public static SyringeDataPacket decode(FriendlyByteBuf buf) {
        UUID playerId = buf.readUUID();
        int slot = buf.readInt();
        float progress = buf.readFloat();
        float blood = buf.readFloat();
        boolean animating = buf.readBoolean();
        boolean inwards = buf.readBoolean();

        return new SyringeDataPacket(playerId, slot, progress, blood, animating, inwards);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUUID(playerId);
        buf.writeInt(slot);
        buf.writeFloat(progress);
        buf.writeFloat(blood);
        buf.writeBoolean(animating);
        buf.writeBoolean(inwards);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Syringe.handleSyringeProgress(this);
        });
        ctx.get().setPacketHandled(true);
        return true;
    }

    public UUID getPlayerId() { return playerId; }
    public int getSlot() { return slot; }
    public float getProgress() { return progress; }
    public float getBlood() { return blood; }
    public boolean isAnimating() { return animating; }
    public boolean isInwards() { return inwards; }
}