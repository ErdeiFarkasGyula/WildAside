package net.farkas.wildaside.event;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
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
}
