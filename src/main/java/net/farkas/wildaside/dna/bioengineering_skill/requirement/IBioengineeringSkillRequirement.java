package net.farkas.wildaside.dna.bioengineering_skill.requirement;

import net.minecraft.server.level.ServerPlayer;

public abstract class IBioengineeringSkillRequirement {
    public abstract boolean isSatisfied(ServerPlayer player);
    public void unlock(ServerPlayer player) {}
}
