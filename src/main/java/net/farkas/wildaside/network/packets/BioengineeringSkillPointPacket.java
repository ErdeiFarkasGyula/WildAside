package net.farkas.wildaside.network.packets;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.dna.bioengineering_skill.BioengineeringSkillPointOperation;
import net.farkas.wildaside.dna.bioengineering_skill.BioengineeringSkillUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class BioengineeringSkillPointPacket {
    private final UUID uuid;
    private final int points;
    private final BioengineeringSkillPointOperation operation;

    public BioengineeringSkillPointPacket(UUID uuid, int points, BioengineeringSkillPointOperation operation) {
        this.uuid = uuid;
        this.points = points;
        this.operation = operation;
    }

    public static BioengineeringSkillPointPacket decode(FriendlyByteBuf buf) {
        return new BioengineeringSkillPointPacket(buf.readUUID(), buf.readInt(), buf.readEnum(BioengineeringSkillPointOperation.class));
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUUID(uuid);
        buf.writeInt(points);
        buf.writeEnum(operation);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = ctx.get().getSender().serverLevel().getPlayerByUUID(uuid);
            if (player == null) WildAside.LOGGER.error("BioengineeringSkillPointPacket: player is null");

            BioengineeringSkillUtils.handlePointsAndSyncToClient(player, points, operation);

        });
        ctx.get().setPacketHandled(true);
    }
}
