package net.farkas.wildaside.dna.bioengineering_skill.requirement;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public abstract class IBioengineeringSkillRequirement {
    public boolean isServerSatisfied(Player player) {
        if (!isPlayerServer(player)) return false;
        return isSatisfied(player);
    }

    public boolean isClientSatisfied(Player player) {
        if (isPlayerServer(player)) return false;
        return isSatisfied(player);
    }

    public abstract boolean isSatisfied(Player player);

    public void unlock(ServerPlayer player) {
    }

    public List<ResourceLocation> getRequiredSkills() {
        return List.of();
    }

    public boolean dependsOn(ResourceLocation resourceLocation) {
        return getRequiredSkills().contains(resourceLocation);
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

    public boolean isPlayerServer(Player player) {
        return player instanceof ServerPlayer;
    }
}
