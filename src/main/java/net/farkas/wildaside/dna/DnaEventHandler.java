package net.farkas.wildaside.dna;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.capability.dna.DnaCapability;
import net.farkas.wildaside.config.ModConfig;
import net.farkas.wildaside.dna.ability.AbilityRegistry;
import net.farkas.wildaside.dna.ability.IAbility;
import net.farkas.wildaside.dna.chromosome.Genome;
import net.farkas.wildaside.dna.expression.ExpressionContext;
import net.farkas.wildaside.dna.merge.DnaDegradationHandler;
import net.farkas.wildaside.dna.merge.DnaIntegrationHandler;
import net.farkas.wildaside.dna.merge.RejectionSideEffects;
import net.farkas.wildaside.dna.trait.Trait;
import net.farkas.wildaside.dna.trait.TraitRegistry;
import net.farkas.wildaside.dna.trait.TraitType;
import net.farkas.wildaside.worldgen.dimension.ModDimensions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = WildAside.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DnaEventHandler {
    private static final int TICK_CADENCE = 20;
    private static final int DEGRADATION_CADENCE = 10 * 20;

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (!ModConfig.enableDna) return;

        if (!(event.getLevel() instanceof ServerLevel) || event.getLevel().dimension() == ModDimensions.TEST_LEVEL) return;
        if (!ModConfig.wildMode) return;
        if (event.getEntity() instanceof Player && !ModConfig.excludePlayersFromWildMode) return;

        if (event.getEntity() instanceof LivingEntity living) {
            living.getCapability(DnaCapability.INSTANCE).ifPresent(dna -> {
                if (dna.getGenome().isEmpty()) {
                    WildAside.LOGGER.debug("Generating base DNA for {}", living.getName().getString());
                    dna.setSource(living.getType());
                    dna.setGenome(DnaUtils.generateBaseGenome(living));
                    dna.setStress(0f);
                }
                dna.setActive(true);
                dna.recomputeAndApply(living);
            });
        }
    }

    @SubscribeEvent
    public static void livingEntityHurt(LivingHurtEvent event) {
        if (!ModConfig.enableDna) return;

        if (event.getEntity().level().isClientSide()) return;
        gainStressFromDamage(event);
        applyResistances(event);
    }

    private static void gainStressFromDamage(LivingHurtEvent event) {
        if (!ModConfig.enableDna) return;

        LivingEntity entity = event.getEntity();
        float stressGain = event.getAmount() * 0.5f;
        entity.getCapability(DnaCapability.INSTANCE).ifPresent(dna -> {
            float oldStress = dna.getStress();
            dna.setStress(oldStress + stressGain);
            WildAside.LOGGER.trace("{} took {} damage, stress:  {} -> {}",
                    entity.getName().getString(),
                    String.format("%.2f", event.getAmount()),
                    String.format("%.2f", oldStress),
                    String.format("%.2f", dna.getStress()));
        });
    }

    private static void applyResistances(LivingHurtEvent event) {
        LivingEntity entity = event.getEntity();
        DamageSource source = event.getSource();

        entity.getCapability(DnaCapability.INSTANCE).ifPresent(dna -> {
            Trait trait = null;
            if (source.is(DamageTypes.IN_FIRE) || source.is(DamageTypes.ON_FIRE) || source.is(DamageTypes.LAVA) || source.is(DamageTypes.HOT_FLOOR)) {
                trait = TraitRegistry.FIRE_RESISTANCE;
            }
            else if (source.is(DamageTypes.EXPLOSION) || source.is(DamageTypes.PLAYER_EXPLOSION)) {
                trait = TraitRegistry.EXPLOSION_RESISTANCE;
            }
            else if (source.is(DamageTypes.FALL)) {
                trait = TraitRegistry.FALL_RESISTANCE;
            }
            else if (source.is(DamageTypes.FREEZE)) {
                trait = TraitRegistry.FREEZE_RESISTANCE;
            }

            if (trait != null) {
                Genome genome = dna.getGenome();
                if (genome != null) {
                    ExpressionContext context = new ExpressionContext(entity);
                    float resistance = genome.getExpressedValue(trait, context);
                    
                    if (resistance > 0) {
                        float originalDamage = event.getAmount();
                        float reducedDamage = originalDamage * (1.0f - resistance);

                        dna.setStress(dna.getStress() + (originalDamage * 0.25f));
                        event.setAmount(reducedDamage);

                        WildAside.LOGGER.debug("{} resistance {} reduced damage {} -> {}",
                                trait.getName(),
                                String.format("%.2f", resistance),
                                String.format("%.2f", originalDamage),
                                String.format("%.2f", reducedDamage));
                    }
                }
            }
        });
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        if (!ModConfig.enableDna) return;

        if (event.getEntity().level().isClientSide()) return;

        LivingEntity entity = event.getEntity();
        long currentTick = entity.level().getGameTime();

        entity.getCapability(DnaCapability.INSTANCE).ifPresent(dna -> {
            if (dna.isActive() && entity.tickCount % TICK_CADENCE == 0) {
                dna.updateDynamicTraits(entity);
            }

            if (!dna.getActiveTransitions().isEmpty()) {
                dna.tickTransitions(entity, currentTick);
            }
        });

        if (entity.tickCount % DnaIntegrationHandler.TICK_INTERVAL == 0) {
            DnaIntegrationHandler.tickIntegrations(entity);
        }

        if (entity.tickCount % 200 == 0) {
            handleStressDecay(entity);
            tickDegradation(entity);
            tickSideEffects(entity);
        }

        handleAbility(entity);
    }

    private static void handleStressDecay(LivingEntity entity) {
        entity.getCapability(DnaCapability.INSTANCE).ifPresent(dna -> {
            float stress = dna.getStress();
            if (stress <= 0) return;

            boolean resting = entity.onGround() && entity.getDeltaMovement().lengthSqr() < 0.01;
            boolean safe = !entity.isOnFire() && entity.getLastHurtByMob() == null;

            float decay = (resting && safe) ? 1.5f : 0.5f;
            dna.setStress(stress - decay);
        });
    }

    private static void tickDegradation(LivingEntity entity) {
        DnaDegradationHandler.tickDegradation(entity);
    }

    private static void tickSideEffects(LivingEntity entity) {
        entity.getCapability(DnaCapability.INSTANCE).ifPresent(dna -> {
            long seed = entity.getUUID().getLeastSignificantBits() ^ entity.tickCount;
            //RejectionSideEffects.tickSideEffects(entity, dna.getGenome(), seed);
        });
    }

    private static void handleAbility(LivingEntity entity) {
        if (entity.tickCount % TICK_CADENCE != 0) return;

        entity.getCapability(DnaCapability.INSTANCE).ifPresent(dna -> {
            float cooldown = entity.getPersistentData().getFloat(IAbility.COOLDOWN);
            if (cooldown > 0) {
                entity.getPersistentData().putFloat(IAbility.COOLDOWN, cooldown - TICK_CADENCE);
            }
            
            Genome genome = dna.getGenome();
            if (genome != null) {
                for (Trait trait : TraitRegistry.getAllTraits()) {
                    if (trait.getTraitType() == TraitType.ABILITY) {
                        var expressionPair = genome.getGeneExpression(trait);
                        Gene gene = new Gene(trait, expressionPair.getMaternal(), expressionPair.getPaternal());
                        
                        IAbility behavior = AbilityRegistry.get(trait);
                        if (behavior != null) behavior.onTick(entity, gene);
                    }
                }
            }
        });
    }

    private static void applyStressTierEffects(LivingEntity entity, float stress) {
        if (stress >= 85f) {
            WildAside.LOGGER.debug("{} at critical stress ({}), applying severe effects", entity.getName().getString(), String.format("%.2f", stress));

            entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 60, 0, false, false, true));
            if (entity.getRandom().nextFloat() < 0.1f) {
                entity.hurt(entity.damageSources().generic(), 0.5f);
            }
        }
        else if (stress >= 70f) {
            WildAside.LOGGER.debug("{} at high stress ({}), applying moderate effects", entity.getName().getString(), String.format("%.2f", stress));

            entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 0, false, false, true));
            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 0, false, false, true));
        }
        else if (stress >= 50f) {
            WildAside.LOGGER.trace("{} at moderate stress ({}), applying mild effects", entity.getName().getString(), String.format("%.2f", stress));

            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 0, false, false, true));
        }
    }
}