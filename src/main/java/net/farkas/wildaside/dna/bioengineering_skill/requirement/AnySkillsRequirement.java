package net.farkas.wildaside.dna.bioengineering_skill.requirement;

import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class AnySkillsRequirement extends IBioengineeringSkillRequirement {
    private final List<SkillRequirement> requirements;

    public AnySkillsRequirement(List<SkillRequirement> requirements) {
        this.requirements = requirements;
    }

    @Override
    public boolean isSatisfied(ServerPlayer player) {
        for (SkillRequirement requirement : requirements) {
            if (requirement.isSatisfied(player)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void unlock(ServerPlayer player) {
        for (IBioengineeringSkillRequirement requirement : requirements) {
            if (requirement.isSatisfied(player)) {
                requirement.unlock(player);
            }
        }
    }
}
