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
    void unlockSkill(ResourceLocation skillId);
    void removeSkill(ResourceLocation skillId);
    void setSkills(Set<ResourceLocation> newSkills);
    void syncToClient(ServerPlayer player);
    CompoundTag serializeNBT();
    void deserializeNBT(CompoundTag tag);
}
