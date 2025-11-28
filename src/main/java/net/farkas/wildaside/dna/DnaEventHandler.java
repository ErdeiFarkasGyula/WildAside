package net.farkas.wildaside.dna;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.capability.dna.DnaCapability;
import net.farkas.wildaside.config.ModConfig;
import net.farkas.wildaside.dna.ability.IAbility;
import net.farkas.wildaside.dna.speed.MobSpeedTestTracker;
import net.farkas.wildaside.dna.trait.Trait;
import net.farkas.wildaside.dna.trait.Traits;
import net.farkas.wildaside.worldgen.dimension.ModDimensions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;

@Mod.EventBusSubscriber(modid = WildAside.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DnaEventHandler {
    @SubscribeEvent
    public static void onEntityLeftLevel(EntityLeaveLevelEvent event) {
        checkTestDimensionEntityRemoves(event);
    }

    public static void checkTestDimensionEntityRemoves(EntityLeaveLevelEvent event) {
        if (!ModConfig.ACCURATE_DNA_WATER_MOVEMENT_SPEEDS.get() || event.getEntity().level().isClientSide()) return;

        if (event.getEntity().level().dimension() == ModDimensions.TEST_LEVEL) {
            if (event.getEntity() instanceof Mob mob && (mob.isSensitiveToWater() || mob.getSpeed() == 0.0 || mob.getDeltaMovement() == Vec3.ZERO)) {
                MobSpeedTestTracker.onMobFinished(mob, "water");
            }
        }
    }

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        applyDnaOnJoinLevel(event);
    }

    public static void applyDnaOnJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel().dimension() == ModDimensions.TEST_LEVEL || !ModConfig.ACCURATE_DNA_MOVEMENT_SPEEDS.get() || !(event.getLevel() instanceof ServerLevel)) return;

        if (event.getEntity() instanceof LivingEntity livingEntity) {
            livingEntity.getCapability(DnaCapability.INSTANCE).ifPresent(dna -> {
                if (dna.getGenes().isEmpty()) {
                    dna.setSource(livingEntity.getType());
                    Map<Trait, Gene> genes = DnaUtils.generateBaseGenes(livingEntity, true);
                    dna.setGenes(genes);
                    dna.setStability(100);
                }
            });
            livingEntity.getPersistentData().putFloat(IAbility.COOLDOWN, 0);
        }
    }

    @SubscribeEvent
    public static void livingEntityHurt(LivingHurtEvent event) {
        if (event.getEntity().level().isClientSide()) return;
        reduceDnaStabilityFromHurting(event);
        reduceDamageBasedOnDnaResistances(event);
    }

    public static void reduceDnaStabilityFromHurting(LivingHurtEvent event) {
        LivingEntity entity = event.getEntity();
        float stabilityReduction = event.getAmount() * 0.05f;

        entity.getCapability(DnaCapability.INSTANCE).ifPresent(dna -> {
            boolean modified = dna.getModified();

            if (modified) {
                dna.setStability(dna.getStability() - stabilityReduction);
            }
        });
    }

    public static void reduceDamageBasedOnDnaResistances(LivingHurtEvent event) {
        LivingEntity entity = event.getEntity();
        DamageSource source = event.getSource();

        entity.getCapability(DnaCapability.INSTANCE).ifPresent(dna -> {
            Trait trait = null;
            if (source.is(DamageTypes.IN_FIRE) || source.is(DamageTypes.ON_FIRE) || source.is(DamageTypes.LAVA) || source.is(DamageTypes.HOT_FLOOR)) {
                trait = Traits.FIRE_RESISTANCE;
            }
            else if (source.is(DamageTypes.EXPLOSION) || source.is(DamageTypes.PLAYER_EXPLOSION)) {
                trait = Traits.EXPLOSION_RESISTANCE;
            }
            else if (source.is(DamageTypes.FALL)) {
                trait = Traits.FALL_RESISTANCE;
            }
            else if (source.is(DamageTypes.FREEZE)) {
                trait = Traits.FREEZE_RESISTANCE;
            }

            if (trait != null) {
                float value = Traits.getTraitValue(dna.getGenes(), trait);
                dna.setStability(dna.getStability() - (event.getAmount() * 0.25f));
                event.setAmount(event.getAmount() * (1.0f - value));
            }
        });
    }
}
