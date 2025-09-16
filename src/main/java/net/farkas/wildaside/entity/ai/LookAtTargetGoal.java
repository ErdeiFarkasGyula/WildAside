package net.farkas.wildaside.entity.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Monster;

public class LookAtTargetGoal extends Goal {
    private LivingEntity target;

    public LookAtTargetGoal(Monster entity) {
        this.target = entity.getTarget();
    }

    @Override
    public boolean canUse() {
        return false;
    }
}
