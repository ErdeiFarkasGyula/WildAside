package net.farkas.wildaside.dna.bioengineering_skill.category;

import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class BioengineeringSkillCategories {
    public static final Map<ResourceLocation, BioengineeringSkillCategory> CATEGORIES = new HashMap<>();

    public static BioengineeringSkillCategory register(BioengineeringSkillCategory skillCategory) {
        CATEGORIES.put(skillCategory.ge, skillCategory);
        return skillCategory;
    }

    public static BioengineeringSkillCategory get(String name) {
        return CATEGORIES.get(name);
    }

    public static Collection<BioengineeringSkillCategory> all() {
        return CATEGORIES.values();
    }
}
