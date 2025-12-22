package net.farkas.wildaside.dna;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.capability.dna.DnaCapability;
import net.farkas.wildaside.config.ModConfig;
import net.farkas.wildaside.dna.ability.AbilityRegistry;
import net.farkas.wildaside.dna.ability.IAbility;
import net.farkas.wildaside.dna.allele.value.AlleleValue;
import net.farkas.wildaside.dna.allele.value.FloatAlleleValue;
import net.farkas.wildaside.dna.locus.GeneLocus;
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

import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = WildAside.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DnaEventHandler {
    private static final int TICK_CADENCE = 20;

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel().dimension() == ModDimensions.TEST_LEVEL || !(event.getLevel() instanceof ServerLevel)) return;
        if (!ModConfig.WILD_MODE.get()) return;
        if (!ModConfig.EXCLUDE_PLAYERS_FROM_WILD_MODE.get() && event.getEntity() instanceof Player) return;

        if (event.getEntity() instanceof LivingEntity living) {
            living.getCapability(DnaCapability.INSTANCE).ifPresent(dna -> {
                if (dna.getLoci().isEmpty()) {
                    dna.setSource(living.getType());
                    dna.setLoci(DnaUtils.generateBaseLoci(living));
                    dna.setStress(0f);
                }
                dna.recomputeAndApply(living);
            });
        }
    }

    @SubscribeEvent
    public static void livingEntityHurt(LivingHurtEvent event) {
        if (event.getEntity().level().isClientSide()) return;
        gainStressFromDamage(event);
        applyResistances(event);
    }

    private static void gainStressFromDamage(LivingHurtEvent event) {
        LivingEntity entity = event.getEntity();
        float stressGain = event.getAmount() * 0.5f;
        entity.getCapability(DnaCapability.INSTANCE).ifPresent(dna -> {
            dna.setStress(dna.getStress() + stressGain);
        });
    }

    private static void applyResistances(LivingHurtEvent event) {
        LivingEntity entity = event.getEntity();
        DamageSource source = event.getSource();

        entity.getCapability(DnaCapability.INSTANCE).ifPresent(dna -> {
            Trait trait = null;
            if (source.is(DamageTypes.IN_FIRE) || source.is(DamageTypes.ON_FIRE) || source.is(DamageTypes.LAVA) || source.is(DamageTypes.HOT_FLOOR)) {
                trait = TraitRegistry.FIRE_RESISTANCE;
            } else if (source.is(DamageTypes.EXPLOSION) || source.is(DamageTypes.PLAYER_EXPLOSION)) {
                trait = TraitRegistry.EXPLOSION_RESISTANCE;
            } else if (source.is(DamageTypes.FALL)) {
                trait = TraitRegistry.FALL_RESISTANCE;
            } else if (source.is(DamageTypes.FREEZE)) {
                trait = TraitRegistry.FREEZE_RESISTANCE;
            }

            if (trait != null) {
                AlleleValue v = DnaUtils.getExpressed(dna.getLoci(), trait);
                if (v instanceof FloatAlleleValue fv) {
                    dna.setStress(dna.getStress() + (event.getAmount() * 0.25f));
                    event.setAmount(event.getAmount() * (1.0f - fv.get()));
                }
            }
        });
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        handleAbility(event);
        handleStressDecay(event);
    }

    private static void handleStressDecay(LivingEvent.LivingTickEvent event) {
        if (event.getEntity().level().isClientSide()) return;
        LivingEntity entity = event.getEntity();
        if (entity.tickCount % 200 != 0) return;

        entity.getCapability(DnaCapability.INSTANCE).ifPresent(dna -> {
            boolean resting = entity.getCombatTracker().getCombatDuration() <= 0 && !entity.isOnFire();
            float decay = resting ? 1.5f : 0.5f;
            dna.setStress(dna.getStress() - decay);
            applyStressTierEffects(entity, dna.getStress());
        });
    }

    private static void applyStressTierEffects(LivingEntity entity, float stress) {
        if (stress >= 85f) {
            entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 60, 0, false, false, true));
            if (entity.getRandom().nextFloat() < 0.1f) entity.hurt(entity.damageSources().generic(), 0.5f);
        } else if (stress >= 70f) {
            entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 0, false, false, true));
            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 0, false, false, true));
        } else if (stress >= 50f) {
            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 0, false, false, true));
        }
    }

    private static void handleAbility(LivingEvent.LivingTickEvent event) {
        if (event.getEntity().level().isClientSide()) return;
        LivingEntity entity = event.getEntity();
        if (entity.tickCount % TICK_CADENCE != 0) return;

        entity.getCapability(DnaCapability.INSTANCE).ifPresent(dna -> {
            float cooldown = entity.getPersistentData().getFloat(IAbility.COOLDOWN);
            if (cooldown > 0) {
                entity.getPersistentData().putFloat(IAbility.COOLDOWN, cooldown - TICK_CADENCE);
            }
            for (Map.Entry<Trait, List<GeneLocus>> e : dna.getLoci().entrySet()) {
                if (e.getKey().getTraitType() == TraitType.ABILITY) {
                    var gene = DnaUtils.asGene(e.getKey(), e.getValue());
                    if (gene != null) {
                        IAbility behavior = AbilityRegistry.get(e.getKey());
                        if (behavior != null) behavior.onTick(entity, gene);
                    }
                }
            }
        });
    }
}