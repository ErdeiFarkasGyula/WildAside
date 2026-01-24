package net.farkas.wildaside.dna.bacillus_blob;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.capability.dna.DnaCapability;
import net.farkas.wildaside.capability.dna.DnaImplementation;
import net.farkas.wildaside.dna.chromosome.Chromosome;
import net.farkas.wildaside.dna.chromosome.Genome;
import net.farkas.wildaside.dna.merge.PendingDnaIntegration;
import net.farkas.wildaside.dna.sequence.GeneSequence;
import net.farkas.wildaside.dna.trait.Trait;
import net.farkas.wildaside.entity.custom.vibrion.BacillusBlobEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = WildAside.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BacillusBlobSpawnHandler {
    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();

        if (entity.level().isClientSide()) return;

        entity.getCapability(DnaCapability.INSTANCE).ifPresent(dna -> {
            List<PendingDnaIntegration> pendingIntegrations = dna.getPendingIntegrations();

            boolean hasPending = pendingIntegrations != null && !pendingIntegrations.isEmpty();

            if (!hasPending) {
                WildAside.LOGGER.debug("Entity {} died with no pending integrations",
                        entity.getName().getString());
                return;
            }

            Genome blobGenome = new Genome(entity.getType());

            if (hasPending) {
                for (PendingDnaIntegration pending : pendingIntegrations) {
                    Trait trait = pending.getTrait();
                    GeneSequence sequence = pending.getSequence();
                    if (trait != null && sequence != null) {
                        Chromosome chromo = blobGenome.getMaternal().getChromosome(trait.getTraitType().getChromosomeType());
                        chromo.setGeneSequence(trait, sequence);
                    }
                }
                WildAside.LOGGER.info("Added sequences from {} pending integrations", pendingIntegrations.size());
            }

            if (blobGenome.isEmpty()) {
                WildAside.LOGGER.warn("No sequences collected for blob despite having pending data");
                return;
            }

            spawnBlobEntity(entity, blobGenome, pendingIntegrations);

            dna.clearPendingIntegrations();
        });
    }

    private static void spawnBlobEntity(LivingEntity deadEntity, Genome blobGenome, List<PendingDnaIntegration> pendingIntegrations) {
        BacillusBlobEntity blob = new BacillusBlobEntity(
                deadEntity.level(),
                deadEntity.getX(),
                deadEntity.getY() + 0.5,
                deadEntity.getZ()
        );

        DnaImplementation blobDna = new DnaImplementation();
        blobDna.setGenome(blobGenome);

        WildAside.LOGGER.info("Created DnaImplementation for blob");

        blob.setCarriedDna(blobDna);

        int averageProgress = 0;
        if (pendingIntegrations != null && !pendingIntegrations.isEmpty()) {
            for (PendingDnaIntegration pending : pendingIntegrations) {
                averageProgress += pending.getCurrentProgress();
            }
            averageProgress /= pendingIntegrations.size();
        }

        float progressPercent = (float) averageProgress / (PendingDnaIntegration.DORMANT_DURATION + PendingDnaIntegration.DURATION_INTEGRATED);
        int lifespan = (int) (BacillusBlobEntity.DEFAULT_LIFESPAN * (1.0f - progressPercent * 0.5f));
        lifespan = Math.max(20 * 30, lifespan);

        blob.setLifespan(lifespan);

        double vx = (deadEntity.getRandom().nextDouble() - 0.5) * 0.4;
        double vy = 0.3 + deadEntity.getRandom().nextDouble() * 0.2;
        double vz = (deadEntity.getRandom().nextDouble() - 0.5) * 0.4;
        blob.setDeltaMovement(vx, vy, vz);

        deadEntity.level().addFreshEntity(blob);

        WildAside.LOGGER.info("Spawned bacillus blob entity at ({}, {}, {}) with pending DNA, lifespan {} ticks",
                blob.getX(), blob.getY(), blob.getZ(), lifespan);
    }
}