package net.farkas.wildaside.network.packets.bioengineering_workstation;

import net.farkas.wildaside.dna.bioengineering_skill.BioengineeringSkillPointOperation;
import net.farkas.wildaside.dna.bioengineering_skill.BioengineeringSkillUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

import static net.minecraftforge.network.NetworkDirection.PLAY_TO_SERVER;

public class BioengineeringSkillPointRequestPacket {
    private final int points;
    private final BioengineeringSkillPointOperation operation;

    public BioengineeringSkillPointRequestPacket(int points, BioengineeringSkillPointOperation operation) {
        this.points = points;
        this.operation = operation;
    }

    public static BioengineeringSkillPointRequestPacket decode(FriendlyByteBuf buf) {
        int points = buf.readInt();
        BioengineeringSkillPointOperation op = buf.readEnum(BioengineeringSkillPointOperation.class);
        return new BioengineeringSkillPointRequestPacket(points, op);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(points);
        buf.writeEnum(operation);
    }

    public static void handle(BioengineeringSkillPointRequestPacket msg, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            if (ctx.getDirection() != PLAY_TO_SERVER) return;
            var sender = ctx.getSender();
            if (sender == null) return;
            BioengineeringSkillUtils.handlePointsAndSyncToClient(sender, msg.points, msg.operation);
        });
        ctx.setPacketHandled(true);
    }
}