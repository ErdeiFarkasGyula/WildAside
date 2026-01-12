package net.farkas.wildaside.dna.merge;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.capability.dna.DnaCapability;
import net.farkas.wildaside.capability.dna.IDna;
import net.farkas.wildaside.dna.locus.GeneLocus;
import net.farkas.wildaside.dna.locus.LocusSource;
import net.farkas.wildaside.dna.trait.Trait;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DnaIntegrationHandler {
    public static final int TICK_INTERVAL = 20;

    public static final int PROGRESS_PER_TICK = 20;

    public static void tickIntegrations(LivingEntity entity) {
        entity.getCapability(DnaCapability.INSTANCE).ifPresent(dna -> {
            List<PendingDnaIntegration> pending = dna.getPendingIntegrations();
            if (pending.isEmpty()) return;

            List<PendingDnaIntegration> completed = new ArrayList<>();
            List<PendingDnaIntegration> justAwakened = new ArrayList<>();
            float stressGain = 0f;

            for (PendingDnaIntegration integration : pending) {
                boolean wasDormant = integration.isDormant();
                PendingDnaIntegration.IntegrationPhase previousPhase = integration.getPhase();

                integration.incrementProgress(PROGRESS_PER_TICK);

                PendingDnaIntegration.IntegrationPhase currentPhase = integration.getPhase();

                if (wasDormant && !integration.isDormant()) {
                    justAwakened.add(integration);
                    WildAside.LOGGER.info("Integration exited dormancy: trait={}, outcome={}",
                            integration.getTrait().getName(), integration.getOutcomeType());
                }

                if (!integration.isDormant()) {
                    if (previousPhase != currentPhase && previousPhase != PendingDnaIntegration.IntegrationPhase.DORMANT) {
                        applyPhaseTransitionEffects(entity, integration, previousPhase, currentPhase);
                    }

                    stressGain += applyOngoingEffects(entity, integration);
                }

                if (integration.isComplete()) {
                    completed.add(integration);
                    WildAside.LOGGER.info("Integration complete: trait={}, outcome={}",
                            integration.getTrait().getName(), integration.getOutcomeType());
                }
            }

            if (!justAwakened.isEmpty() && entity instanceof Player player) {
                sendAwakeningFeedback(player, justAwakened);
            }

            if (stressGain > 0) {
                dna.setStress(dna.getStress() + stressGain);
            }

            for (PendingDnaIntegration integration : completed) {
                finalizeIntegration(entity, dna, integration);
                dna.removePendingIntegration(integration);
            }

            if (!completed.isEmpty()) {
                dna.recomputeAndApply(entity);

                if (entity instanceof Player player) {
                    sendCompletionFeedback(player, completed);
                }
            }
        });
    }

    private static void sendAwakeningFeedback(Player player, List<PendingDnaIntegration> awakened) {
        int integrated = 0, transient_ = 0, rejected = 0;

        for (PendingDnaIntegration p : awakened) {
            switch (p.getOutcomeType()) {
                case INTEGRATED, REPLACED -> integrated++;
                case TRANSIENT -> transient_++;
                case REJECTED -> rejected++;
            }
        }

        player.displayClientMessage(
                Component.translatable("dna.wildaside.integration.awakening", awakened.size())
                        .withStyle(s -> s.withColor(0xFF6600).withBold(true)),
                true
        );

        if (integrated > 0) {
            player.displayClientMessage(
                    Component.translatable("dna.wildaside.integration.awakening.integrated", integrated)
                            .withStyle(s -> s.withColor(0x44FF44)),
                    false
            );
        }

        if (transient_ > 0) {
            player.displayClientMessage(
                    Component.translatable("dna.wildaside.integration.awakening.transient", transient_)
                            .withStyle(s -> s.withColor(0xFFFF44)),
                    false
            );
        }

        if (rejected > 0) {
            player.displayClientMessage(
                    Component.translatable("dna.wildaside.integration.awakening.rejected", rejected)
                            .withStyle(s -> s.withColor(0xFF4444)),
                    false
            );
        }
    }


    private static void applyPhaseTransitionEffects(LivingEntity entity, PendingDnaIntegration integration, PendingDnaIntegration.IntegrationPhase from, PendingDnaIntegration.IntegrationPhase to) {
        MergeOutcomeType outcome = integration.getOutcomeType();

        switch (to) {
            case INITIAL -> {
                if (outcome == MergeOutcomeType.REJECTED) {
                    entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 40, 0, false, false, true));
                }

                if (entity instanceof Player player) {
                    player.displayClientMessage(
                            Component.translatable("dna.wildaside.integration.initial",
                                            Component.translatable("trait. wildaside." + integration.getTrait().getName()))
                                    .withStyle(s -> s.withColor(0xFFAA00)),
                            true
                    );
                }
            }
            case ADAPTING -> {
                if (outcome == MergeOutcomeType.REJECTED) {
                    entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 60, 0, false, false, true));
                    entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 80, 0, false, false, true));
                }
                else if (outcome == MergeOutcomeType.TRANSIENT) {
                    entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 0, false, false, true));
                }

                if (entity instanceof Player player) {
                    player.displayClientMessage(
                            Component.translatable("dna.wildaside.integration.adapting",
                                            Component.translatable("trait.wildaside." + integration.getTrait().getName()))
                                    .withStyle(s -> s.withColor(0xFFAA00)),
                            true
                    );
                }
            }
            case STABILIZING -> {
                if (outcome == MergeOutcomeType.REJECTED) {
                    entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 1, false, false, true));
                    entity.addEffect(new MobEffectInstance(MobEffects.HUNGER, 80, 0, false, false, true));
                }

                if (entity instanceof Player player) {
                    player.displayClientMessage(
                            Component.translatable("dna.wildaside.integration.stabilizing",
                                            Component.translatable("trait.wildaside." + integration.getTrait().getName()))
                                    .withStyle(s -> s.withColor(outcome == MergeOutcomeType.REJECTED ? 0xFF4444 : 0xFFFF00)),
                            true
                    );
                }
            }
            case FINALIZING -> {
                if (outcome == MergeOutcomeType.INTEGRATED || outcome == MergeOutcomeType.REPLACED) {
                    entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 40, 0, false, false, true));
                }

                if (entity instanceof Player player) {
                    player.displayClientMessage(
                            Component.translatable("dna.wildaside.integration.finalizing",
                                            Component.translatable("trait.wildaside." + integration.getTrait().getName()))
                                    .withStyle(s -> s.withColor(0x44FF44)),
                            true
                    );
                }
            }
        }
    }

    private static float applyOngoingEffects(LivingEntity entity, PendingDnaIntegration integration) {
        if (integration.isDormant()) {
            return 0f;
        }

        MergeOutcomeType outcome = integration.getOutcomeType();
        PendingDnaIntegration.IntegrationPhase phase = integration.getPhase();
        float stressGain = 0f;

        switch (outcome) {
            case INTEGRATED, REPLACED -> {
                if (phase == PendingDnaIntegration.IntegrationPhase.ADAPTING) {
                    stressGain = 0.1f;
                }
            }
            case TRANSIENT -> {
                stressGain = switch (phase) {
                    case INITIAL -> 0.05f;
                    case ADAPTING -> 0.15f;
                    case STABILIZING -> 0.1f;
                    default -> 0f;
                };
            }
            case REJECTED -> {
                stressGain = switch (phase) {
                    case INITIAL -> 0.1f;
                    case ADAPTING -> 0.25f;
                    case STABILIZING -> 0.3f;
                    case FINALIZING -> 0.15f;
                    default -> 0f;
                };

                if (phase == PendingDnaIntegration.IntegrationPhase.ADAPTING ||
                        phase == PendingDnaIntegration.IntegrationPhase.STABILIZING) {
                    if (entity.getRandom().nextFloat() < 0.1f) {
                        applyRandomRejectionEffect(entity);
                    }
                }
            }
        }

        return stressGain;
    }

    private static void applyRandomRejectionEffect(LivingEntity entity) {
        float roll = entity.getRandom().nextFloat();

        if (roll < 0.3f) {
            entity.addEffect(new MobEffectInstance(MobEffects.POISON, 40, 0, false, true, true));
        }
        else if (roll < 0.5f) {
            entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 60, 0, false, true, true));
        }
        else if (roll < 0.7f) {
            entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 80, 0, false, true, true));
        }
        else if (roll < 0.85f) {
            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 0, false, true, true));
        }
        else {
            entity.hurt(entity.damageSources().magic(), 1f);
        }
    }

    private static void finalizeIntegration(LivingEntity entity, IDna dna, PendingDnaIntegration integration) {
        Trait trait = integration.getTrait();
        GeneLocus locus = integration.getLocus();
        MergeOutcomeType outcome = integration.getOutcomeType();

        Map<Trait, List<GeneLocus>> allLoci = new HashMap<>(dna.getGenomeLociView());
        List<GeneLocus> existingLoci = allLoci.computeIfAbsent(trait, k -> new ArrayList<>());

        switch (outcome) {
            case INTEGRATED, TRANSIENT -> {
                if (existingLoci.size() < DnaMerger.MAX_LOCI_PER_TRAIT) {
                    existingLoci.add(locus);
                    WildAside.LOGGER.info("Added {} locus for trait {}", outcome, trait.getName());
                }
            }
            case REPLACED -> {
                if (!existingLoci.isEmpty()) {
                    existingLoci.removeIf(l -> l.getSource() != LocusSource.NATIVE);
                }
                existingLoci.add(locus);
                WildAside.LOGGER.info("Replaced locus for trait {}", trait.getName());
            }
            case REJECTED -> {
                if (existingLoci.size() < DnaMerger.MAX_LOCI_PER_TRAIT) {
                    existingLoci.add(locus);
                    WildAside.LOGGER.info("Added rejected locus for trait {} (will degrade)", trait.getName());
                }

                long seed = entity.getUUID().getLeastSignificantBits() ^ entity.level().getGameTime();
                RejectionSideEffects.generateMutations(allLoci, 1, seed);
            }
        }

        dna.setGenomeFromLoci(allLoci);
        removeFromInvadingLoci(dna, trait, locus);
    }

    private static void removeFromInvadingLoci(IDna dna, Trait trait, GeneLocus locus) {
        Map<Trait, List<GeneLocus>> invading = dna.getInvadingLoci();
        List<GeneLocus> traitLoci = invading.get(trait);

        if (traitLoci != null) {
            traitLoci.removeIf(l -> l.getId().equals(locus.getId()));
            if (traitLoci. isEmpty()) {
                invading. remove(trait);
            }
            WildAside.LOGGER. debug("Removed locus {} from invading loci for trait {}",
                    locus.getId(), trait.getName());
        }
    }

    private static void sendCompletionFeedback(Player player, List<PendingDnaIntegration> completed) {
        int integrated = 0, transient_ = 0, rejected = 0;

        for (PendingDnaIntegration p : completed) {
            switch (p.getOutcomeType()) {
                case INTEGRATED, REPLACED -> integrated++;
                case TRANSIENT -> transient_++;
                case REJECTED -> rejected++;
            }
        }

        if (integrated > 0) {
            player.displayClientMessage(
                    Component.translatable("dna.wildaside.integration.complete.integrated", integrated)
                            .withStyle(s -> s.withColor(0x44FF44)),
                    false
            );
        }

        if (transient_ > 0) {
            player.displayClientMessage(
                    Component.translatable("dna.wildaside.integration.complete.transient", transient_)
                            .withStyle(s -> s.withColor(0xFFFF44)),
                    false
            );
        }

        if (rejected > 0) {
            player.displayClientMessage(
                    Component.translatable("dna.wildaside.integration.complete.rejected", rejected)
                            .withStyle(s -> s.withColor(0xFF4444)),
                    false
            );
        }
    }
}