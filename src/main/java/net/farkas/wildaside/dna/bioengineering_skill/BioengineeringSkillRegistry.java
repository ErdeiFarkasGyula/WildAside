package net.farkas.wildaside.dna.bioengineering_skill;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.dna.bioengineering_skill.category.BioengineeringSkillCategoryRegistry;
import net.farkas.wildaside.dna.bioengineering_skill.requirement.AllRequirements;
import net.farkas.wildaside.dna.bioengineering_skill.requirement.ItemRequirement;
import net.farkas.wildaside.item.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BioengineeringSkillRegistry {
    private static final Map<ResourceLocation, BioengineeringSkill> REGISTRY = new HashMap<>();

    public static final BioengineeringSkill REVEAL_ALLELES = register(new BioengineeringSkill.Builder()
            .name("reveal_alleles")
            .category(BioengineeringSkillCategoryRegistry.CATEGORY_1)
            .requirement(
                    new AllRequirements(
                            List.of(
                                new ItemRequirement(new ItemStack(ModItems.HICKORY_NUT.get(), 10), true)
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
