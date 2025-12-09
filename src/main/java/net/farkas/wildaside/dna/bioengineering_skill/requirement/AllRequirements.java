package net.farkas.wildaside.dna.bioengineering_skill.requirement;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class AllRequirements extends IBioengineeringSkillRequirement {
    private final List<IBioengineeringSkillRequirement> requirements;

    public AllRequirements(List<IBioengineeringSkillRequirement> requirementIds) {
        this.requirements = requirementIds;
    }

    @Override
    public boolean isSatisfied(ServerPlayer player) {
        for (IBioengineeringSkillRequirement requirement : requirements) {
            if (!requirement.isSatisfied(player)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isClientSatisfied(Player player) {
        for (IBioengineeringSkillRequirement requirement : requirements) {
            if (!requirement.isClientSatisfied(player)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void unlock(ServerPlayer player) {
        for (IBioengineeringSkillRequirement requirement : requirements) {
            requirement.unlock(player);
        }
    }
}
