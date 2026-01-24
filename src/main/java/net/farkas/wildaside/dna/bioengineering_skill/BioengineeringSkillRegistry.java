package net.farkas.wildaside.dna.bioengineering_skill;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.advancement.ModAdvancements;
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

    public static final BioengineeringSkill REVEAL_SEQUENCES = register(new BioengineeringSkill.Builder()
            .name("reveal_sequences")
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

    public static final BioengineeringSkill SWAP_SEQUENCES = register(new BioengineeringSkill.Builder()
            .name("swap_sequences")
            .category(BioengineeringSkillCategoryRegistry.CATEGORY_1)
            .requirement(
                    new AllRequirements(
                            List.of(
                                    new PointRequirement(20),
                                    new SkillRequirement(REVEAL_SEQUENCES.getId())
                            ))).build());

    public static final BioengineeringSkill ISOLATE_SEQUENCE = register(new BioengineeringSkill.Builder()
            .name("isolate_sequence")
            .category(BioengineeringSkillCategoryRegistry.CATEGORY_1)
            .requirement(
                    new AllRequirements(
                            List.of(
                                    new PointRequirement(40),
                                    new SkillRequirement(SWAP_SEQUENCES.getId())
                            ))).build());

    public static final BioengineeringSkill MODIFY_DOMINANCE = register(new BioengineeringSkill.Builder()
            .name("modify_dominance")
            .category(BioengineeringSkillCategoryRegistry.CATEGORY_1)
            .requirement(
                    new AllRequirements(
                            List.of(
                                    new PointRequirement(30),
                                    new SkillRequirement(ISOLATE_SEQUENCE.getId()),
                                    new AdvancementRequirement(ModAdvancements.BACTERIA_BRICKS)
                            ))).build());

    public static final BioengineeringSkill STABILIZE_GENE = register(new BioengineeringSkill.Builder()
            .name("stabilize_gene")
            .category(BioengineeringSkillCategoryRegistry.CATEGORY_1)
            .requirement(
                    new AllRequirements(
                            List.of(
                                    new PointRequirement(25),
                                    new SkillRequirement(REVEAL_SEQUENCES.getId())
                            ))).build());


    public static final BioengineeringSkill SUPPRESS_GENE = register(new BioengineeringSkill.Builder()
            .name("suppress_gene")
            .category(BioengineeringSkillCategoryRegistry.CATEGORY_1)
            .requirement(
                    new AllRequirements(
                            List.of(
                                    new PointRequirement(35),
                                    new SkillRequirement(MODIFY_DOMINANCE.getId())
                            ))).build());

    public static final BioengineeringSkill AMPLIFY_GENE = register(new BioengineeringSkill.Builder()
            .name("amplify_gene")
            .category(BioengineeringSkillCategoryRegistry.CATEGORY_1)
            .requirement(
                    new AllRequirements(
                            List.of(
                                    new PointRequirement(35),
                                    new SkillRequirement(SUPPRESS_GENE.getId())
                            ))).build());

    public static final BioengineeringSkill SPLICE_SEQUENCE = register(new BioengineeringSkill.Builder()
            .name("splice_sequence")
            .category(BioengineeringSkillCategoryRegistry.CATEGORY_1)
            .requirement(
                    new AllRequirements(
                            List.of(
                                    new PointRequirement(50),
                                    new SkillRequirement(AMPLIFY_GENE.getId()),
                                    new SkillRequirement(SUPPRESS_GENE.getId())
                            ))).build());


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