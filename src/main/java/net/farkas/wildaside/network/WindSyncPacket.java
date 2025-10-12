package net.farkas.wildaside.network;

import net.farkas.wildaside.client.ClientWindData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class WindSyncPacket {
    private final Vec3 direction;
    private final float strength;

    public WindSyncPacket(Vec3 direction, float strength) {
        this.direction = direction;
        this.strength = strength;
    }

    public static void encode(WindSyncPacket msg, FriendlyByteBuf buf) {
        buf.writeDouble(msg.direction.x);
        buf.writeDouble(msg.direction.y);
        buf.writeDouble(msg.direction.z);
        buf.writeFloat(msg.strength);
    }

    public static WindSyncPacket decode(FriendlyByteBuf buf) {
        Vec3 dir = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        float s = buf.readFloat();
        return new WindSyncPacket(dir, s);
    }

    public static void handle(WindSyncPacket msg, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            if (context.getDirection().getReceptionSide().isClient()) {
                ClientWindData.setWind(msg.direction, msg.strength);
            }
        });
        context.setPacketHandled(true);
    }
}