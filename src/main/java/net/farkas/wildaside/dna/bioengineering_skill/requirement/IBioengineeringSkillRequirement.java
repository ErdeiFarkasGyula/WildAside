package net.farkas.wildaside.dna.bioengineering_skill.requirement;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public abstract class IBioengineeringSkillRequirement {
    public abstract boolean isSatisfied(ServerPlayer player);
    public abstract boolean isClientSatisfied(Player player);
    public void unlock(ServerPlayer player) {}
}
