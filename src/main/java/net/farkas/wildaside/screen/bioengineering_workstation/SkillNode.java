package net.farkas.wildaside.screen.bioengineering_workstation;

import net.farkas.wildaside.dna.bioengineering_skill.BioengineeringSkill;
import net.farkas.wildaside.dna.bioengineering_skill.BioengineeringSkillUtils;
import net.minecraft.world.entity.player.Player;

public class SkillNode {
    public final BioengineeringSkill skill;
    public int x, y;

    public SkillNode(BioengineeringSkill skill, int x, int y) {
        this.skill = skill;
        this.x = x;
        this.y = y;
    }

    public boolean isUnlocked(Player player) {
        return BioengineeringSkillUtils.hasSkill(player, skill.getId());
    }

    public boolean canUnlock(Player player) {
        return BioengineeringSkillUtils.canUnlock(player, skill);
    }
}