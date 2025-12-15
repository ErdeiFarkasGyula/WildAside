package net.farkas.wildaside.screen.bioengineering_workstation;

import net.farkas.wildaside.dna.bioengineering_skill.BioengineeringSkill;
import net.farkas.wildaside.dna.bioengineering_skill.BioengineeringSkillRegistry;

import java.util.*;

public class BioengineeringSkillTreeRegistry {
    public static final List<SkillNode> NODES = new ArrayList<>();

    public static void rebuild() {
        NODES.clear();

        for (var skill : BioengineeringSkillRegistry.all()) {
            NODES.add(new SkillNode(skill));
        }

        HelixLayouter.layout(NODES, 0, 0, 200, 1);
    }

    private static void registerNodes() {
        for (BioengineeringSkill skill : BioengineeringSkillRegistry.all()) {
            SkillNode skillNode = new SkillNode(skill);
            NODES.add(skillNode);
        }
    }
}
