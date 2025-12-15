package net.farkas.wildaside.screen.bioengineering_workstation;

import net.farkas.wildaside.dna.bioengineering_skill.BioengineeringSkill;
import net.farkas.wildaside.dna.bioengineering_skill.BioengineeringSkillUtils;
import net.minecraft.world.entity.player.Player;

public class SkillNode {
    public final BioengineeringSkill skill;
    public int x, y;

    public SkillNode(BioengineeringSkill skill) {
        this.skill = skill;
        this.x = 0;
        this.y = 0;
    }

    public boolean isUnlocked(Player player) {
        return BioengineeringSkillUtils.hasSkill(player, skill.getId());
    }

    public boolean canUnlock(Player player) {
        return BioengineeringSkillUtils.canUnlock(player, skill);
    }
}