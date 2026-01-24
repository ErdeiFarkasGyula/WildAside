package net.farkas.wildaside.dna.bioengineering_skill.requirement;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class AnyRequirements extends IBioengineeringSkillRequirement {
    private final List<IBioengineeringSkillRequirement> requirements;

    public AnyRequirements(List<IBioengineeringSkillRequirement> requirements) {
        this.requirements = requirements;
    }

    @Override
    public boolean isSatisfied(Player player) {
        return requirements.stream().anyMatch(r -> r.isSatisfied(player));
    }

    @Override
    public void unlock(ServerPlayer player) {
        for (IBioengineeringSkillRequirement req : requirements) {
            if (req.isServerSatisfied(player)) {
                req.unlock(player);
                return;
            }
        }
    }

    @Override
    public List<ResourceLocation> getRequiredSkills() {
        List<ResourceLocation> out = new ArrayList<>();
        for (var requirement : requirements) {
            out.addAll(requirement.getRequiredSkills());
        }
        return out;
    }

    @Override
    protected List<Component> getTooltip(Player player, int depth) {
        List<Component> list = new ArrayList<>();

        boolean satisfied = isClientSatisfied(player);

        list.add(
                indent(depth)
                        .append(bullet(satisfied))
                        .append(Component.translatable("skill.wildaside.requirements.any"))
        );

        for (IBioengineeringSkillRequirement req : requirements) {
            list.addAll(req.getTooltip(player, depth + 1));
        }

        return list;
    }
}
