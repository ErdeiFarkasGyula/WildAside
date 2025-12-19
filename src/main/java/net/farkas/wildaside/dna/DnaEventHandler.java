package net.farkas.wildaside.dna;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.capability.dna.DnaCapability;
import net.farkas.wildaside.config.ModConfig;
import net.farkas.wildaside.dna.ability.AbilityRegistry;
import net.farkas.wildaside.dna.ability.IAbility;
import net.farkas.wildaside.dna.allele.value.AlleleValue;
import net.farkas.wildaside.dna.allele.value.FloatAlleleValue;
import net.farkas.wildaside.dna.trait.Trait;
import net.farkas.wildaside.dna.trait.TraitType;
import net.farkas.wildaside.dna.trait.TraitRegistry;
import net.farkas.wildaside.worldgen.dimension.ModDimensions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;

@Mod.EventBusSubscriber(modid = WildAside.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DnaEventHandler {
    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        applyDnaOnJoinLevel(event);
    }

    public static void applyDnaOnJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel().dimension() == ModDimensions.TEST_LEVEL || !(event.getLevel() instanceof ServerLevel)) return;

        if (!ModConfig.WILD_MODE.get()) return;
        if (!ModConfig.EXCLUDE_PLAYERS_FROM_WILD_MODE.get() && event.getEntity() instanceof Player) return;

        if (event.getEntity() instanceof LivingEntity livingEntity) {
            livingEntity.getCapability(DnaCapability.INSTANCE).ifPresent(dna -> {
                if (dna.getGenes().isEmpty()) {
                    dna.setSource(livingEntity.getType());
                    Map<Trait, Gene> genes = DnaUtils.generateBaseGenes(livingEntity, true);
                    dna.setGenes(genes);
                    dna.setStability(100);
                    dna.applyGenes(livingEntity);
                }
            });
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
                AlleleValue value = TraitRegistry.getTraitValueHolder(dna.getGenes(), trait);
                if (value instanceof FloatAlleleValue floatAlleleValue) {
                    dna.setStability(dna.getStability() - (event.getAmount() * 0.25f));
                    event.setAmount(event.getAmount() * (1.0f - floatAlleleValue.get()));
                }
            }
        });
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        handleAbility(event);
    }

    public static void handleAbility(LivingEvent.LivingTickEvent event) {
        if (event.getEntity().level().isClientSide()) return;

        LivingEntity entity = event.getEntity();

        entity.getCapability(DnaCapability.INSTANCE).ifPresent(dna -> {
            float cooldown = entity.getPersistentData().getFloat(IAbility.COOLDOWN);
            if (cooldown > 0) {
                entity.getPersistentData().putFloat(IAbility.COOLDOWN, cooldown - 1);
            }
            for (Gene gene : dna.getGenes().values()) {
                if (gene.getTrait().getTraitType() == TraitType.ABILITY) {
                    IAbility behavior = AbilityRegistry.get(gene.getTrait());
                    if (behavior != null) behavior.onTick(entity, gene);
                }
            }
        });
    }
}
