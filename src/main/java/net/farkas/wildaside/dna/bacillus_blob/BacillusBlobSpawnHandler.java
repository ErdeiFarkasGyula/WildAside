package net.farkas.wildaside.dna.bacillus_blob;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.capability.dna.DnaCapability;
import net.farkas.wildaside.capability.dna.DnaImplementation;
import net.farkas.wildaside.dna.locus.GeneLocus;
import net.farkas.wildaside.dna.merge.PendingDnaIntegration;
import net.farkas.wildaside.dna.trait.Trait;
import net.farkas.wildaside.entity.custom.vibrion.BacillusBlobEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = WildAside.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BacillusBlobSpawnHandler {
    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();

        if (entity.level().isClientSide()) return;

        entity.getCapability(DnaCapability.INSTANCE).ifPresent(dna -> {
            Map<Trait, List<GeneLocus>> invadingLoci = dna.getInvadingLoci();
            List<PendingDnaIntegration> pendingIntegrations = dna.getPendingIntegrations();

            boolean hasInvading = invadingLoci != null && !invadingLoci.isEmpty();
            boolean hasPending = pendingIntegrations != null && !pendingIntegrations.isEmpty();

            if (!hasInvading && !hasPending) {
                WildAside.LOGGER.debug("Entity {} died with no invading loci or pending integrations",
                        entity.getName().getString());
                return;
            }

            Map<Trait, List<GeneLocus>> lociForBlob = new HashMap<>();

            if (hasInvading) {
                for (Map.Entry<Trait, List<GeneLocus>> entry : invadingLoci.entrySet()) {
                    List<GeneLocus> copied = new ArrayList<>(entry.getValue());
                    lociForBlob.put(entry.getKey(), copied);
                }
                WildAside.LOGGER.info("Copied {} traits from invadingLoci", lociForBlob.size());
            }

            if (hasPending) {
                for (PendingDnaIntegration pending : pendingIntegrations) {
                    Trait trait = pending.getTrait();
                    GeneLocus locus = pending.getLocus();
                    if (trait != null && locus != null) {
                        lociForBlob.computeIfAbsent(trait, k -> new ArrayList<>()).add(locus);
                    }
                }
                WildAside.LOGGER.info("Added loci from {} pending integrations", pendingIntegrations.size());
            }

            if (lociForBlob.isEmpty()) {
                WildAside.LOGGER.warn("No loci collected for blob despite having invading/pending data");
                return;
            }

            int totalLoci = lociForBlob.values().stream().mapToInt(List::size).sum();
            WildAside.LOGGER.info("Entity {} died with {} invading traits ({} loci)",
                    entity.getName().getString(), lociForBlob.size(), totalLoci);

            for (Map.Entry<Trait, List<GeneLocus>> entry : lociForBlob.entrySet()) {
                WildAside.LOGGER.info("  Trait [{}]: {} loci", entry.getKey().getName(), entry.getValue().size());
                for (GeneLocus locus : entry.getValue()) {
                    WildAside.LOGGER.info("    - {} [{}]", locus.getId(), locus.getSource());
                }
            }

            spawnBlobEntity(entity, lociForBlob, pendingIntegrations);

            dna.clearPendingIntegrations();
            dna.clearInvadingLoci();
        });
    }

    private static void spawnBlobEntity(LivingEntity deadEntity, Map<Trait, List<GeneLocus>> lociForBlob, List<PendingDnaIntegration> pendingIntegrations) {
        BacillusBlobEntity blob = new BacillusBlobEntity(
                deadEntity.level(),
                deadEntity.getX(),
                deadEntity.getY() + 0.5,
                deadEntity.getZ()
        );

        DnaImplementation blobDna = new DnaImplementation();
        Map<Trait, List<GeneLocus>> copiedLoci = new HashMap<>();

        for (Map.Entry<Trait, List<GeneLocus>> entry : lociForBlob.entrySet()) {
            copiedLoci.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }

        blobDna.setGenomeFromLoci(copiedLoci);

        WildAside.LOGGER.info("Created DnaImplementation for blob with {} traits", blobDna.getGenomeLociView().size());

        blob.setCarriedDna(blobDna);

        WildAside.LOGGER.info("After setCarriedDna - blob. hasDna(): {}", blob.hasDna());
        WildAside.LOGGER.info("After setCarriedDna - blob.getCarriedDna().getGenomeLociView().size(): {}",
                blob.getCarriedDna().getGenomeLociView().size());

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

        int totalLoci = lociForBlob.values().stream().mapToInt(List::size).sum();
        WildAside.LOGGER.info("Spawned bacillus blob entity at ({}, {}, {}) with {} traits ({} loci), lifespan {} ticks",
                blob.getX(), blob.getY(), blob.getZ(),
                lociForBlob.size(), totalLoci, lifespan);
    }
}