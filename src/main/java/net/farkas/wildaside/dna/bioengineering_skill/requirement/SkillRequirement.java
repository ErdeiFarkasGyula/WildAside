package net.farkas.wildaside.dna.bioengineering_skill.requirement;

import net.farkas.wildaside.dna.bioengineering_skill.BioengineeringSkill;
import net.farkas.wildaside.dna.bioengineering_skill.BioengineeringSkillRegistry;
import net.farkas.wildaside.dna.bioengineering_skill.BioengineeringSkillUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.List;

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

    @Override
    public List<ResourceLocation> getRequiredSkills() {
        return List.of(requiredSkill);
    }

    @Override
    protected List<Component> getTooltip(Player player, int depth) {
        boolean has = isClientSatisfied(player);

        BioengineeringSkill skill = BioengineeringSkillRegistry.get(requiredSkill);

        return List.of(
                indent(depth)
                        .append(bullet(has))
                        .append(Component.translatable("skill.wildaside.requires_skill",
                                skill.getNameComponent().copy()))
        );
    }

}
