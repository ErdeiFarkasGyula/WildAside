package net.farkas.wildaside.network;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.network.packets.*;
import net.farkas.wildaside.screen.bioengineering_workstation.BioengineeringWorkstationTab;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;
import java.util.Set;

public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1";
    private static SimpleChannel CHANNEL = null;
    private static int packetId = 0;
    private static int id() { return packetId++; }

    public static void init(final FMLCommonSetupEvent event) {
        if (CHANNEL != null) return;

        CHANNEL = NetworkRegistry.newSimpleChannel(
                new ResourceLocation(WildAside.MOD_ID, "main"),
                () -> PROTOCOL_VERSION,
                PROTOCOL_VERSION::equals,
                PROTOCOL_VERSION::equals
        );

        CHANNEL.registerMessage(id(),
                WindSyncPacket.class,
                WindSyncPacket::encode,
                WindSyncPacket::decode,
                WindSyncPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));

        CHANNEL.registerMessage(id(),
                UseAbilityPacket.class,
                UseAbilityPacket::encode,
                UseAbilityPacket::decode,
                UseAbilityPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));

        CHANNEL.registerMessage(id(),
                BioengineeringWorkstationTabPacket.class,
                BioengineeringWorkstationTabPacket::encode,
                BioengineeringWorkstationTabPacket::decode,
                BioengineeringWorkstationTabPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));

        CHANNEL.registerMessage(id(),
                RecompileDnaPacket.class,
                RecompileDnaPacket::encode,
                RecompileDnaPacket::decode,
                RecompileDnaPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));

        CHANNEL.registerMessage(id(),
                SyringeDataPacket.class,
                SyringeDataPacket::encode,
                SyringeDataPacket::decode,
                SyringeDataPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));

        CHANNEL.registerMessage(id(),
                BioengineeringSkillUnlockRequestPacket.class,
                BioengineeringSkillUnlockRequestPacket::encode,
                BioengineeringSkillUnlockRequestPacket::decode,
                BioengineeringSkillUnlockRequestPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));

        CHANNEL.registerMessage(id(),
                BioengineeringSkillClientSyncPacket.class,
                BioengineeringSkillClientSyncPacket::encode,
                BioengineeringSkillClientSyncPacket::decode,
                BioengineeringSkillClientSyncPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    public static void sendWindUpdateToAll(Vec3 dir, float strength) {
        if (CHANNEL == null) {
            WildAside.LOGGER.warn("Tried to send wind update before network init. Ignoring.");
            return;
        }
        CHANNEL.send(PacketDistributor.ALL.noArg(), new WindSyncPacket(dir, strength));
    }

    public static void sendAbilityKeyUpdate() {
        if (CHANNEL == null) {
            WildAside.LOGGER.warn("Tried to send ability key update before network init. Ignoring.");
            return;
        }
        CHANNEL.send(PacketDistributor.SERVER.noArg(), new UseAbilityPacket());
    }

    public static void sendBioengineeringWorkstationTabPacket(BioengineeringWorkstationTab tab) {
        if (CHANNEL == null) {
            WildAside.LOGGER.warn("Tried to send bioengineering workstation tab update before network init. Ignoring.");
            return;
        }
        CHANNEL.send(PacketDistributor.SERVER.noArg(), new BioengineeringWorkstationTabPacket(tab.ordinal()));
    }

    public static void sendBioengineeringWorkstationRecompileGenesPacket(BlockPos pos) {
        if (CHANNEL == null) {
            WildAside.LOGGER.warn("Tried to send bioengineering workstation gene recompiling update before network init. Ignoring.");
            return;
        }
        CHANNEL.send(PacketDistributor.SERVER.noArg(), new RecompileDnaPacket(pos));
    }

    public static void sendSyringeDataClientSyncPacket(ServerPlayer player, int slot, float progress, float fluid, boolean animating, boolean inwards,
                                                       String fluidType, int fluidColor, int dirtiness, long creationTick, long freezerTicks, boolean multipleSources) {
        if (CHANNEL == null) {
            WildAside.LOGGER.warn("Tried to send send syringe data client sync packet before network init. Ignoring.");
            return;
        }

        NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player),
                new SyringeDataPacket(player.getUUID(), slot, progress, fluid, animating, inwards, fluidType, fluidColor, dirtiness, creationTick, freezerTicks, multipleSources)
        );
    }

    public static void sendBioengineeringSkillRequestPacket(ResourceLocation skillId) {
        if (CHANNEL == null) {
            WildAside.LOGGER.warn("Tried to send bioengineering skill unlock request before network init. Ignoring.");
            return;
        }
        CHANNEL.send(PacketDistributor.ALL.noArg(), new BioengineeringSkillUnlockRequestPacket(skillId));
    }

    public static void sendBioengineeringSkillClientSyncPacket(Set<ResourceLocation> skills, int points) {
        if (CHANNEL == null) {
            WildAside.LOGGER.warn("Tried to send bioengineering skill sync packet before network init. Ignoring.");
            return;
        }
        CHANNEL.send(PacketDistributor.ALL.noArg(), new BioengineeringSkillClientSyncPacket(skills, points));
    }
}