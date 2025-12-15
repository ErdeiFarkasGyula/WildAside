package net.farkas.wildaside.screen.bioengineering_workstation;

import java.util.ArrayList;
import java.util.List;

public class SkillTreeLayout {
    private static final float NODE_SPACING_X = 80f;
    private static final float NODE_SPACING_Y = 40f;
    private static final float HELIX_AMPLITUDE = 20f;
    private static final float HELIX_FREQUENCY = (float) Math.PI;

    public static void layoutNodes(List<SkillNode> allNodes) {
        List<SkillNode> roots = new ArrayList<>();
        for (SkillNode node : allNodes) {
            if (node.skill.getRequirement().getRequiredSkills().isEmpty()) {
                roots.add(node);
            }
        }

        int rootIndex = 0;
        for (SkillNode root : roots) {
            layoutBranch(root, 0, 0, rootIndex, allNodes, new ArrayList<>());
            rootIndex++;
        }
    }

    private static void layoutBranch(SkillNode node, float x, int layer, int branchIndex,
                                     List<SkillNode> allNodes, List<SkillNode> visited) {
        if (visited.contains(node)) return;
        visited.add(node);

        node.x = (int) (x + branchIndex * NODE_SPACING_X);
        node.y = (int) (layer * NODE_SPACING_Y);

        float helixOffset = (float) Math.sin(layer * HELIX_FREQUENCY) * HELIX_AMPLITUDE;
        if (branchIndex % 2 == 0) {
            node.y -= helixOffset;
        } else {
            node.y += helixOffset;
        }

        List<SkillNode> children = SkillTreeUtils.getDependentNodes(node, allNodes);
        for (int i = 0; i < children.size(); i++) {
            SkillNode child = children.get(i);
            layoutBranch(child, x + i * NODE_SPACING_X / 2f, layer + 1, branchIndex, allNodes, visited);
        }
    }
}
