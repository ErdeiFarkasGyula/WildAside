package net.farkas.wildaside.dna.bioengineering_skill.requirement;

import net.farkas.wildaside.dna.bioengineering_skill.BioengineeringSkillUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.Set;

public class SkillRequirement extends IBioengineeringSkillRequirement {
    private final ResourceLocation requiredSkill;

    public SkillRequirement(ResourceLocation skill) {
        this.requiredSkill = skill;
    }

    @Override
    public boolean isSatisfied(ServerPlayer player) {
        return BioengineeringSkillUtils.hasSkill(player, requiredSkill);
    }

    @Override
    public boolean isClientSatisfied(Player player) {
        return BioengineeringSkillUtils.hasSkill(player, requiredSkill);
    }
}
