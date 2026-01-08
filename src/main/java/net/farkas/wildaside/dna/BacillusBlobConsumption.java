package net.farkas.wildaside.dna;

import net.farkas.wildaside.capability.dna.DnaCapability;
import net.farkas.wildaside.capability.dna.DnaImplementation;
import net.farkas.wildaside.dna.locus.GeneLocus;
import net.farkas.wildaside.dna.merge.DnaMerger;
import net.farkas.wildaside.dna.merge.MergeResult;
import net.farkas.wildaside.dna.merge.RejectionSideEffects;
import net.farkas.wildaside.dna.trait.Trait;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Map;

import static net.farkas.wildaside.dna.DnaConstants.DNA_DATA;

public class BacillusBlobConsumption {
    public static boolean consume(LivingEntity entity, CompoundTag blobTag) {
        DnaImplementation invaderDna = new DnaImplementation();
        invaderDna.deserializeNBT(blobTag);

        if (invaderDna.getLoci().isEmpty()) {
            return false;
        }

        entity.getCapability(DnaCapability.INSTANCE).ifPresent(hostDna -> {
            Map<Trait, List<GeneLocus>> hostLoci = hostDna.getLoci();
            Map<Trait, List<GeneLocus>> invaderLoci = invaderDna.getLoci();

            long currentTick = entity.level().getGameTime();
            long seed = entity.getUUID().getLeastSignificantBits() ^ currentTick;

            MergeResult result = DnaMerger.merge(
                    hostLoci,
                    invaderLoci,
                    hostDna.getStress(),
                    currentTick,
                    seed
            );

            hostDna.setLoci(result.resultLoci());
            hostDna.setStress(hostDna.getStress() + result.stressGain());

            RejectionSideEffects.applyImmediateEffects(entity, result);

            if (result.hasRejections()) {
                RejectionSideEffects.generateMutations(
                        result.resultLoci(),
                        result.getRejectedCount(),
                        seed
                );
            }

            hostDna.recomputeAndApply(entity);

            if (entity instanceof Player player) {
                sendMergeFeedback(player, result);
            }
        });

        return true;
    }

    private static void sendMergeFeedback(Player player, MergeResult result) {
        int integrated = result.getIntegratedCount();
        int transient_ = result.getTransientCount();
        int rejected = result.getRejectedCount();

        if (integrated > 0) {
            player.displayClientMessage(
                    Component.translatable("dna.wildaside.merge.integrated", integrated)
                            .withStyle(s -> s.withColor(0x44FF44)),
                    true
            );
        }

        if (transient_ > 0) {
            player.displayClientMessage(
                    Component.translatable("dna.wildaside.merge.transient", transient_)
                            .withStyle(s -> s.withColor(0xFFFF44)),
                    false
            );
        }

        if (rejected > 0) {
            player.displayClientMessage(
                    Component.translatable("dna.wildaside.merge.rejected", rejected)
                            .withStyle(s -> s.withColor(0xFF4444)),
                    false
            );
        }
    }
}