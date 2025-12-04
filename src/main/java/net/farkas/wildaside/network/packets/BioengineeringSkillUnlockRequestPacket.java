package net.farkas.wildaside.network.packets;

import net.farkas.wildaside.dna.bioengineering_skill.BioengineeringSkillUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class BioengineeringSkillUnlockRequestPacket {
    private final ResourceLocation skillId;
    private final boolean unlock;

    public BioengineeringSkillUnlockRequestPacket(ResourceLocation skill, boolean unlock) {
        this.skillId = skill;
        this.unlock = unlock;
    }

    public static BioengineeringSkillUnlockRequestPacket decode(FriendlyByteBuf buf) {
        return new BioengineeringSkillUnlockRequestPacket(buf.readResourceLocation(), buf.readBoolean());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeResourceLocation(skillId);
        buf.writeBoolean(unlock);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (unlock) {
                BioengineeringSkillUtils.unlockAndSyncToClient(player, skillId);
            } else {
                BioengineeringSkillUtils.removeAndSyncToClient(player, skillId);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
