package net.farkas.wildaside.advancement;

import net.farkas.wildaside.WildAside;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementList;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientAdvancements;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.Map;

public class AdvancementUtils {
    @Nullable
    public static Advancement getAdvancement(Entity entity, ResourceLocation advancementLocation) {
        if (entity instanceof ServerPlayer serverPlayer) {
            return serverPlayer.server.getAdvancements().getAdvancement(advancementLocation);
        } else if (entity.level().isClientSide && entity instanceof LocalPlayer) {
            return getClientAdvancement(advancementLocation);
        }
        return null;
    }

    @OnlyIn(Dist.CLIENT)
    @Nullable
    public static ClientAdvancements getClientAdvancements() {
        LocalPlayer localPlayer = Minecraft.getInstance().player;
        if (localPlayer == null) return null;
        return localPlayer.connection.getAdvancements();
    }

    @OnlyIn(Dist.CLIENT)
    @Nullable
    public static AdvancementList getClientAdvancementList() {
        ClientAdvancements clientAdvancements = getClientAdvancements();
        return clientAdvancements != null ? clientAdvancements.getAdvancements() : null;
    }

    @OnlyIn(Dist.CLIENT)
    @Nullable
    public static Advancement getClientAdvancement(ResourceLocation resourceLocation) {
        AdvancementList advancementList = getClientAdvancementList();
        return advancementList != null ? advancementList.get(resourceLocation) : null;
    }

    @Nullable
    public static DisplayInfo getAdvancementDisplay(Entity entity, ResourceLocation advancementLocation) {
        Advancement advancement = getAdvancement(entity, advancementLocation);
        return (advancement != null) ? advancement.getDisplay() : null;
    }

    public static Component getAdvancementTitle(Entity entity, ResourceLocation advancementLocation) {
        DisplayInfo displayInfo = getAdvancementDisplay(entity, advancementLocation);
        return (displayInfo != null) ? displayInfo.getTitle() : Component.empty();
    }

    public static Component getAdvancementDescription(Entity entity, ResourceLocation advancementLocation) {
        DisplayInfo displayInfo = getAdvancementDisplay(entity, advancementLocation);
        return (displayInfo != null) ? displayInfo.getDescription() : Component.empty();
    }

    public static void givePlayerAdvancement(Entity entity, ResourceLocation advancementLocation) {
        if (entity instanceof ServerPlayer player) {
            Advancement advancement = getAdvancement(entity, advancementLocation);
            if (advancement == null) {
                WildAside.LOGGER.warn("Tried giving advancement {} to {}, but it can't be found!", advancementLocation, player.getName().getString());
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

    public static boolean hasAdvancement(Player player, ResourceLocation advancementLocation) {
        if (player instanceof ServerPlayer serverPlayer) {
            Advancement advancement = serverPlayer.server.getAdvancements().getAdvancement(advancementLocation);
            if (advancement == null) return false;
            return serverPlayer.getAdvancements().getOrStartProgress(advancement).isDone();
        } else if (player.level().isClientSide && player instanceof LocalPlayer) {
            return hasClientAdvancement(advancementLocation);
        }
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    private static boolean hasClientAdvancement(ResourceLocation advancementLocation) {
        ClientAdvancements clientAdvancements = getClientAdvancements();
        if (clientAdvancements == null) return false;

        Advancement advancement = getClientAdvancement(advancementLocation);
        if (advancement == null) return false;

        try {
            Field progressField = ClientAdvancements.class.getDeclaredField("progress");
            progressField.setAccessible(true);
            Map<Advancement, AdvancementProgress> progressMap = (Map<Advancement, AdvancementProgress>) progressField.get(clientAdvancements);

            if (progressMap.containsKey(advancement)) {
                return progressMap.get(advancement).isDone();
            }
        } catch (Exception e) {
            WildAside.LOGGER.warn("Failed to access client advancement progress", e);
        }
        
        return false;
    }
}