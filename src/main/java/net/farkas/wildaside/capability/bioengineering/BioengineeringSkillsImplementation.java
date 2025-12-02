package net.farkas.wildaside.capability.bioengineering;

import net.farkas.wildaside.dna.DnaConstants;
import net.farkas.wildaside.dna.bioengineering_skill.BioengineeringSkillUtils;
import net.farkas.wildaside.dna.bioengineering_skill.BioengineeringSkills;
import net.farkas.wildaside.network.NetworkHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;

import static net.farkas.wildaside.dna.DnaConstants.*;

public class BioengineeringSkillsImplementation implements IBioengineeringSkills {
    private Set<ResourceLocation> skills = new HashSet<>();
    private int points = 0;

    @Override
    public Set<ResourceLocation> getSkills() {
        return skills;
    }

    @Override
    public int getPoints() {
        return points;
    }

    @Override
    public void addPoints(int pointsToAdd) {
        points += pointsToAdd;
    }

    @Override
    public void setPoints(int newPoints) {
        points = newPoints;
    }

    @Override
    public boolean spendPoints(int pointsToRemove) {
        if (points <= 0 || points < pointsToRemove) return false;
        points -= pointsToRemove;
        return true;
    }

    @Override
    public boolean hasSkill(ResourceLocation skillId) {
        return skills.contains(skillId);
    }

    @Override
    public void unlockSkill(ResourceLocation skillId) {
        NetworkHandler.sendBioengineeringSkillRequestPacket(skillId);
    }

    @Override
    public void removeSkill(ResourceLocation skillId) {
        skills.remove(skillId);
    }

    @Override
    public void setSkills(Set<ResourceLocation> newSkills) {
        skills = newSkills;
    }

    @Override
    public void syncToClient(ServerPlayer player) {
        NetworkHandler.sendBioengineeringSkillClientSyncPacket(skills, points);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag skillsTag = new ListTag();

        tag.putInt(BIOENGINEERING_POINTS, points);

        for (ResourceLocation skillId : skills) {
            skillsTag.add(StringTag.valueOf(skillId.toString()));
        }

        tag.put(BIOENGINEERING_SKILL, skillsTag);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        points = tag.getInt(BIOENGINEERING_POINTS);

        skills.clear();

        ListTag skillsTag = tag.getList(BIOENGINEERING_SKILL, Tag.TAG_STRING);
        for (Tag t : skillsTag) {
            skills.add(new ResourceLocation(t.getAsString()));
        }
    }
}
