package net.farkas.wildaside.capability.bioengineering;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.Set;

public interface IBioengineeringSkills {
    Set<ResourceLocation> getSkills();
    boolean hasSkill(ResourceLocation skillId);
    void unlockSkill(ResourceLocation skillId);
    void removeSkill(ResourceLocation skillId);
    CompoundTag serializeNBT();
    void deserializeNBT(CompoundTag tag);
}
