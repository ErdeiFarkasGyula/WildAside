package net.farkas.wildaside.network.packet;

import net.farkas.wildaside.capability.dna.DnaCapability;
import net.farkas.wildaside.dna.Gene;
import net.farkas.wildaside.dna.ability.AbilityRegistry;
import net.farkas.wildaside.dna.ability.IAbility;
import net.farkas.wildaside.dna.chromosome.Chromosome;
import net.farkas.wildaside.dna.chromosome.Genome;
import net.farkas.wildaside.dna.expression.ExpressionContext;
import net.farkas.wildaside.dna.sequence.GeneSequence;
import net.farkas.wildaside.dna.trait.Trait;
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
                Genome genome = dna.getGenome();
                if (genome == null) return;

                ExpressionContext context = new ExpressionContext(player);

                for (Chromosome chromo : genome.getMaternal().getAllChromosomes()) {
                    for (GeneSequence seq : chromo.getAllGeneSequences()) {
                        Trait trait = seq.getTrait();
                        if (trait.getTraitType() != TraitType.ABILITY) continue;

                        float value = genome.getExpressedValue(trait, context);
                        if (value <= 0f) continue;

                        IAbility ability = AbilityRegistry.get(trait);
                        if (ability != null) {
                            ability.onUse(player, value);
                            float stressGain = trait.getInstabilityModifier() * 0.5f;
                            dna.setStress(dna.getStress() + stressGain);
                        }
                    }
                }
            });
        });
        ctx.get().setPacketHandled(true);
    }
}