package net.farkas.wildaside.entity.ai.contaminated;

import net.farkas.wildaside.entity.custom.vibrion.ContaminatedCreeperEntity;
import net.minecraft.world.entity.ai.goal.MoveTowardsTargetGoal;

public class ChaseTargetGoal extends MoveTowardsTargetGoal {
    private final ContaminatedCreeperEntity creeper;

    public ChaseTargetGoal(ContaminatedCreeperEntity creeper, double pSpeedModifier, float pWithin) {
        super(creeper, pSpeedModifier, pWithin);
        this.creeper = creeper;
    }

    @Override
    public void start() {
        super.start();
        creeper.setState(ContaminatedCreeperEntity.STATE_CHASE);
    }
}
