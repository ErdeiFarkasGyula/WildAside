package net.farkas.wildaside.network.packets.bioengineering_workstation;

import net.farkas.wildaside.screen.bioengineering_workstation.BioengineeringWorkstationMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class BioengineeringWorkstationTabPacket {
    private final int tabIndex;

    public BioengineeringWorkstationTabPacket(int tabIndex) {
        this.tabIndex = tabIndex;
    }

    public BioengineeringWorkstationTabPacket(FriendlyByteBuf buf) {
        this.tabIndex = buf.readInt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(tabIndex);
    }

    public static BioengineeringWorkstationTabPacket decode(FriendlyByteBuf buf) { return new BioengineeringWorkstationTabPacket(buf.readInt()); }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) return;

            if (player.containerMenu instanceof BioengineeringWorkstationMenu menu) {
//                menu.blockEntity.setTab(tabIndex);
                menu.setTab(tabIndex);
            }
        });
        context.setPacketHandled(true);
    }
}