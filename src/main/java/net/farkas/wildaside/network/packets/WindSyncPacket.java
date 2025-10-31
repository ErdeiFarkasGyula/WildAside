package net.farkas.wildaside.network.packets;

import net.farkas.wildaside.client.ClientWindData;
import net.farkas.wildaside.network.WindData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class WindSyncPacket {
    private final WindData windData;

    public WindSyncPacket(Vec3 direction, float strength) {
        this.windData = new WindData(direction, strength);
    }

    public WindSyncPacket(WindData windData) {
        this.windData = windData;
    }

    public static void encode(WindSyncPacket msg, FriendlyByteBuf buf) {
        Vec3 direction = msg.windData.direction();
        buf.writeDouble(direction.x);
        buf.writeDouble(direction.y);
        buf.writeDouble(direction.z);
        buf.writeFloat(msg.windData.strength());
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
                WindData data = msg.windData;
                ClientWindData.setWind(data.direction(), data.strength());
            }
        });
        context.setPacketHandled(true);
    }
}