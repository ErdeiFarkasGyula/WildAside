package net.farkas.wildaside.entity.ai.mucellith;

import net.farkas.wildaside.entity.custom.MucellithEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.goal.Goal;

public class MucellithDefendGoal extends Goal {
    private final MucellithEntity entity;

    public MucellithDefendGoal(MucellithEntity entity) {
        this.entity = entity;
    }

    @Override
    public boolean canUse() {
        return !entity.hasDefended() && !entity.isAttacking();
    }

    @Override
    public void stop() {
        entity.removeEffect(MobEffects.REGENERATION);
        entity.removeEffect(MobEffects.ABSORPTION);

        entity.setHasDefended(true);
        entity.setDefending(false);
    }

    @Override
    public void start() {
        super.start();
        entity.setDefending(true);

        MobEffectInstance regeneration = new MobEffectInstance(MobEffects.REGENERATION, Integer.MAX_VALUE, 2, true, false);
        MobEffectInstance absorption = new MobEffectInstance(MobEffects.ABSORPTION, Integer.MAX_VALUE, 3, true, false);

        entity.addEffect(regeneration);
        entity.addEffect(absorption);
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();

        if (!entity.belowHealthThreshold(0.5f)) {
            stop();
        }
    }
}
