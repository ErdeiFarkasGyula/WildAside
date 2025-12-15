package net.farkas.wildaside.dna.bioengineering_skill;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.dna.bioengineering_skill.category.BioengineeringSkillCategoryRegistry;
import net.farkas.wildaside.dna.bioengineering_skill.requirement.*;
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
                                    new PointRequirement(15),
                                    new AnyRequirements(
                                            List.of(
                                                    new ItemRequirement(new ItemStack(ModItems.HICKORY_NUT.get(), 10), true),
                                                    new ItemRequirement(new ItemStack(ModItems.MUCELLITH_JAW.get(), 1), false)
                                            )
                                    )
                            )
                    )).build());

    public static final BioengineeringSkill TOP_1 = register(new BioengineeringSkill.Builder()
            .name("top_1")
            .category(BioengineeringSkillCategoryRegistry.CATEGORY_1)
            .requirement(
                    new AllRequirements(
                            List.of(
                                    new PointRequirement(15),
                                    new SkillRequirement(REVEAL_ALLELES.getId())
                            )
                    )).build());

    public static final BioengineeringSkill TOP_2 = register(new BioengineeringSkill.Builder()
            .name("top_2")
            .category(BioengineeringSkillCategoryRegistry.CATEGORY_1)
            .requirement(
                    new AllRequirements(
                            List.of(
                                    new SkillRequirement(TOP_1.getId())
                            )
                    )).build());

    public static final BioengineeringSkill TOP_3 = register(new BioengineeringSkill.Builder()
            .name("top_3")
            .category(BioengineeringSkillCategoryRegistry.CATEGORY_1)
            .requirement(
                    new AllRequirements(
                            List.of(
                                    new SkillRequirement(TOP_2.getId())
                            )
                    )).build());

    public static final BioengineeringSkill BOT_1 = register(new BioengineeringSkill.Builder()
            .name("bot_1")
            .category(BioengineeringSkillCategoryRegistry.CATEGORY_1)
            .requirement(
                    new AllRequirements(
                            List.of(
                                    new SkillRequirement(REVEAL_ALLELES.getId())
                            )
                    )).build());

    public static final BioengineeringSkill BOT_2 = register(new BioengineeringSkill.Builder()
            .name("bot_2")
            .category(BioengineeringSkillCategoryRegistry.CATEGORY_1)
            .requirement(
                    new AllRequirements(
                            List.of(
                                    new SkillRequirement(BOT_1.getId())
                            )
                    )).build());

    public static final BioengineeringSkill END = register(new BioengineeringSkill.Builder()
            .name("end")
            .category(BioengineeringSkillCategoryRegistry.CATEGORY_1)
            .requirement(
                    new AllRequirements(
                            List.of(
                                    new SkillRequirement(TOP_3.getId()),
                                    new SkillRequirement(BOT_2.getId())
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
