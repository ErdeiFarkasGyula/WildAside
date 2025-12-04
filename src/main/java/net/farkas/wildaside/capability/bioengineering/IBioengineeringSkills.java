package net.farkas.wildaside.capability.bioengineering;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.Set;

public interface IBioengineeringSkills {
    Set<ResourceLocation> getSkills();
    int getPoints();
    void addPoints(int points);
    void setPoints(int points);
    boolean spendPoints(int points);
    boolean hasSkill(ResourceLocation skillId);
    void sendUnlockRequest(ResourceLocation skillId);
    void sendRemoveRequest(ResourceLocation skillId);
    void addSkillToUnlocked(ResourceLocation skillId);
    void removeSkillFromUnlocked(ResourceLocation skillId);
    void setSkills(Set<ResourceLocation> newSkills);
    void syncToClient(ServerPlayer player);
    CompoundTag serializeNBT();
    void deserializeNBT(CompoundTag tag);
}
