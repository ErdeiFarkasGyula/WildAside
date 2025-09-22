package net.farkas.wildaside.entity.ai.mucellith;

import net.farkas.wildaside.entity.custom.vibrion.MucellithEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;

public class MucellithLookAtPlayerGoal extends LookAtPlayerGoal {
    final MucellithEntity entity;

    public MucellithLookAtPlayerGoal(MucellithEntity pMob, Class<? extends LivingEntity> pLookAtType, float pLookDistance) {
        super(pMob, pLookAtType, pLookDistance);
        this.entity = pMob;
    }

    @Override
    public boolean canUse() {
        if (entity.isDefending()) return false;
        return super.canUse();
    }
}
