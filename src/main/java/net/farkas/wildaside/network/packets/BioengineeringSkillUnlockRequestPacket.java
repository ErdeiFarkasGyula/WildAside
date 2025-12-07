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
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class BioengineeringSkillUnlockRequestPacket {
    private final ResourceLocation skillId;
    private final boolean unlock;

    public BioengineeringSkillUnlockRequestPacket(ResourceLocation skillId, boolean unlock) {
        this.skillId = skillId;
        this.unlock = unlock;
    }

    public static BioengineeringSkillUnlockRequestPacket decode(FriendlyByteBuf buf) {
        ResourceLocation rl = buf.readResourceLocation();
        boolean unlock = buf.readBoolean();
        return new BioengineeringSkillUnlockRequestPacket(rl, unlock);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeResourceLocation(skillId);
        buf.writeBoolean(unlock);
    }

    public static void handle(BioengineeringSkillUnlockRequestPacket msg, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            if (ctx.getDirection() != NetworkDirection.PLAY_TO_SERVER) return;
            var sender = ctx.getSender();
            if (sender == null) return;

            if (msg.unlock) {
                BioengineeringSkillUtils.unlockAndSyncToClient(sender, msg.skillId, false);
            } else {
                BioengineeringSkillUtils.removeAndSyncToClient(sender, msg.skillId);
            }
        });
        ctx.setPacketHandled(true);
    }
}