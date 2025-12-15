package net.farkas.wildaside.screen.bioengineering_workstation;

import net.minecraft.resources.ResourceLocation;

import java.util.*;

public class SkillTreeUtils {
    public static Map<SkillNode, Integer> computeDepths(Collection<SkillNode> nodes) {
        Map<SkillNode, Integer> depthMap = new HashMap<>();
        Set<SkillNode> visited = new HashSet<>();

        List<SkillNode> roots = nodes.stream()
                .filter(n -> n.skill.getRequirement().getRequiredSkills().isEmpty())
                .toList();

        for (SkillNode root : roots) {
            dfsDepth(root, 0, depthMap, visited, nodes);
        }

        return depthMap;
    }

    private static void dfsDepth(SkillNode node, int depth, Map<SkillNode, Integer> depthMap, Set<SkillNode> visited, Collection<SkillNode> allNodes) {
        if (visited.contains(node)) return;
        visited.add(node);

        depthMap.put(node, Math.max(depthMap.getOrDefault(node, 0), depth));

        for (ResourceLocation depId : node.skill.getRequirement().getRequiredSkills()) {
            SkillNode child = allNodes.stream()
                    .filter(n -> n.skill.getId().equals(depId))
                    .findFirst().orElse(null);
            if (child != null) dfsDepth(child, depth + 1, depthMap, visited, allNodes);
        }
    }

    public static boolean isTopStrand(SkillNode node) {
        int depth = computeDepths(List.of(node)).getOrDefault(node, 0);
        return (depth % 2) == 0;
    }


    public static SkillNode getNode(Collection<SkillNode> nodes, ResourceLocation id) {
        return nodes.stream().filter(n -> n.skill.getId().equals(id)).findFirst().orElse(null);
    }

    public static List<SkillNode> getDependentNodes(SkillNode node, List<SkillNode> allNodes) {
        List<SkillNode> dependents = new ArrayList<>();
        ResourceLocation nodeId = node.skill.getId();

        for (SkillNode candidate : allNodes) {
            if (candidate.skill.getRequirement().dependsOn(nodeId)) {
                dependents.add(candidate);
            }
        }

        return dependents;
    }
}
