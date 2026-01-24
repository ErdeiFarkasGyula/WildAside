package net.farkas.wildaside.dna.bacillus_blob;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.capability.dna.DnaCapability;
import net.farkas.wildaside.capability.dna.DnaImplementation;
import net.farkas.wildaside.dna.DnaUtils;
import net.farkas.wildaside.dna.chromosome.Chromosome;
import net.farkas.wildaside.dna.chromosome.ChromosomeSet;
import net.farkas.wildaside.dna.chromosome.Genome;
import net.farkas.wildaside.dna.sequence.GeneSequence;
import net.farkas.wildaside.dna.sequence.GeneSource;
import net.farkas.wildaside.dna.trait.Trait;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.Random;

import static net.farkas.wildaside.dna.DnaConstants.DNA_DATA;

public class BacillusBlobConsumption {
    private static final float INTEGRATION_CHANCE = 0.4f;
    private static final float REJECTION_CHANCE = 0.3f;
    private static final float STRESS_PER_SEQUENCE = 2.0f;

    public static boolean consume(LivingEntity entity, CompoundTag blobTag) {
        WildAside.LOGGER.info("######################################");
        WildAside.LOGGER.info("# BACILLUS BLOB CONSUMPTION START (SEQUENCE-BASED)");
        WildAside.LOGGER.info("# Entity: {}", entity.getName().getString());
        WildAside.LOGGER.info("######################################");

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

        Genome invaderGenome = invaderDna.getGenome();
        if (invaderGenome == null || invaderGenome.getMaternal().getAllChromosomes().isEmpty()) {
            WildAside.LOGGER.warn("Invader genome has no sequences, consumption failed");
            return false;
        }

        WildAside.LOGGER.info("Invader genome source: {}", invaderGenome.getEntityType());
        WildAside.LOGGER.info("Invader maternal chromosomes: {}", 
                invaderGenome.getMaternal().getAllChromosomes().size());

        final boolean[] success = {false};

        entity.getCapability(DnaCapability.INSTANCE).ifPresent(hostDna -> {
            Genome hostGenome = hostDna.getGenome();
            if (hostGenome == null) {
                hostGenome = DnaUtils.generateBaseGenome(entity);
                hostDna.setGenome(hostGenome);
            }

            WildAside.LOGGER.info("Host genome chromosomes: {}", 
                    hostGenome.getMaternal().getAllChromosomes().size());
            WildAside.LOGGER.info("Host stress before consumption: {}", hostDna.getStress());

            long currentTick = entity.level().getGameTime();
            long seed = entity.getUUID().getLeastSignificantBits() ^ currentTick;
            Random random = new Random(seed);

            int integrated = 0;
            int transient_ = 0;
            int rejected = 0;
            float totalStress = 0f;

            ChromosomeSet invaderMaternalSet = invaderGenome.getMaternal();

            for (Chromosome invaderChromosome : invaderMaternalSet.getAllChromosomes()) {
                for (Trait trait : net.farkas.wildaside.dna.trait.TraitRegistry.getAllTraits()) {
                    GeneSequence invaderSeq = invaderChromosome.getGeneSequence(trait);
                    if (invaderSeq == null) continue;

                    float roll = random.nextFloat();
                    GeneSource newSource;
                    
                    if (roll < INTEGRATION_CHANCE) {
                        newSource = GeneSource.INTEGRATED;
                        integrated++;
                        totalStress += STRESS_PER_SEQUENCE * 0.8f;
                    } else if (roll < INTEGRATION_CHANCE + REJECTION_CHANCE) {
                        newSource = GeneSource.MUTATED;
                        rejected++;
                        totalStress += STRESS_PER_SEQUENCE * 0.5f;
                    } else {
                        newSource = GeneSource.INTEGRATED;
                        transient_++;
                        totalStress += STRESS_PER_SEQUENCE;
                    }

                    GeneSequence.Builder builder = GeneSequence.builder()
                            .trait(trait)
                            .dominance(invaderSeq.getDominance())
                            .mutationRate(invaderSeq.getMutationRate())
                            .stability(invaderSeq.getStability())
                            .source(newSource);
                    
                    invaderSeq.getCodingRegions().forEach(builder::codingRegion);
                    invaderSeq.getActivators().forEach(builder::activator);
                    invaderSeq.getEnhancers().forEach(builder::enhancer);
                    invaderSeq.getSilencers().forEach(builder::silencer);
                    invaderSeq.getRegulators().forEach(builder::regulator);
                    
                    GeneSequence taggedSeq = builder.build();

                    if (newSource == GeneSource.INTEGRATED || newSource == GeneSource.MUTATED) {
                        Chromosome hostMaternal = hostGenome.getMaternal().getChromosome(trait.getTraitType().getChromosomeType());
                        hostMaternal.setGeneSequence(trait, taggedSeq);
                        WildAside.LOGGER.debug("Integrated sequence for trait: {}", trait.getName());
                    }
                }
            }

            hostDna.setStress(hostDna.getStress() + (totalStress * 0.3f));

            WildAside.LOGGER.info("Merge complete:");
            WildAside.LOGGER.info("  - Integrated: {}", integrated);
            WildAside.LOGGER.info("  - Transient: {}", transient_);
            WildAside.LOGGER.info("  - Rejected: {}", rejected);
            WildAside.LOGGER.info("  - Total stress added: {}", totalStress * 0.3f);

            if (entity instanceof Player player) {
                sendConsumptionFeedback(player, integrated, transient_, rejected);
            }

            success[0] = true;
        });

        WildAside.LOGGER.info("######################################");
        WildAside.LOGGER.info("# BACILLUS BLOB CONSUMPTION END");
        WildAside.LOGGER.info("######################################");

        return success[0];
    }

    private static void sendConsumptionFeedback(Player player, int integrated, int transient_, int rejected) {
        int total = integrated + transient_ + rejected;
        player.displayClientMessage(
                Component.translatable("dna.wildaside.blob.consumed", total)
                        .withStyle(s -> s.withColor(0x44FF44)),
                true
        );

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