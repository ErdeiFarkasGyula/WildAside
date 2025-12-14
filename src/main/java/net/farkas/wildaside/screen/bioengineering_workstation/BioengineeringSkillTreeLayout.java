package net.farkas.wildaside.screen.bioengineering_workstation;

import net.farkas.wildaside.dna.bioengineering_skill.BioengineeringSkill;
import net.farkas.wildaside.dna.bioengineering_skill.BioengineeringSkillRegistry;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class BioengineeringSkillTreeLayout {
    public static final List<SkillNode> NODES = new ArrayList<>();

    static {
        NODES.add(new SkillNode(BioengineeringSkillRegistry.REVEAL_ALLELES, 0, 0));
        NODES.add(new SkillNode(BioengineeringSkillRegistry.EDIT_ALLELES, 50, 50));
        NODES.add(new SkillNode(BioengineeringSkillRegistry.MEOW, 50, -50));
        NODES.add(new SkillNode(BioengineeringSkillRegistry.WOOF, 200, 0));
    }

    public static List<SkillEdge> EDGES = new ArrayList<>();

    public static void rebuildEdges() {
        EDGES.clear();

        for (BioengineeringSkill skill : BioengineeringSkillRegistry.all()) {
            SkillNode child = getNodeBySkill(skill);
            if (child == null) continue;

            for (ResourceLocation parentId : skill.getRequirement().getRequiredSkills()) {
                BioengineeringSkill parent = BioengineeringSkillRegistry.get(parentId);
                if (parent == null) continue;

                SkillNode parentNode = getNodeBySkill(parent);
                if (parentNode == null) continue;

                EDGES.add(new SkillEdge(parentNode, child));
            }
        }
    }

    public static SkillNode getNodeBySkill(BioengineeringSkill skill) {
        for (SkillNode node : NODES) {
            if (node.skill == skill) return node;
        }
        return null;
    }
}
