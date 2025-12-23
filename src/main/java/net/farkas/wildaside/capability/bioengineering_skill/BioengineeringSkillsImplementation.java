package net.farkas.wildaside.capability.bioengineering_skill;

import net.farkas.wildaside.dna.bioengineering_skill.BioengineeringSkillPointOperation;
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
    public void setPoints(int newPoints) {
        points = newPoints;
    }

    @Override
    public void handlePoints(int inPoints, BioengineeringSkillPointOperation operation) {
        switch (operation) {
            case ADD:
                points += inPoints;
                break;
            case REMOVE:
                points -= inPoints;
                break;
            case SPEND:
                if (points <= 0 || points < inPoints) {
                    return;
                }
                points -= inPoints;
                break;
            case SET:
                points = inPoints;
                break;
            default:
                break;
        }
    }

    @Override
    public boolean hasSkill(ResourceLocation skillId) {
        return skills.contains(skillId);
    }

    @Override
    public void addSkillToUnlocked(ResourceLocation skillId) {
        skills.add(skillId);
    }

    @Override
    public void removeSkillFromUnlocked(ResourceLocation skillId) {
        skills.remove(skillId);
    }

    @Override
    public void setSkills(Set<ResourceLocation> newSkills) {
        skills = newSkills;
    }

    @Override
    public void syncToClient(ServerPlayer player) {
        NetworkHandler.sendBioengineeringSkillClientSyncPacket(player, skills, points);
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
