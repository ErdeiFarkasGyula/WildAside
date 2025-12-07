package net.farkas.wildaside.dna.bioengineering_skill;

import net.farkas.wildaside.capability.bioengineering.BioengineeringSkillsCapability;
import net.farkas.wildaside.network.NetworkHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;

import java.util.HashSet;
import java.util.Set;

public class BioengineeringSkillUtils {
    public static Set<ResourceLocation> getUnlocked(Player player) {
        Set<ResourceLocation> unlocked = new HashSet<ResourceLocation>();
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.getCapability(BioengineeringSkillsCapability.INSTANCE).ifPresent(cap -> {
                unlocked.addAll(cap.getSkills());
            });
        }
        return unlocked;
    }

    public static boolean canUnlock(Player player, BioengineeringSkill skill) {
        if (player instanceof ServerPlayer serverPlayer) {
            return skill.getRequirement().isSatisfied(serverPlayer);
        }

        return false;
    }

    public static boolean hasSkill(Player player, ResourceLocation skill) {
        if (player instanceof ServerPlayer serverPlayer) {
            return getUnlocked(serverPlayer).contains(skill);
        }
        return false;
    }

    public static void unlockAndSyncToClient(Player player, ResourceLocation skillId, boolean forceUnlock) {
        if (player instanceof ServerPlayer serverPlayer) {
            BioengineeringSkill skill = BioengineeringSkillRegistry.get(skillId);

            if (!forceUnlock) {
                if (!canUnlock(serverPlayer, skill)) return;
                if (hasSkill(serverPlayer, skillId)) return;
            }

            serverPlayer.getCapability(BioengineeringSkillsCapability.INSTANCE).ifPresent(cap -> {
                if (!forceUnlock) {
                    skill.getRequirement().unlock(serverPlayer);
                }
                cap.addSkillToUnlocked(skillId);
                cap.syncToClient(serverPlayer);
            });

            serverPlayer.playSound(SoundEvents.PLAYER_LEVELUP, 0.8f, 1.5f);
        }
    }

    public static void removeAndSyncToClient(Player player, ResourceLocation skillId) {
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.getCapability(BioengineeringSkillsCapability.INSTANCE).ifPresent(cap -> {
                cap.removeSkillFromUnlocked(skillId);
                cap.syncToClient(serverPlayer);
            });

            serverPlayer.playSound(SoundEvents.PLAYER_HURT_FREEZE, 0.8f, 1.5f);
        }
    }

    public static void handlePointsAndSyncToClient(Player player, int points, BioengineeringSkillPointOperation operation) {
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.getCapability(BioengineeringSkillsCapability.INSTANCE).ifPresent(cap -> {
                cap.handlePoints(points, operation);
                cap.syncToClient(serverPlayer);
            });

            serverPlayer.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 0.8f, 1.5f);
        }
    }

    public static void syncToClient(ServerPlayer player) {
        player.getCapability(BioengineeringSkillsCapability.INSTANCE).ifPresent(cap -> {
            NetworkHandler.sendBioengineeringSkillClientSyncPacket(player, cap.getSkills(), cap.getPoints());
        });
    }

    public static void syncToClients(Iterable<ServerPlayer> players) {
        for (ServerPlayer player : players) {
            syncToClient(player);
        }
    }
}
