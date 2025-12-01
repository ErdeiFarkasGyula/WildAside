package net.farkas.wildaside.dna.bioengineering_skill.requirement;

import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class AllSkillsRequirement extends IBioengineeringSkillRequirement {
    private final List<SkillRequirement> requirements;

    public AllSkillsRequirement(List<SkillRequirement> requirementIds) {
        this.requirements = requirementIds;
    }

    @Override
    public boolean isSatisfied(ServerPlayer player) {
        for (SkillRequirement requirement : requirements) {
            if (!requirement.isSatisfied(player)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void unlock(ServerPlayer player) {
        for (SkillRequirement requirement : requirements) {
            requirement.unlock(player);
        }
    }
}
