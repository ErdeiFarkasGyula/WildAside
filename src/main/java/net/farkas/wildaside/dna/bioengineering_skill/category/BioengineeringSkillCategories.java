package net.farkas.wildaside.dna.bioengineering_skill.category;

import net.farkas.wildaside.WildAside;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class BioengineeringSkillCategories {
    public static final Map<ResourceLocation, BioengineeringSkillCategory> CATEGORIES = new HashMap<>();

    public static final BioengineeringSkillCategory CATEGORY_1 = register(new BioengineeringSkillCategory.Builder()
            .name("category_1")
            .build());

    public static BioengineeringSkillCategory register(BioengineeringSkillCategory skillCategory) {
        CATEGORIES.put(skillCategory.getId(), skillCategory);
        return skillCategory;
    }

    public static BioengineeringSkillCategory get(String name) {
        return CATEGORIES.get(new ResourceLocation(WildAside.MOD_ID,  name));
    }

    public static BioengineeringSkillCategory get(ResourceLocation id) {
        return CATEGORIES.get(id);
    }

    public static Collection<BioengineeringSkillCategory> all() {
        return CATEGORIES.values();
    }
}
