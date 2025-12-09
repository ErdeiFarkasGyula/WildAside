package net.farkas.wildaside.dna.bioengineering_skill.requirement;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class AnyRequirements extends IBioengineeringSkillRequirement {
    private final List<IBioengineeringSkillRequirement> requirements;

    public AnyRequirements(List<IBioengineeringSkillRequirement> requirements) {
        this.requirements = requirements;
    }

    @Override
    public boolean isSatisfied(ServerPlayer player) {
        for (IBioengineeringSkillRequirement requirement : requirements) {
            if (requirement.isSatisfied(player)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isClientSatisfied(Player player) {
        for (IBioengineeringSkillRequirement requirement : requirements) {
            if (requirement.isClientSatisfied(player)) {
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
