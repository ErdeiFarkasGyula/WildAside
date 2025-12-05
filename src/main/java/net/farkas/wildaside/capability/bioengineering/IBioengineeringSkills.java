package net.farkas.wildaside.capability.bioengineering;

import net.farkas.wildaside.dna.bioengineering_skill.BioengineeringSkillPointOperation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.Set;

public interface IBioengineeringSkills {
    Set<ResourceLocation> getSkills();
    int getPoints();
    void setPoints(int points);
    void handlePoints(int points, BioengineeringSkillPointOperation operation);
    boolean hasSkill(ResourceLocation skillId);
    void addSkillToUnlocked(ResourceLocation skillId);
    void removeSkillFromUnlocked(ResourceLocation skillId);
    void setSkills(Set<ResourceLocation> newSkills);
    void syncToClient(ServerPlayer player);
    CompoundTag serializeNBT();
    void deserializeNBT(CompoundTag tag);
}
