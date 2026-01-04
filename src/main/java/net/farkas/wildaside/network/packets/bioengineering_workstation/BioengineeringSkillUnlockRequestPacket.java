package net.farkas.wildaside.network.packets.bioengineering_workstation;

import net.farkas.wildaside.dna.bioengineering_skill.BioengineeringSkillUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class BioengineeringSkillUnlockRequestPacket {
    private final ResourceLocation skillId;

    public BioengineeringSkillUnlockRequestPacket(ResourceLocation skillId) {
        this.skillId = skillId;
    }

    public static BioengineeringSkillUnlockRequestPacket decode(FriendlyByteBuf buf) {
        return new BioengineeringSkillUnlockRequestPacket(buf.readResourceLocation());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeResourceLocation(skillId);
    }

    public static void handle(BioengineeringSkillUnlockRequestPacket msg, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();

        ctx.enqueueWork(() -> {
            if (ctx.getDirection() != NetworkDirection.PLAY_TO_SERVER) return;

            ServerPlayer player = ctx.getSender();
            if (player == null) return;

            BioengineeringSkillUtils.unlockAndSyncToClient(player, msg.skillId, false);
        });

        ctx.setPacketHandled(true);
    }
}
