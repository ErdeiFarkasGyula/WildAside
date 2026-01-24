package net.farkas.wildaside.dna.bioengineering_skill.requirement;

import net.farkas.wildaside.advancement.AdvancementUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class AdvancementRequirement extends IBioengineeringSkillRequirement {
    private final ResourceLocation advancementLocation;

    public AdvancementRequirement(ResourceLocation advancementLocation) {
        this.advancementLocation = advancementLocation;
    }

    @Override
    public boolean isSatisfied(Player player) {
        return AdvancementUtils.hasAdvancement(player, advancementLocation);
    }

    @Override
    protected List<Component> getTooltip(Player player, int depth) {
        boolean has = isClientSatisfied(player);

        return List.of(
                indent(depth)
                        .append(bullet(has))
                        .append(Component.translatable("skill.wildaside.requires_advancement",
                                Component.translatable(AdvancementUtils.getAdvancementTitle(player, advancementLocation).getString())
                        ))
        );
    }
}
