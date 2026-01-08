package net.farkas.wildaside.advancement;

import net.farkas.wildaside.WildAside;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class AdvancementHandler {
    public static void givePlayerAdvancement(Entity entity, ResourceLocation resourceLocation) {
        if (entity instanceof ServerPlayer player) {
            Advancement advancement = player.server.getAdvancements().getAdvancement(resourceLocation);

            if (advancement == null) {
                WildAside.LOGGER.warn("Tried giving advancement {} to {}, but it can't be found!", resourceLocation, player.getName().getString());
                return;
            }

            AdvancementProgress progress = player.getAdvancements().getOrStartProgress(advancement);
            if (!progress.isDone()) {
                for (String criterion : progress.getRemainingCriteria()) {
                    player.getAdvancements().award(advancement, criterion);
                }
            }
        }
    }
}
