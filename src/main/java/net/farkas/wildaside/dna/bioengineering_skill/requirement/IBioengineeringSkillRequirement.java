package net.farkas.wildaside.dna.bioengineering_skill.requirement;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.Set;

public interface IBioengineeringSkillRequirement {
    boolean isSatisfied(ServerPlayer player, Set<ResourceLocation> unlockedSkills);
}
