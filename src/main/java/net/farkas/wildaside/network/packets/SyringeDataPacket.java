package net.farkas.wildaside.network.packets;

import net.farkas.wildaside.dna.DnaConstants;
import net.farkas.wildaside.item.custom.SyringeItem;
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
    private final float dirtiness;

    private final long creationTick;
    private final long freezerTicks;

    private final boolean multipleSources;

    public SyringeDataPacket(UUID playerId, int slot, float progress, float blood, boolean animating, boolean inwards,
                             String fluidType, int fluidColor, float dirtiness, long creationTick, long freezerTicks, boolean multipleSources) {
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
        this.multipleSources = multipleSources;
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
        float dirtiness = buf.readFloat();

        long creationTick = buf.readLong();
        long freezerTicks = buf.readLong();

        boolean multipleSources = buf.readBoolean();

        return new SyringeDataPacket(playerId, slot, progress, blood, animating, inwards, fluidType, fluidColor, dirtiness, creationTick, freezerTicks, multipleSources);
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
        buf.writeFloat(dirtiness);

        buf.writeLong(creationTick);
        buf.writeLong(freezerTicks);

        buf.writeBoolean(multipleSources);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            SyringeItem.handleSyringeProgress(this);
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
    public float getDirtiness() { return dirtiness; }

    public long getCreationTick() { return creationTick; }
    public long getFreezerTicks() { return freezerTicks; }

    public boolean isMultipleSources() { return multipleSources; }
}