package net.farkas.wildaside.entity.ai.mucellith;

import net.farkas.wildaside.entity.custom.MucellithEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;

public class MucellithRandomLookAroundGoal extends RandomLookAroundGoal {
    MucellithEntity entity;

    public MucellithRandomLookAroundGoal(MucellithEntity pMob) {
        super(pMob);
        this.entity = pMob;
    }

    @Override
    public boolean canUse() {
        if (entity.isDefending()) return false;
        return super.canUse();
    }
}
