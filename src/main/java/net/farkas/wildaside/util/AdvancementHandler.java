package net.farkas.wildaside.util;

import net.farkas.wildaside.WildAside;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class AdvancementHandler {
    public static void givePlayerAdvancement(Entity entity, String advName) {
        if (entity instanceof ServerPlayer player) {
            ResourceLocation advancementID = new ResourceLocation(WildAside.MOD_ID, advName);
            Advancement advancement = player.server.getAdvancements().getAdvancement(advancementID);

            if (advancement == null) {
                System.out.println("Advancement " + advName + " can't be found!");
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
