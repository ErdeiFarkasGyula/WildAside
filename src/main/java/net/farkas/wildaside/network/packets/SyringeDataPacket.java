package net.farkas.wildaside.network.packets;

import net.farkas.wildaside.dna.DnaConstants;
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

    private final String fluidType;
    private final int fluidColor;
    private final int dirtiness;

    private final long creationTick;
    private final long freezerTicks;

    public SyringeDataPacket(UUID playerId, int slot, float progress, float blood, boolean animating, boolean inwards,
                             String fluidType, int fluidColor, int dirtiness, long creationTick, long freezerTicks) {
        this.playerId = playerId;
        this.slot = slot;
        this.progress = progress;
        this.blood = blood;
        this.animating = animating;
        this.inwards = inwards;
        this.fluidType = fluidType;
        this.fluidColor = fluidColor;
        this.dirtiness = dirtiness;
        this.creationTick = creationTick;
        this.freezerTicks = freezerTicks;
    }

    public static SyringeDataPacket decode(FriendlyByteBuf buf) {
        UUID playerId = buf.readUUID();
        int slot = buf.readInt();
        float progress = buf.readFloat();
        float blood = buf.readFloat();
        boolean animating = buf.readBoolean();
        boolean inwards = buf.readBoolean();

        String fluidType = buf.readUtf(32767);
        int fluidColor = buf.readInt();
        int dirtiness = buf.readInt();

        long creationTick = buf.readLong();
        long freezerTicks = buf.readLong();

        return new SyringeDataPacket(playerId, slot, progress, blood, animating, inwards, fluidType, fluidColor, dirtiness, creationTick, freezerTicks);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUUID(playerId);
        buf.writeInt(slot);
        buf.writeFloat(progress);
        buf.writeFloat(blood);
        buf.writeBoolean(animating);
        buf.writeBoolean(inwards);

        buf.writeUtf(fluidType == null ? DnaConstants.NONE : fluidType);
        buf.writeInt(fluidColor);
        buf.writeInt(dirtiness);

        buf.writeLong(creationTick);
        buf.writeLong(freezerTicks);
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

    public String getFluidType() { return fluidType; }
    public int getFluidColor() { return fluidColor; }
    public int getDirtiness() { return dirtiness; }

    public long getCreationTick() { return creationTick; }
    public long getFreezerTicks() { return freezerTicks; }
}