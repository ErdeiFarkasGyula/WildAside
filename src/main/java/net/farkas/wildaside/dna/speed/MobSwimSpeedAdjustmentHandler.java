package net.farkas.wildaside.dna.speed;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.capability.dna.DnaCapability;
import net.farkas.wildaside.config.ModConfig;
import net.farkas.wildaside.dna.DnaUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = WildAside.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MobSwimSpeedAdjustmentHandler {
    private static final UUID WATER_ADJUST_UUID = DnaUtils.generateUuid("dna_dynamic_water_adjust");
    private static final String LAST_STATE_KEY = "dna_last_env_state";

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        if (!ModConfig.ACCURATE_DNA_WATER_MOVEMENT_SPEEDS.get()) return;

        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide()) return;

        CompoundTag data = entity.getPersistentData();
        boolean inWater = entity.isInWater();

        boolean wasInWater = data.getBoolean(LAST_STATE_KEY);
        if (inWater == wasInWater) return;
        data.putBoolean(LAST_STATE_KEY, inWater);

        applyWaterAdjustment(entity, inWater);
    }

    private static void applyWaterAdjustment(LivingEntity livingEntity, boolean inWater) {
        AttributeInstance attr = livingEntity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attr == null) return;

        attr.removeModifier(WATER_ADJUST_UUID);
        attr.removePermanentModifier(WATER_ADJUST_UUID);

        if (inWater) {
            livingEntity.getCapability(DnaCapability.INSTANCE).ifPresent(dna -> {
                double groundSpeed = MobSpeedResultStorage.getSpeed(dna.source(), "ground");
                double waterSpeed = MobSpeedResultStorage.getSpeed(dna.source(), "water");

                if (groundSpeed == 0) return;

                if (groundSpeed == -1) groundSpeed = livingEntity.getAttributeBaseValue(attr.getAttribute());
                if (waterSpeed == -1) waterSpeed = livingEntity.getAttributeBaseValue(attr.getAttribute()) / 5;

                double adjustment = (waterSpeed / groundSpeed);

                attr.addPermanentModifier(new AttributeModifier(
                        WATER_ADJUST_UUID,
                        "dna_water_adjust",
                        adjustment,
                        AttributeModifier.Operation.MULTIPLY_TOTAL
                ));
            });

        }
    }
}
