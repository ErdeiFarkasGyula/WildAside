package net.farkas.wildaside.screen.bioengineering_workstation;

import net.farkas.wildaside.dna.bioengineering_skill.BioengineeringSkill;
import net.farkas.wildaside.dna.bioengineering_skill.BioengineeringSkillRegistry;
import net.farkas.wildaside.dna.bioengineering_skill.VisualStrand;
import net.farkas.wildaside.dna.bioengineering_skill.requirement.IBioengineeringSkillRequirement;
import net.farkas.wildaside.dna.bioengineering_skill.requirement.SkillRequirement;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

public class BioengineeringSkillTreeRegistry {

    public static final List<SkillNode> NODES = new ArrayList<>();

    private static final int X_SPACING = 30;
    private static final int STRAND_OFFSET = 40;
    private static final float FREQUENCY = (float) Math.PI;

    public static void rebuild() {
        NODES.clear();

        Collection<BioengineeringSkill> skills = BioengineeringSkillRegistry.all();

        Map<BioengineeringSkill, List<BioengineeringSkill>> parents = new HashMap<>();
        Map<BioengineeringSkill, List<BioengineeringSkill>> children = new HashMap<>();

        for (BioengineeringSkill skill : skills) {
            parents.put(skill, new ArrayList<>());
            children.put(skill, new ArrayList<>());
        }

        for (BioengineeringSkill skill : skills) {
            for (ResourceLocation id : skill.getRequirement().getRequiredSkills()) {
                BioengineeringSkill parent = BioengineeringSkillRegistry.get(id);
                if (parent != null) {
                    parents.get(skill).add(parent);
                    children.get(parent).add(skill);
                }
            }
        }

        List<BioengineeringSkill> topo = SkillTreeUtils.topologicalSort(skills, parents);

        Map<BioengineeringSkill, Integer> depth = new HashMap<>();

        for (BioengineeringSkill skill : topo) {
            int maxParent = -1;
            for (BioengineeringSkill p : parents.get(skill)) {
                maxParent = Math.max(maxParent, depth.get(p));
            }
            depth.put(skill, maxParent + 1);
        }

        Map<BioengineeringSkill, Float> t = new HashMap<>();

        for (VisualStrand strand : VisualStrand.values()) {
            List<BioengineeringSkill> strandSkills = topo.stream()
                    .filter(s -> s.getStrand() == strand)
                    .toList();

            if (strandSkills.isEmpty()) continue;

            int min = strandSkills.stream().mapToInt(depth::get).min().orElse(0);
            int max = strandSkills.stream().mapToInt(depth::get).max().orElse(min);

            float range = Math.max(1, max - min);

            for (BioengineeringSkill s : strandSkills) {
                t.put(s, (depth.get(s) - min) / range);
            }
        }

        for (BioengineeringSkill skill : topo) {
            int d = depth.get(skill);
            int x = d * X_SPACING;

            int y = switch (skill.getStrand()) {
                case TOP -> -STRAND_OFFSET;
                case BOTTOM -> STRAND_OFFSET;
                case CENTER -> 0;
            };

            NODES.add(new SkillNode(skill, x, y));
        }

        for (SkillNode node : NODES) {
            System.out.println("Skill: " + node.skill + " x: " + node.x + " y: " + node.y);
        }
    }
}
