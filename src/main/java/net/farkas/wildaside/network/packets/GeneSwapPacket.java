package net.farkas.wildaside.network.packets;

import net.farkas.wildaside.block.entity.BioengineeringWorkstationBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class GeneSwapPacket {
    private final BlockPos pos;
    private final int indexA;
    private final int indexB;

    public GeneSwapPacket(BlockPos pos, int indexA, int indexB) {
        this.pos = pos;
        this.indexA = indexA;
        this.indexB = indexB;
    }

    public GeneSwapPacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.indexA = buf.readInt();
        this.indexB = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeInt(indexA);
        buf.writeInt(indexB);
    }

    public static void handle(GeneSwapPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            if (player.level().getBlockEntity(msg.pos) instanceof BioengineeringWorkstationBlockEntity be) {
//                be.swapGenes(msg.indexA, msg.indexB);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
