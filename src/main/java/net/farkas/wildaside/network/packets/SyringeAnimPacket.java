package net.farkas.wildaside.network.packets;

import net.farkas.wildaside.capability.syringe.SyringeAnimCapability;
import net.farkas.wildaside.capability.syringe.SyringeAnimProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyringeAnimPacket {
    public boolean inwards;
    public int slot;

    public SyringeAnimPacket() {}
    public SyringeAnimPacket(boolean inwards, int slot) { this.inwards = inwards; this.slot = slot; }

    public static void encode(SyringeAnimPacket msg, FriendlyByteBuf buf) {
        buf.writeBoolean(msg.inwards);
        buf.writeInt(msg.slot);
    }

    public static SyringeAnimPacket decode(FriendlyByteBuf buf) {
        return new SyringeAnimPacket(buf.readBoolean(), buf.readInt());
    }

    public static void handle(SyringeAnimPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection().getReceptionSide().isClient()) {
                Player clientPlayer = net.minecraft.client.Minecraft.getInstance().player;
                System.out.println(clientPlayer);
                if (clientPlayer != null) {
                    System.out.println("YES");
                    clientPlayer.getCapability(SyringeAnimCapability.INSTANCE)
                            .ifPresent(anim -> anim.start(msg.inwards, msg.slot));
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}