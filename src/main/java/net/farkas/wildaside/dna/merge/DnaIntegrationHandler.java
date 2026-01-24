package net.farkas.wildaside.dna.merge;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.capability.dna.DnaCapability;
import net.farkas.wildaside.capability.dna.IDna;
import net.farkas.wildaside.dna.chromosome.Chromosome;
import net.farkas.wildaside.dna.chromosome.ChromosomeType;
import net.farkas.wildaside.dna.chromosome.Genome;
import net.farkas.wildaside.dna.sequence.GeneSequence;
import net.farkas.wildaside.dna.sequence.GeneSource;
import net.farkas.wildaside.dna.trait.Trait;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

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
        GeneSequence sequence = integration.getSequence();
        MergeOutcomeType outcome = integration.getOutcomeType();

        Genome genome = dna.getGenome();
        ChromosomeType chromoType = trait.getTraitType().getChromosomeType();
        Chromosome maternalChromo = genome.getMaternal().getChromosome(chromoType);
        Chromosome paternalChromo = genome.getPaternal().getChromosome(chromoType);

        GeneSequence.Builder builder = GeneSequence.builder()
                .trait(trait)
                .codingRegion(sequence.getCodingRegions().get(0))
                .dominance(sequence.getDominance())
                .mutationRate(sequence.getMutationRate())
                .stability(sequence.getStability());

        sequence.getActivators().forEach(builder::activator);
        sequence.getEnhancers().forEach(builder::enhancer);
        sequence.getSilencers().forEach(builder::silencer);
        sequence.getRegulators().forEach(builder::regulator);

        GeneSource newSource = switch (outcome) {
            case INTEGRATED, REPLACED -> GeneSource.INTEGRATED;
            case TRANSIENT -> GeneSource.INTEGRATED;
            case REJECTED -> GeneSource.MUTATED;
        };

        builder.source(GeneSource.INTEGRATED);

        System.out.println("INTEGRATING");
        GeneSequence finalSequence = builder.build();

        maternalChromo.setGeneSequence(trait, finalSequence);
        
        WildAside.LOGGER.info("Finalized integration for trait {} with outcome {}", trait.getName(), outcome);

        if (outcome == MergeOutcomeType.REJECTED) {
             long seed = entity.getUUID().getLeastSignificantBits() ^ entity.level().getGameTime();
             //RejectionSideEffects.generateMutations(genome, 1, seed);
        }

        dna.setGenome(genome);
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