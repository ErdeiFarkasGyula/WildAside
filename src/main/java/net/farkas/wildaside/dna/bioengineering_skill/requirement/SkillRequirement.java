package net.farkas.wildaside.dna.bioengineering_skill.requirement;

import net.farkas.wildaside.capability.bioengineering.BioengineeringSkillsCapability;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.Set;

public class SkillRequirement implements BioengineeringSkillRequirement {
    private final Set<ResourceLocation> requirements;

    public SkillRequirement(Set<ResourceLocation> requirementIds) {
        this.requirements = requirementIds;
    }

    @Override
    public boolean isSatisfied(ServerPlayer player, Set<ResourceLocation> unlockedSkills) {
        for (ResourceLocation requirementId : requirements) {
            if (!unlockedSkills.contains(requirementId)) {
                return false;
            }
        }
        return true;
    }
}
