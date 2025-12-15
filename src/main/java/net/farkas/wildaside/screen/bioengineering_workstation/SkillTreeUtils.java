package net.farkas.wildaside.screen.bioengineering_workstation;

import net.farkas.wildaside.dna.bioengineering_skill.BioengineeringSkill;

import java.util.*;

public final class SkillTreeUtils {
    public static List<BioengineeringSkill> topologicalSort(Collection<BioengineeringSkill> skills, Map<BioengineeringSkill, List<BioengineeringSkill>> parents) {
        List<BioengineeringSkill> result = new ArrayList<>();
        Set<BioengineeringSkill> visited = new HashSet<>();
        Set<BioengineeringSkill> visiting = new HashSet<>();

        for (BioengineeringSkill skill : skills) {
            dfs(skill, parents, visited, visiting, result);
        }

        return result;
    }

    private static void dfs(BioengineeringSkill skill, Map<BioengineeringSkill, List<BioengineeringSkill>> parents,
            Set<BioengineeringSkill> visited, Set<BioengineeringSkill> visiting, List<BioengineeringSkill> result
    ) {
        if (visited.contains(skill)) return;
        if (visiting.contains(skill)) {
            throw new IllegalStateException("Cycle detected in skill graph at " + skill.getId());
        }

        visiting.add(skill);
        for (BioengineeringSkill p : parents.get(skill)) {
            dfs(p, parents, visited, visiting, result);
        }
        visiting.remove(skill);
        visited.add(skill);
        result.add(skill);
    }
}
