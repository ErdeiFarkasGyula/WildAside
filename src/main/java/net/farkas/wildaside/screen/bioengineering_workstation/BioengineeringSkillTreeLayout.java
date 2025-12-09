package net.farkas.wildaside.screen.bioengineering_workstation;

import net.farkas.wildaside.dna.bioengineering_skill.BioengineeringSkillRegistry;

import java.util.ArrayList;
import java.util.List;

public class BioengineeringSkillTreeLayout {
    public static final List<SkillNode> NODES = new ArrayList<>();

    static {
        NODES.add(new SkillNode(BioengineeringSkillRegistry.REVEAL_ALLELES, 50, 50));
    }
}
