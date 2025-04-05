package net.farkas.wildaside.event;

import net.farkas.wildaside.worldgen.biome.ModBiomes;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.telemetry.events.WorldLoadEvent;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ModEventBusServerEvents {
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            Advancement advancement = player.server.getAdvancements().getAdvancement(new ResourceLocation("wildaside:wild_wilder_wildest"));
            AdvancementProgress progress = player.getAdvancements().getOrStartProgress(advancement);
            if (!progress.isDone()) {
                for (String criteria : progress.getRemainingCriteria()) {
                    player.getAdvancements().award(advancement, criteria);
                }
            }
        }
    }

    private static final ResourceLocation GLOWING_FOREST = new ResourceLocation("wildaside", "glowing_hickory_forest");

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && !event.player.level().isClientSide) {
            ServerPlayer player = (ServerPlayer) event.player;
            ServerLevel world = player.serverLevel();

            long time = world.getDayTime();

            // Check for nighttime
            if (time >= 18000 && time <= 22000) {

                // Get biome at player location
                Holder<Biome> biomeHolder = world.getBiome(player.blockPosition());
                ResourceKey<Biome> biomeKey = biomeHolder.unwrapKey().orElse(null);

                if (biomeKey != null && biomeKey.location().equals(GLOWING_FOREST)) {
                    // Now grant the advancement
                    ResourceLocation advancementID = new ResourceLocation("wildaside", "glow_up");
                    Advancement advancement = player.server.getAdvancements().getAdvancement(advancementID);
                    if (advancement != null) {
                        AdvancementProgress progress = player.getAdvancements().getOrStartProgress(advancement);
                        if (!progress.isDone()) {
                            for (String criterion : progress.getRemainingCriteria()) {
                                player.getAdvancements().award(advancement, criterion);
                            }
                        }
                    }
                }
            }
        }
    }
}
