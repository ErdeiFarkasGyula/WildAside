package net.farkas.wildaside.screen.bioengineering_workstation;

import net.farkas.wildaside.dna.bioengineering_skill.BioengineeringSkillRegistry;

import java.util.*;

public class BioengineeringSkillTreeRegistry {
    public static final List<SkillNode> NODES = new ArrayList<>();

    public static void rebuild() {
        NODES.clear();

        NODES.add(new SkillNode(BioengineeringSkillRegistry.REVEAL_ALLELES, 0, 0));

        NODES.add(new SkillNode(BioengineeringSkillRegistry.TOP_1, 35, 33));
        NODES.add(new SkillNode(BioengineeringSkillRegistry.TOP_2, 80, 50));
        NODES.add(new SkillNode(BioengineeringSkillRegistry.TOP_3, 124, 33));

        NODES.add(new SkillNode(BioengineeringSkillRegistry.BOT_1, 45, -40));
        NODES.add(new SkillNode(BioengineeringSkillRegistry.BOT_2, 105, -40));

        NODES.add(new SkillNode(BioengineeringSkillRegistry.END, 150, 0));
    }
}
