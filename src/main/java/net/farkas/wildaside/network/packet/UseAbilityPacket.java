package net.farkas.wildaside.network.packet;

import net.farkas.wildaside.capability.dna.DnaCapability;
import net.farkas.wildaside.dna.DnaUtils;
import net.farkas.wildaside.dna.Gene;
import net.farkas.wildaside.dna.ability.AbilityRegistry;
import net.farkas.wildaside.dna.ability.IAbility;
import net.farkas.wildaside.dna.allele.value.FloatAlleleValue;
import net.farkas.wildaside.dna.locus.GeneLocus;
import net.farkas.wildaside.dna.locus.LocusFlag;
import net.farkas.wildaside.dna.trait.Trait;
import net.farkas.wildaside.dna.trait.TraitType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.Map;
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
                for (Map.Entry<Trait, List<GeneLocus>> entry : dna.getLoci().entrySet()) {
                    Trait trait = entry.getKey();
                    if (trait.getTraitType() != TraitType.ABILITY) continue;

                    boolean hasActivator = entry.getValue().stream().anyMatch(l -> l.getFlags().contains(LocusFlag.ACTIVATOR));
                    if (hasActivator) {
                        boolean activatorOn = entry.getValue().stream()
                                .filter(l -> l.getFlags().contains(LocusFlag.ACTIVATOR))
                                .map(GeneLocus::getExpressedValue)
                                .anyMatch(v -> v instanceof FloatAlleleValue fv && fv.get() > 0f);
                        if (!activatorOn) continue;
                    }

                    Gene gene = DnaUtils.asGene(trait, entry.getValue());
                    if (gene == null) continue;

                    if (gene.getExpressedValueHolder() instanceof FloatAlleleValue floatVal) {
                        IAbility ability = AbilityRegistry.get(trait);
                        if (ability != null && floatVal.get() > 0f) {
                            ability.onUse(player, floatVal.get());


                            boolean hasSide = entry.getValue().stream().anyMatch(l -> l.getFlags().contains(LocusFlag.SIDE_EFFECT));
                            float stressGain = trait.getInstabilityModifier() * (hasSide ? 0.75f : 0.5f);
                            dna.setStress(dna.getStress() + stressGain);
                        }
                    }
                }
            });
        });
        ctx.get().setPacketHandled(true);
    }
}