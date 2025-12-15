package net.farkas.wildaside.screen.bioengineering_workstation;

import java.util.Collection;
import java.util.Map;

public class HelixLayouter {
    public static void layout(Collection<SkillNode> nodes, float originX, float originY, float horizSpacing, float amplitude) {
        Map<SkillNode, Integer> depths = SkillTreeUtils.computeDepths(nodes);

        float frequency = (float)Math.PI * 2f / 6f;

        for (SkillNode node : nodes) {
            int depth = depths.getOrDefault(node, 0);

            float x = originX + depth * horizSpacing;

            boolean topStrand = SkillTreeUtils.isTopStrand(node);

            float phase = topStrand ? 0f : (float)Math.PI;

            float y = originY + (float)Math.sin(depth * frequency + phase) * amplitude;

            node.x = Math.round(x);
            node.y = Math.round(y);
        }
    }

}
