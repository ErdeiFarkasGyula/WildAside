package net.farkas.wildaside.dna.bioengineering_skill;

import net.farkas.wildaside.dna.bioengineering_skill.category.BioengineeringSkillCategory;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class BioengineeringSkills {
    private static final Map<ResourceLocation, BioengineeringSkill> REGISTRY = new HashMap<>();

    public static final BioengineeringSkillCategory GENETICS = register(
            new BioengineeringSkillCategory.Builder()
                    .name("genetic")
                    .icon()
    )

    public static BioengineeringSkill register(BioengineeringSkill skill) {
        REGISTRY.put(skill.getId(), skill);
        return skill;
    }

    public static BioengineeringSkill get(ResourceLocation id) {
        return REGISTRY.get(id);
    }

    public static Collection<BioengineeringSkill> all() {
        return REGISTRY.values();
    }
}
