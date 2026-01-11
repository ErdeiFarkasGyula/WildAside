package net.farkas.wildaside.network.packet.incubator;

import net.farkas.wildaside.block.entity.custom.IncubatorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

public record SetIncubatorHeatLevelPacket(BlockPos pos, int level) {
    public static void encode(SetIncubatorHeatLevelPacket pkt, FriendlyByteBuf buf) {
        buf.writeBlockPos(pkt.pos);
        buf.writeVarInt(pkt.level);
    }

    public static SetIncubatorHeatLevelPacket decode(FriendlyByteBuf buf) {
        return new SetIncubatorHeatLevelPacket(buf.readBlockPos(), buf.readVarInt());
    }

    public static void handle(SetIncubatorHeatLevelPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            var player = ctx.get().getSender();
            if (player == null) return;
            var be = player.level().getBlockEntity(pkt.pos);
            if (be instanceof IncubatorBlockEntity inc) {
                inc.setHeatLevel(pkt.level);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}