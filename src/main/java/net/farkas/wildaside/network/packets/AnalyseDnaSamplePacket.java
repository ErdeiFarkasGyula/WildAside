package net.farkas.wildaside.network.packets;

import net.farkas.wildaside.block.entity.BioengineeringWorkstationBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class AnalyseDnaSamplePacket {
    private final BlockPos pos;

    public AnalyseDnaSamplePacket(BlockPos pos) {
        this.pos = pos;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    public static AnalyseDnaSamplePacket decode(FriendlyByteBuf buf) {
        return new AnalyseDnaSamplePacket(buf.readBlockPos());
    }

    public static void handle(AnalyseDnaSamplePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) return;

            if (player.level().getBlockEntity(msg.pos) instanceof BioengineeringWorkstationBlockEntity be) {
                be.analyseDna();
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
