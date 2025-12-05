package net.farkas.wildaside.network.packets;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.dna.bioengineering_skill.BioengineeringSkillUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class BioengineeringSkillUnlockRequestPacket {
    private final UUID uuid;
    private final ResourceLocation skillId;
    private final boolean unlock;

    public BioengineeringSkillUnlockRequestPacket(UUID uuid, ResourceLocation skill, boolean unlock) {
        this.uuid = uuid;
        this.skillId = skill;
        this.unlock = unlock;
    }

    public static BioengineeringSkillUnlockRequestPacket decode(FriendlyByteBuf buf) {
        return new BioengineeringSkillUnlockRequestPacket(buf.readUUID(), buf.readResourceLocation(), buf.readBoolean());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUUID(uuid);
        buf.writeResourceLocation(skillId);
        buf.writeBoolean(unlock);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = ctx.get().getSender().serverLevel().getPlayerByUUID(uuid);
            if (player == null) WildAside.LOGGER.error("BioengineeringSkillUnlockRequestPacket: player is null");

            if (unlock) {
                BioengineeringSkillUtils.unlockAndSyncToClient(player, skillId);
            } else {
                BioengineeringSkillUtils.removeAndSyncToClient(player, skillId);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
