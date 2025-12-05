package net.farkas.wildaside.dna.bioengineering_skill.requirement;

import net.farkas.wildaside.capability.bioengineering.BioengineeringSkillsCapability;
import net.farkas.wildaside.dna.bioengineering_skill.BioengineeringSkillPointOperation;
import net.minecraft.server.level.ServerPlayer;

public class PointRequirement extends IBioengineeringSkillRequirement {
    private final int requiredPoints;

    public PointRequirement(int requiredPoints) {
        this.requiredPoints = requiredPoints;
    }

    @Override
    public boolean isSatisfied(ServerPlayer player) {
        var cap = player.getCapability(BioengineeringSkillsCapability.INSTANCE).orElse(null);
        return cap != null && cap.getPoints() >= requiredPoints;
    }

    @Override
    public void unlock(ServerPlayer player) {
        var cap = player.getCapability(BioengineeringSkillsCapability.INSTANCE).orElse(null);
        if (cap != null) {
            cap.handlePoints(requiredPoints, BioengineeringSkillPointOperation.SPEND);
        }
    }
}
