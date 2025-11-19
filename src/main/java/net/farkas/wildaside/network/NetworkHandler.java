package net.farkas.wildaside.network;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.network.packets.*;
import net.farkas.wildaside.screen.bioengineering_workstation.BioengineeringWorkstationTab;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

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
                SyringeAnimPacket.class,
                SyringeAnimPacket::encode,
                SyringeAnimPacket::decode,
                SyringeAnimPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));
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

    public static void sendSyringeAnimPacket(boolean inwards, int slot) {
        if (CHANNEL == null) {
            WildAside.LOGGER.warn("Tried to send syringe animation update before network init. Ignoring.");
            return;
        }
        CHANNEL.send(PacketDistributor.SERVER.noArg(), new SyringeAnimPacket(inwards, slot));
    }
}