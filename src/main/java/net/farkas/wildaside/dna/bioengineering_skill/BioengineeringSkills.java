package net.farkas.wildaside.dna.bioengineering_skill;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.dna.bioengineering_skill.category.BioengineeringSkillCategories;
import net.farkas.wildaside.dna.bioengineering_skill.requirement.AllRequirements;
import net.farkas.wildaside.dna.bioengineering_skill.requirement.ItemRequirement;
import net.farkas.wildaside.dna.bioengineering_skill.requirement.PointRequirement;
import net.farkas.wildaside.item.ModItems;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BioengineeringSkills {
    private static final Map<ResourceLocation, BioengineeringSkill> REGISTRY = new HashMap<>();

    private static final BioengineeringSkill TEST_1 = register(new BioengineeringSkill.Builder()
            .name("test_1")
            .category(BioengineeringSkillCategories.CATEGORY_1)
            .requirement(
                    new AllRequirements(
                    List.of(
                            new PointRequirement(67),
                            new ItemRequirement(ModItems.ENTORIUM.get(), null, false)
                    )
            )).build());

    public static BioengineeringSkill register(BioengineeringSkill skill) {
        REGISTRY.put(skill.getId(), skill);
        return skill;
    }

    public static BioengineeringSkill get(String name) {
        return REGISTRY.get(new ResourceLocation(WildAside.MOD_ID, name));
    }

    public static BioengineeringSkill get(ResourceLocation id) {
        return REGISTRY.get(id);
    }

    public static Collection<BioengineeringSkill> all() {
        return REGISTRY.values();
    }
}
