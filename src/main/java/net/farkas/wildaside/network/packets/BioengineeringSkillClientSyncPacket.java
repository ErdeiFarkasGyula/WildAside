package net.farkas.wildaside.network.packets;

import net.farkas.wildaside.capability.bioengineering.BioengineeringSkillsCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class BioengineeringSkillClientSyncPacket {
    private final Set<ResourceLocation> skills;
    private final int points;

    public BioengineeringSkillClientSyncPacket(Set<ResourceLocation> skills, int points) {
        this.points = points;
        this.skills = skills;
    }

    public static BioengineeringSkillClientSyncPacket decode(FriendlyByteBuf buf) {
        int points = buf.readVarInt();
        int size = buf.readVarInt();
        Set<ResourceLocation> skills;
        skills = new HashSet<>();
        for (int i = 0; i < size; i++) {
            skills.add(buf.readResourceLocation());
        }
        return new BioengineeringSkillClientSyncPacket(skills, points);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(points);
        buf.writeVarInt(skills.size());
        for (ResourceLocation rl : skills)
            buf.writeResourceLocation(rl);
    }

    public void handle(Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            Player player = Minecraft.getInstance().player;
            player.getCapability(BioengineeringSkillsCapability.INSTANCE).ifPresent(cap -> {
                cap.setSkills(this.skills);
                cap.setPoints(this.points);
            });
        });
        ctx.setPacketHandled(true);
    }
}
