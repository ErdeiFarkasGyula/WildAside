package net.farkas.wildaside.capability.bioengineering;

import net.farkas.wildaside.dna.DnaConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

import static net.farkas.wildaside.dna.DnaConstants.*;

public class BioengineeringSkillsImplementation implements IBioengineeringSkills {
    private final Set<ResourceLocation> skills = new HashSet<>();

    @Override
    public Set<ResourceLocation> getSkills() {
        return skills;
    }

    @Override
    public boolean hasSkill(ResourceLocation skillId) {
        return skills.contains(skillId);
    }

    @Override
    public void unlockSkill(ResourceLocation skillId) {
        skills.add(skillId);
    }

    @Override
    public void removeSkill(ResourceLocation skillId) {
        skills.remove(skillId);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag skillsTag = new ListTag();

        for (ResourceLocation skillId : skills) {
            skillsTag.add(StringTag.valueOf(skillId.toString()));
        }

        tag.put(BIOENGINEERING_SKILL, skillsTag);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        skills.clear();

        ListTag skillsTag = tag.getList(BIOENGINEERING_SKILL, Tag.TAG_STRING);
        for (Tag t : skillsTag) {
            skills.add(new ResourceLocation(t.getAsString()));
        }
    }
}
