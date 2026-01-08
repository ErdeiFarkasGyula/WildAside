package net.farkas.wildaside.dna.merge;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.capability.dna.DnaCapability;
import net.farkas.wildaside.dna.DnaUtils;
import net.farkas.wildaside.dna.locus.GeneLocus;
import net.farkas.wildaside.dna.locus.LocusSource;
import net.farkas.wildaside.dna.trait.Trait;
import net.minecraft.world.entity.LivingEntity;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DnaDegradationHandler {
    public static final float TRANSIENT_DEGRADATION_RATE = 0.005f;
    public static final float REJECTED_DEGRADATION_RATE = 0.02f;

    public static final float STABILIZATION_CHANCE = 0.002f;

    public static void tickDegradation(LivingEntity entity) {
        entity.getCapability(DnaCapability.INSTANCE).ifPresent(dna -> {
            Map<Trait, List<GeneLocus>> loci = dna.getLoci();
            boolean changed = false;
            long seed = entity.getUUID().getLeastSignificantBits() ^ entity.tickCount;

            int totalDegrading = 0;
            int removed = 0;
            int stabilized = 0;

            for (var entry : loci.entrySet()) {
                Trait trait = entry.getKey();
                List<GeneLocus> group = entry.getValue();
                Iterator<GeneLocus> iter = group.iterator();

                while (iter.hasNext()) {
                    GeneLocus locus = iter.next();

                    if (locus.getSource().isDegrading()) {
                        totalDegrading++;

                        float baseRate = locus.getSource() == LocusSource.REJECTED
                                ? REJECTED_DEGRADATION_RATE
                                : TRANSIENT_DEGRADATION_RATE;

                        float rate = baseRate / Math.max(0.1f, locus.getStability());

                        float oldDegradation = locus.getDegradation();
                        locus.addDegradation(rate);
                        changed = true;

                        WildAside.LOGGER.trace("Degrading [{}] ({}): {} -> {} (rate: {})",
                                locus.getId(),
                                locus.getSource(),
                                String.format("%.1f%%", oldDegradation * 100),
                                String.format("%.1f%%", locus.getDegradation() * 100),
                                String.format("%.3f", rate));

                        if (locus.isFullyDegraded()) {
                            WildAside.LOGGER.info("Locus [{}] fully degraded and removed from trait [{}]", locus.getId(), trait.getName());
                            iter.remove();
                            removed++;
                            continue;
                        }

                        if (locus.getSource() == LocusSource.TRANSIENT) {
                            float stabChance = STABILIZATION_CHANCE * locus.getStability();
                            float roll = DnaUtils.hashToFloat(seed, locus.getId(), 0);

                            if (roll < stabChance) {
                                int idx = group.indexOf(locus);
                                if (idx >= 0) {
                                    GeneLocus stabilizedLocus = locus.withSource(LocusSource.INTEGRATED, entity.level().getGameTime());
                                    group.set(idx, stabilizedLocus);
                                    stabilized++;

                                    WildAside.LOGGER.info("Locus [{}] STABILIZED!  TRANSIENT -> INTEGRATED (roll {} < {})",
                                            locus.getId(),
                                            String.format("%.4f", roll),
                                            String.format("%.4f", stabChance));
                                }
                            }
                        }
                    }
                }
            }

            if (totalDegrading > 0) {
                WildAside.LOGGER.debug("Degradation tick for {}: {} degrading, {} removed, {} stabilized",
                        entity.getName().getString(), totalDegrading, removed, stabilized);
            }

            if (changed) {
                dna.recomputeAndApply(entity);
            }
        });
    }
}