package net.farkas.wildaside.network.packets;

import net.farkas.wildaside.block.entity.BioengineeringWorkstationBlockEntity;
import net.farkas.wildaside.screen.bioengineering_workstation.BioengineeringWorkstationMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class RecompileDnaPacket {
    private final BlockPos pos;

    public RecompileDnaPacket(BlockPos pos) {
        this.pos = pos;
    }

    public RecompileDnaPacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    public static void handle(RecompileDnaPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            if (player.level().getBlockEntity(msg.pos) instanceof BioengineeringWorkstationBlockEntity be) {
                System.out.println("RECOMP");
                be.recompileDnas();
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
