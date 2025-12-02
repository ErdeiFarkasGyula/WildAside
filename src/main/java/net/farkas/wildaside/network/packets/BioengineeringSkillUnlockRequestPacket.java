package net.farkas.wildaside.network.packets;

import net.farkas.wildaside.dna.bioengineering_skill.BioengineeringSkillUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class BioengineeringSkillUnlockRequestPacket {
    private final ResourceLocation skillId;

    public BioengineeringSkillUnlockRequestPacket(ResourceLocation skill) {
        this.skillId = skill;
    }

    public static BioengineeringSkillUnlockRequestPacket decode(FriendlyByteBuf buf) {
        return new BioengineeringSkillUnlockRequestPacket(buf.readResourceLocation());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeResourceLocation(skillId);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            BioengineeringSkillUtils.unlockAndSyncToClient(player, skillId);
        });
        ctx.get().setPacketHandled(true);
    }
}
