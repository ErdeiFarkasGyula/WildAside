package net.farkas.wildaside.dna.bioengineering_skill.requirement;

import net.farkas.wildaside.capability.bioengineering_skill.BioengineeringSkillsCapability;
import net.farkas.wildaside.dna.bioengineering_skill.BioengineeringSkillPointOperation;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.List;

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
    public boolean isClientSatisfied(Player player) {
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

    @Override
    protected List<Component> getTooltip(Player player, int depth) {
        boolean has = isClientSatisfied(player);

        return List.of(
                indent(depth)
                        .append(bullet(has))
                        .append(Component.translatable(
                                "skill.wildaside.requires_points",
                                requiredPoints
                        ))
        );
    }
}
