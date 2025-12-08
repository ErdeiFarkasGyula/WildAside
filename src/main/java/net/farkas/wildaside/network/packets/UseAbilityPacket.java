package net.farkas.wildaside.network.packets;

import net.farkas.wildaside.capability.dna.DnaCapability;
import net.farkas.wildaside.dna.Gene;
import net.farkas.wildaside.dna.ability.AbilityRegistry;
import net.farkas.wildaside.dna.ability.IAbility;
import net.farkas.wildaside.dna.trait.TraitType;
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
                for (Gene gene : dna.getGenes().values()) {
                    if (gene.getTrait().getTraitType() == TraitType.ABILITY) {
                        IAbility ability = AbilityRegistry.get(gene.getTrait());
                        if (ability != null && gene.getExpressedValue() != 0) {
                            ability.onUse(player, gene.getExpressedValue());
                            dna.setStability(dna.getStability() - gene.getTrait().getInstabilityModifier() * 0.5f);
                        }
                    }
                }
            });
        });
        ctx.get().setPacketHandled(true);
    }
}