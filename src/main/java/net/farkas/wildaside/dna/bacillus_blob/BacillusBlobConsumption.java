package net.farkas.wildaside.dna.bacillus_blob;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.capability.dna.DnaCapability;
import net.farkas.wildaside.capability.dna.DnaImplementation;
import net.farkas.wildaside.dna.locus.GeneLocus;
import net.farkas.wildaside.dna.locus.LocusSource;
import net.farkas.wildaside.dna.merge.*;
import net.farkas.wildaside.dna.trait.Trait;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.farkas.wildaside.dna.DnaConstants.DNA_DATA;

public class BacillusBlobConsumption {
    public static boolean consume(LivingEntity entity, CompoundTag blobTag) {
        WildAside.LOGGER.info("######################################");
        WildAside.LOGGER.info("# BACILLUS BLOB CONSUMPTION START");
        WildAside.LOGGER.info("# Entity: {}", entity.getName().getString());
        WildAside.LOGGER.info("######################################");

        WildAside.LOGGER.debug("Blob tag keys: {}", blobTag.getAllKeys());

        if (!blobTag.contains(DNA_DATA)) {
            WildAside.LOGGER.warn("Blob has no DNA_DATA key, consumption failed");
            return false;
        }

        CompoundTag dnaTag = blobTag.getCompound(DNA_DATA);
        if (dnaTag.isEmpty()) {
            WildAside.LOGGER.warn("DNA_DATA is empty, consumption failed");
            return false;
        }

        DnaImplementation invaderDna = new DnaImplementation();
        invaderDna.deserializeNBT(dnaTag);

        WildAside.LOGGER.info("Invader DNA source: {}", invaderDna.getSource());
        WildAside.LOGGER.info("Invader DNA loci count: {}", invaderDna.getLoci().size());

        if (invaderDna.getLoci().isEmpty()) {
            WildAside.LOGGER.warn("Invader DNA has no loci after deserialization, consumption failed");
            return false;
        }

        for (Map.Entry<Trait, List<GeneLocus>> entry : invaderDna.getLoci().entrySet()) {
            WildAside.LOGGER.info("  Invader trait [{}]: {} loci", entry.getKey().getName(), entry.getValue().size());
        }

        final boolean[] success = {false};

        entity.getCapability(DnaCapability.INSTANCE).ifPresent(hostDna -> {
            Map<Trait, List<GeneLocus>> hostLoci = hostDna.getLoci();
            Map<Trait, List<GeneLocus>> invaderLoci = invaderDna.getLoci();

            WildAside.LOGGER.info("Host DNA loci count: {}", hostLoci.size());
            WildAside.LOGGER.info("Host stress before consumption: {}", hostDna.getStress());
            WildAside.LOGGER.info("Host pending integrations:  {}", hostDna.getPendingIntegrations().size());
            WildAside.LOGGER.info("Host existing invading loci: {}", hostDna.getInvadingLoci().size());

            long currentTick = entity.level().getGameTime();
            long seed = entity.getUUID().getLeastSignificantBits() ^ currentTick;

            MergeResult result = DnaMerger.merge(
                    hostLoci,
                    invaderLoci,
                    hostDna.getStress(),
                    currentTick,
                    seed
            );

            WildAside.LOGGER.info("Merge analysis complete:");
            WildAside.LOGGER.info("  - Will integrate:  {}", result.getIntegratedCount());
            WildAside.LOGGER.info("  - Will be transient: {}", result.getTransientCount());
            WildAside.LOGGER.info("  - Will be rejected: {}", result.getRejectedCount());
            WildAside.LOGGER.info("  - Estimated stress gain: {}", result.stressGain());

            Map<Trait, List<GeneLocus>> newInvadingLoci = new HashMap<>();

            int queued = 0;
            for (MergeEvent event : result.events()) {
                GeneLocus locusToIntegrate = event.invadingLocus();
                MergeOutcomeType outcomeType = event.outcome().type();

                LocusSource source = switch (outcomeType) {
                    case INTEGRATED, REPLACED -> LocusSource.INTEGRATED;
                    case TRANSIENT -> LocusSource.TRANSIENT;
                    case REJECTED -> LocusSource.REJECTED;
                };

                GeneLocus taggedLocus = locusToIntegrate.withSource(source, currentTick);

                newInvadingLoci.computeIfAbsent(event.trait(), k -> new ArrayList<>()).add(taggedLocus);

                PendingDnaIntegration pending = new PendingDnaIntegration(
                        event.trait(),
                        taggedLocus,
                        outcomeType,
                        currentTick
                );

                hostDna.addPendingIntegration(pending);
                queued++;

                WildAside.LOGGER.debug("Queued integration:  trait={}, outcome={}, duration={}t",
                        event.trait().getName(), outcomeType, pending.getTotalDuration());
            }

            hostDna.addInvadingLoci(newInvadingLoci);

            WildAside.LOGGER.info("Stored {} invading loci for {} traits",
                    newInvadingLoci.values().stream().mapToInt(List::size).sum(),
                    newInvadingLoci.size());
            WildAside.LOGGER.info("Queued {} loci for gradual integration", queued);

            float initialStress = result.stressGain() * 0.3f;
            hostDna.setStress(hostDna.getStress() + initialStress);

            WildAside.LOGGER.info("Initial stress applied: {} (total: {})", initialStress, hostDna.getStress());

            if (entity instanceof Player player) {
                sendConsumptionFeedback(player, result, queued);
            }

            success[0] = true;
        });

        WildAside.LOGGER.info("######################################");
        WildAside.LOGGER.info("# BACILLUS BLOB CONSUMPTION END");
        WildAside.LOGGER.info("######################################");

        return success[0];
    }

    private static void sendConsumptionFeedback(Player player, MergeResult result, int queued) {
        player.displayClientMessage(
                Component.translatable("dna.wildaside.blob.consumed", queued)
                        .withStyle(s -> s.withColor(0x44FF44)),
                true
        );

        int integrated = result.getIntegratedCount();
        int transient_ = result.getTransientCount();
        int rejected = result.getRejectedCount();

        if (integrated > 0) {
            player.displayClientMessage(
                    Component.translatable("dna.wildaside.merge.integrating_count", integrated)
                            .withStyle(s -> s.withColor(0x44FF44)),
                    false
            );
        }

        if (transient_ > 0) {
            player.displayClientMessage(
                    Component.translatable("dna.wildaside.merge.transient_count", transient_)
                            .withStyle(s -> s.withColor(0xFFFF44)),
                    false
            );
        }

        if (rejected > 0) {
            player.displayClientMessage(
                    Component.translatable("dna.wildaside.merge.rejecting_count", rejected)
                            .withStyle(s -> s.withColor(0xFF4444)),
                    false
            );
        }

        player.displayClientMessage(
                Component.translatable("dna.wildaside.blob.integration_time")
                        .withStyle(s -> s.withColor(0xAAAAAA).withItalic(true)),
                false
        );
    }
}