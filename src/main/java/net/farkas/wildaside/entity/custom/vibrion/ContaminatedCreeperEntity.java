package net.farkas.wildaside.entity.custom.vibrion;

import net.farkas.wildaside.entity.ai.contaminated.ApproachWhenLookedAtGoal;
import net.farkas.wildaside.entity.ai.contaminated.BurrowedChaseGoal;
import net.farkas.wildaside.entity.ai.contaminated.BurrowedNavigation;
import net.farkas.wildaside.entity.ai.contaminated.SurfaceChaseGoal;
import net.farkas.wildaside.particle.ModParticles;
import net.farkas.wildaside.util.ContaminationHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.List;

public class ContaminatedCreeperEntity extends Creeper {
    private static final EntityDataAccessor<Integer> STATE = SynchedEntityData.defineId(ContaminatedCreeperEntity.class, EntityDataSerializers.INT);

    public static final int STATE_IDLE = 0;
    public static final int STATE_FOLLOWING = 1;
    public static final int STATE_CHASE = 2;
    public static final int STATE_BURROWED = 3;

    public ContaminatedCreeperEntity(EntityType<? extends Creeper> type, Level level) {
        super(type, level);
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.@NotNull Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FOLLOW_RANGE, 48.0D);
    }

    public void explode() {
        if (!this.level().isClientSide) {
            int f = this.isPowered() ? 2 : 1;
            int radius = 3 * f;
            this.dead = true;
            this.level().explode(this, this.getX(), this.getY(), this.getZ(), radius, Level.ExplosionInteraction.MOB);
            applySporeCloud(radius + 1);
            this.discard();
        }
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 64));
        this.goalSelector.addGoal(5, new RandomStrollGoal(this, 0.95D, 40));

        this.goalSelector.addGoal(1, new BurrowedChaseGoal(this, 1.5D, 2.0D, 200));
        this.goalSelector.addGoal(2, new SurfaceChaseGoal(this, 1.5D, 48));
        this.goalSelector.addGoal(3, new ApproachWhenLookedAtGoal(this, 1D, 48, 24D));

        this.goalSelector.addGoal(4, new FloatGoal(this));
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide) {
            if (this.getState() == STATE_BURROWED) {
                for (int i = 0; i < 3; i++) {
                    double dx = this.getX() + (this.random.nextDouble() - 0.5) * 0.5;
                    double dy = this.getY();
                    double dz = this.getZ() + (this.random.nextDouble() - 0.5) * 0.5;
                    this.level().addParticle(new DustParticleOptions(new Vector3f(), 1), dx, dy, dz, 0, 0.05, 0);
                }
            }
        } else {
            if (this.getState() == STATE_BURROWED) {
                this.navigation = new BurrowedNavigation(this, this.level());
                this.noPhysics = true;
            } else {
                this.navigation = new GroundPathNavigation(this, this.level());
                this.noPhysics = false;
                if (this.getTarget() != null) {
                    this.setState(STATE_CHASE);
                } else {
                    this.setState(STATE_IDLE);
                }
            }
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(STATE, STATE_IDLE);
    }

    public void setState(int state) {
        this.entityData.set(STATE, state);
    }

    public int getState() {
        return this.entityData.get(STATE);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("ContaminatedCreeperState", this.getState());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setState(tag.getInt("ContaminatedCreeperState"));
    }

    private void applySporeCloud(int radius) {
        Level level = this.level();
        RandomSource rand = level.random;
        BlockPos center = this.blockPosition();
        SimpleParticleType particle = ModParticles.VIBRION_PARTICLE.get();

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (dx * dx + dy * dy + dz * dz <= radius * radius) {
                        for (int i = 0; i < 3; i++) {
                            double x = center.getX() + 0.5 + dx + (rand.nextDouble() - 0.5);
                            double y = center.getY() + 0.5 + dy + (rand.nextDouble() - 0.5);
                            double z = center.getZ() + 0.5 + dz + (rand.nextDouble() - 0.5);
                            level.addParticle(particle, x, y, z,
                                    1, 0, 0);
                        }
                    }
                }
            }
        }

        AABB box = new AABB(center).inflate(radius);
        List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, box, e -> !e.isSpectator());

        for (LivingEntity entity : list) {
            ContaminationHandler.giveContaminationDose(entity, rand.nextInt(1500, 2500) * (isPowered() ? 2 : 1));
            level.addParticle(particle,
                    entity.getX(), entity.getY() + 0.5, entity.getZ(),
                    5, 0.2, 0.2);
        }
    }

    public boolean hasAdjacentBlock(Block block) {
        for (Direction direction : Direction.values()) {
            if (level().getBlockState())
        }
    }
}
