package net.farkas.wildaside.network.packet.incubator;

import net.farkas.wildaside.block.entity.custom.IncubatorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record ToggleIncubatorOpenPacket(BlockPos pos) {
    public static void encode(ToggleIncubatorOpenPacket pkt, FriendlyByteBuf buf) {
        buf.writeBlockPos(pkt.pos);
    }

    public static ToggleIncubatorOpenPacket decode(FriendlyByteBuf buf) {
        return new ToggleIncubatorOpenPacket(buf.readBlockPos());
    }

    public static void handle(ToggleIncubatorOpenPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            var player = ctx.get().getSender();
            if (player == null) return;
            var be = player.level().getBlockEntity(pkt.pos);
            if (be instanceof IncubatorBlockEntity inc) {
                inc.setOpen(!inc.isOpen());
            }
        });
        ctx.get().setPacketHandled(true);
    }
}