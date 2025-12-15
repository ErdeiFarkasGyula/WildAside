package net.farkas.wildaside.screen.bioengineering_workstation;

import net.farkas.wildaside.dna.bioengineering_skill.BioengineeringSkill;
import net.farkas.wildaside.dna.bioengineering_skill.BioengineeringSkillRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.*;

public class BioengineeringSkillTreeRegistry {
    public static final List<SkillNode> NODES = new ArrayList<>();

    private static final int X_SPACING = 42;
    private static final float WAVE_AMPLITUDE = 12f;
    private static final float FREQUENCY = 1.2f;

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

            int maxDepth = depth.values().stream().max(Integer::compareTo).orElse(1);

            for (BioengineeringSkill s : topo) {
                t.put(s, depth.get(s) / (float) maxDepth);
            }

        }

        for (BioengineeringSkill skill : topo) {
            int d = depth.get(skill);

            float tNorm = t.getOrDefault(skill, 0f);
            VisualStrand strand = skill.getStrand();

            float x = d * X_SPACING + strand.xOffset;

            float wave = (float) Math.sin(tNorm * FREQUENCY * 2f * Math.PI + strand.phase);

            float y = strand.baseY + wave * WAVE_AMPLITUDE;


            NODES.add(new SkillNode(skill, Math.round(x), Math.round(y)));
        }

        for (SkillNode node : NODES) {
            System.out.println("Skill: " + node.skill + " x: " + node.x + " y: " + node.y);
        }
    }
}
