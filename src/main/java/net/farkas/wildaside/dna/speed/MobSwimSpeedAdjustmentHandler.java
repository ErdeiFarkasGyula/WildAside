package net.farkas.wildaside.dna.speed;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.dna.DnaUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = WildAside.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MobSwimSpeedAdjustmentHandler {
    private static final UUID WATER_ADJUST_UUID = DnaUtils.getUuid("dna_dynamic_water_adjust");
    private static final String LAST_STATE_KEY = "dna_last_env_state";

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Player player = event.player;
        if (player.level().isClientSide()) return;

        CompoundTag data = player.getPersistentData();
        boolean inWater = player.isInWater();

        boolean wasInWater = data.getBoolean(LAST_STATE_KEY);
        if (inWater == wasInWater) return;
        data.putBoolean(LAST_STATE_KEY, inWater);

        applyWaterAdjustment(player, inWater);
    }

    private static void applyWaterAdjustment(Player player, boolean inWater) {
        AttributeInstance attr = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attr == null) return;

        attr.removeModifier(WATER_ADJUST_UUID);
        attr.removePermanentModifier(WATER_ADJUST_UUID);

        if (inWater) {
            CompoundTag data = player.getPersistentData();
            if (!data.contains("dna_source_entity")) return;

            String sourceType = data.getString("dna_source_entity");
            var entityType = player.level().registryAccess().registryOrThrow(Registries.ENTITY_TYPE).get(new ResourceLocation(sourceType));

            double groundSpeed = MobSpeedResultStorage.getSpeed(entityType, "ground");
            double waterSpeed = MobSpeedResultStorage.getSpeed(entityType, "water");

            if (groundSpeed <= 0) return;

            double groundVal = groundSpeed / 43.17;
            double waterVal = waterSpeed / 43.17;

            double adjustment = (waterVal / groundVal) - 1.0;

            attr.addPermanentModifier(new AttributeModifier(
                    WATER_ADJUST_UUID,
                    "dna_water_adjust",
                    adjustment,
                    AttributeModifier.Operation.MULTIPLY_TOTAL
            ));

            System.out.printf("[%s] Entered water → Adjust %.4f (ground %.4f → water %.4f)%n",
                    player.getName().getString(), adjustment, groundVal, waterVal);
        } else {
            System.out.printf("[%s] Left water → Removed adjust%n", player.getName().getString());
        }
    }
}
