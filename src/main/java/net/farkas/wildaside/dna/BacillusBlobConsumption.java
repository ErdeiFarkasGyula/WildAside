package net.farkas.wildaside.dna;

import net.farkas.wildaside.WildAside;
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

import java.util.List;
import java.util.Map;

public class BacillusBlobConsumption {
    public static boolean consume(LivingEntity entity, CompoundTag blobTag) {
        WildAside.LOGGER.info("######################################");
        WildAside.LOGGER.info("# BACILLUS BLOB CONSUMPTION START");
        WildAside.LOGGER.info("# Entity: {}", entity.getName().getString());
        WildAside.LOGGER.info("######################################");

        DnaImplementation invaderDna = new DnaImplementation();
        invaderDna.deserializeNBT(blobTag);

        if (invaderDna.getLoci().isEmpty()) {
            WildAside.LOGGER.warn("Blob has no DNA data, consumption failed");
            return false;
        }

        WildAside.LOGGER.info("Invader DNA source: {}",
                invaderDna.getSource() != null ? invaderDna.getSource().toString() : "unknown");
        logDnaSummary("INVADER", invaderDna.getLoci());

        entity.getCapability(DnaCapability.INSTANCE).ifPresent(hostDna -> {
            Map<Trait, List<GeneLocus>> hostLoci = hostDna.getLoci();

            if (hostLoci.isEmpty()) {
                WildAside.LOGGER.info("--- PHASE 0: GENERATING BASE DNA FOR HOST ---");
                WildAside.LOGGER.info("Host has no existing DNA, generating base loci from entity attributes");

                hostLoci = DnaUtils.generateBaseLoci(entity);
                hostDna.setLoci(hostLoci);
                hostDna.setSource(entity.getType());
                hostDna.setStress(0f);

                WildAside.LOGGER.info("Generated {} base traits for {}", hostLoci.size(), entity.getType().toString());
                logDnaSummary("HOST (generated)", hostLoci);
            }

            Map<Trait, List<GeneLocus>> invaderLoci = invaderDna.getLoci();

            WildAside.LOGGER.info("Host stress before merge: {}", String.format("%.2f", hostDna.getStress()));
            logDnaSummary("HOST (before)", hostLoci);

            long currentTick = entity.level().getGameTime();
            long seed = entity.getUUID().getLeastSignificantBits() ^ currentTick;

            WildAside.LOGGER.info("--- PHASE 1: MERGE ---");
            MergeResult result = DnaMerger.merge(
                    hostLoci,
                    invaderLoci,
                    hostDna.getStress(),
                    currentTick,
                    seed
            );

            hostDna.setLoci(result.resultLoci());
            hostDna.setStress(hostDna.getStress() + result.stressGain());

            WildAside.LOGGER.info("Host stress after merge: {} (+{})",
                    String.format("%.2f", hostDna.getStress()),
                    String.format("%.2f", result.stressGain()));

            WildAside.LOGGER.info("--- PHASE 2: SIDE EFFECTS ---");
            RejectionSideEffects.applyImmediateEffects(entity, result);

            if (result.hasRejections()) {
                WildAside.LOGGER.info("--- PHASE 3: REJECTION MUTATIONS ---");
                List<GeneLocus> mutations = RejectionSideEffects.generateMutations(
                        result.resultLoci(),
                        result.getRejectedCount(),
                        seed
                );
                WildAside.LOGGER.info("Generated {} mutations from {} rejections", mutations.size(), result.getRejectedCount());
            }

            WildAside.LOGGER.info("--- PHASE 4: APPLY TO ENTITY ---");
            hostDna.recomputeAndApply(entity);

            logDnaSummary("HOST (after)", hostDna.getLoci());

            if (entity instanceof Player player) {
                sendMergeFeedback(player, result);
            }
        });

        WildAside.LOGGER.info("######################################");
        WildAside.LOGGER.info("# BACILLUS BLOB CONSUMPTION COMPLETE");
        WildAside.LOGGER.info("######################################");

        return true;
    }

    private static void logDnaSummary(String label, Map<Trait, List<GeneLocus>> loci) {
        WildAside.LOGGER.info("{} DNA Summary: {} traits", label, loci.size());
        for (var entry : loci.entrySet()) {
            Trait trait = entry.getKey();
            List<GeneLocus> group = entry.getValue();

            StringBuilder sb = new StringBuilder();
            sb.append("  [").append(trait.getName()).append("] ");
            sb.append(group.size()).append(" loci: ");

            for (GeneLocus locus : group) {
                sb.append(locus.getId())
                        .append("(").append(locus.getSource().name().charAt(0)).append(")")
                        .append(" ");
            }

            WildAside.LOGGER.info(sb.toString());
        }
    }

    private static void sendMergeFeedback(Player player, MergeResult result) {
        int integrated = result.getIntegratedCount();
        int transient_ = result.getTransientCount();
        int rejected = result.getRejectedCount();

        WildAside.LOGGER.info("Sending feedback to {}: {} integrated, {} transient, {} rejected", player.getName().getString(), integrated, transient_, rejected);

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