package net.farkas.wildaside.dna.bioengineering_skill.requirement;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public abstract class IBioengineeringSkillRequirement {
    public abstract boolean isSatisfied(ServerPlayer player);

    public boolean isClientSatisfied(Player player) {
        return true;
    }

    public void unlock(ServerPlayer player) {
    }

    public List<ResourceLocation> getRequiredSkills() {
        return List.of();
    }

    public List<Component> getTooltip(Player player) {
        return getTooltip(player, 0);
    }

    protected abstract List<Component> getTooltip(Player player, int depth);

    protected MutableComponent indent(int depth) {
        return Component.literal("  ".repeat(depth));
    }

    protected Component bullet(boolean satisfied) {
        return Component.literal(satisfied ? "✔ " : "✖ ")
                .withStyle(satisfied ? ChatFormatting.GREEN : ChatFormatting.RED);
    }
}
