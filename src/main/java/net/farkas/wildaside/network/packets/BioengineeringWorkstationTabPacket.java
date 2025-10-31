package net.farkas.wildaside.network.packets;

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

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(tabIndex);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) return;

            if (player.containerMenu instanceof BioengineeringWorkstationMenu menu) {
//                menu.setCurrentTab(tabIndex);
            }
        });
        context.setPacketHandled(true);
    }
}