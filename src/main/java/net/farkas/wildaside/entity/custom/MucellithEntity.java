package net.farkas.wildaside.entity.custom;

import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidType;

public class MucellithEntity extends PathfinderMob {
    public MucellithEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public final AnimationState idleAnimation = new AnimationState();
    private int idleAnimationTimeout = 0;

//    @Override
//    protected void registerGoals() {
//        this.goalSelector.addGoal();
//    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide) {

        }
    }

    private void setupAnimationStates() {
        if (this.idleAnimationTimeout <= 0) {

        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 20)
                .add(Attributes.FOLLOW_RANGE, 240)
                .add(Attributes.MOVEMENT_SPEED, 0)
                .add(Attributes.JUMP_STRENGTH, 0)
                .add(Attributes.FLYING_SPEED, 0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1);
    }

    @Override
    public void push(Entity pEntity) {
        //NO PUSH!
    }

    @Override
    public void push(double pX, double pY, double pZ) {
        //NO PUSH!
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isPushedByFluid(FluidType type) {
        return false;
    }

    @Override
    public boolean ignoreExplosion() {
        return true;
    }
}
