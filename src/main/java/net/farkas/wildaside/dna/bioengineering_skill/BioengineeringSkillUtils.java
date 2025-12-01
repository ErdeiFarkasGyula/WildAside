package net.farkas.wildaside.dna.bioengineering_skill;

import net.farkas.wildaside.capability.bioengineering.BioengineeringSkillsCapability;
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
            return getUnlocked(player).contains(skill);
        }
        return false;
    }

    public static boolean unlockSkill(Player player, BioengineeringSkill skill) {
        if (player instanceof ServerPlayer serverPlayer) {
            if (!canUnlock(serverPlayer, skill)) return false;

            if (hasSkill(serverPlayer, skill.getId())) return false;

            skill.getRequirement().unlock(serverPlayer);

            serverPlayer.getCapability(BioengineeringSkillsCapability.INSTANCE).ifPresent(cap -> {
                cap.unlockSkill(skill.getId());
                cap.syncToClient(serverPlayer);
            });

            serverPlayer.playSound(SoundEvents.PLAYER_LEVELUP, 0.8f, 1.5f);

            return true;
        }
        return false;
    }
}
