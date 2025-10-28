package net.farkas.wildaside.network;

import net.farkas.wildaside.capability.dna.DnaCapability;
import net.farkas.wildaside.dna.Gene;
import net.farkas.wildaside.dna.ability.Abilities;
import net.farkas.wildaside.dna.ability.IAbility;
import net.farkas.wildaside.dna.traits.TraitTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class UseAbilityPacket {
    public UseAbilityPacket() {}

    public static void encode(UseAbilityPacket msg, FriendlyByteBuf buf) {}
    public static UseAbilityPacket decode(FriendlyByteBuf buf) { return new UseAbilityPacket(); }

    public static void handle(UseAbilityPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            player.getCapability(DnaCapability.INSTANCE).ifPresent(dna -> {
                for (Gene gene : dna.genes().values()) {
                    if (gene.trait().traitType() == TraitTypes.ABILITY) {
                        IAbility ability = Abilities.get(gene.trait());
                        if (ability != null) {
                            ability.onUse(player, gene.value);
                            dna.setStability(dna.stability() - gene.trait().baseInstability() * 0.1f);
                        }
                    }
                }
            });
        });
        ctx.get().setPacketHandled(true);
    }
}