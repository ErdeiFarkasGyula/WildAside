package net.farkas.wildaside.entity.custom.hickory;

import net.farkas.wildaside.block.ModBlocks;
import net.farkas.wildaside.block.custom.RootBushBlock;
import net.farkas.wildaside.entity.ai.hickory.HickoryTreantBeamAttackGoal;
import net.farkas.wildaside.entity.ai.hickory.HickoryTreantMeleeAttackGoal;
import net.farkas.wildaside.entity.ai.hickory.HickoryTreantRootAttackGoal;
import net.farkas.wildaside.util.HickoryColour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class HickoryTreantEntity extends Monster {
    private static final EntityDataAccessor<Integer> PHASE = SynchedEntityData.defineId(HickoryTreantEntity.class, EntityDataSerializers.INT);
    public int rootCooldown;
    public int beamCooldown;
    public int previousAttackX;
    public int previousAttackY;
    public int previousAttackZ;

    public HickoryTreantEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.rootCooldown = getRootInterval(1);
        this.beamCooldown = getBeamInterval(1);
        this.previousAttackX = 0;
        this.previousAttackZ = 0;
        this.previousAttackY = 0;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 200.0)
                .add(Attributes.MOVEMENT_SPEED, 0.2)
                .add(Attributes.ATTACK_KNOCKBACK, 1.0)
                .add(Attributes.ATTACK_DAMAGE, 6.0)
                .add(Attributes.FOLLOW_RANGE, 24)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));

        this.goalSelector.addGoal(3, new HickoryTreantRootAttackGoal(this));
        this.goalSelector.addGoal(3, new HickoryTreantBeamAttackGoal(this));
        this.goalSelector.addGoal(3, new HickoryTreantMeleeAttackGoal(this, 1, true));

        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 24));
    }

    @Override
    public void remove(RemovalReason pReason) {
        super.remove(pReason);
        resetRoots(getPhase());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(PHASE, 1);
    }

    public int getPhase() {
        return this.entityData.get(PHASE);
    }

    public void setPhase(int phase) {
        this.entityData.set(PHASE, phase);
    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide) {
            animationHandler();
        }
    }

    private void animationHandler() {

    }

    @Override
    public void aiStep() {
        super.aiStep();

        rootCooldown--;
        beamCooldown--;

        this.setPhase(updatePhase());

        if (getTarget() != null) {
            getNavigation().moveTo(getTarget(), 1.0);
            getLookControl().setLookAt(getTarget(), 30, 30);
        }

        bossEvent.setProgress(healthPresentage());

        switch (getPhase()) {
            case 1:
                bossEvent.setColor(BossEvent.BossBarColor.GREEN);
                break;
            case 2:
                bossEvent.setColor(BossEvent.BossBarColor.YELLOW);
                break;
            case 3:
                this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, MobEffectInstance.INFINITE_DURATION, 0, true, false));
                bossEvent.setColor(BossEvent.BossBarColor.RED);
                break;
            case 4:
                this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, MobEffectInstance.INFINITE_DURATION, 1, true, false));
                this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, MobEffectInstance.INFINITE_DURATION, 1, true, false));
                bossEvent.setColor(BossEvent.BossBarColor.WHITE);
                break;
        }
    }


    public float healthPresentage() {
        return this.getHealth() / this.getMaxHealth();
    }

    private int updatePhase() {
        double percent = healthPresentage();
        if (percent <= 0.25) return 4;
        if (percent <= 0.5) return 3;
        if (percent <= 0.75) return 2;
        return 1;
    }

    public int getRootInterval(int phase) {
        return switch (phase) {
            case 2 -> 120;
            case 3 -> 100;
            case 4 -> 80;
            default -> 140;
        };
    }

    public int getBeamInterval(int phase) {
        return switch (phase) {
            case 3 -> 80;
            case 4 -> 60;
            default -> 100;
        };
    }


//        // 4) Sprinkle some leaf‐dust particles for visual flair
//        for (int i = 0; i < 20; i++) {
//            level.addParticle(ParticleTypes.FALLING_LEAVES,
//                    getX() + (random.nextDouble() - 0.5) * 2,
//                    getY() + random.nextDouble() * 2,
//                    getZ() + (random.nextDouble() - 0.5) * 2,
//                    1, 0, 0, 0, 0);
//        }

    public int getRootPatchRadius(int phase) {
        return phase >= 3 ? 2 : 1;
    }

    public void doRootingAttack(int phase) {
        resetRoots(phase);

        Level level = this.level();
        BlockPos rootPos = getTarget().blockPosition().below();
        previousAttackX = rootPos.getX();
        previousAttackY = rootPos.getY();
        previousAttackZ = rootPos.getZ();

        int radius = getRootPatchRadius(phase);
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (!(Math.abs(dx) == radius && Math.abs(dz) == radius)) {
                        BlockPos pos = rootPos.offset(dx, dy, dz);
                        BlockPos placePos = pos.above();
                        if (level.isEmptyBlock(placePos) && level.getBlockState(pos).isFaceSturdy(level, pos, Direction.UP)) {
                            level.setBlock(placePos, ModBlocks.HICKORY_ROOT_BUSH.get().defaultBlockState().setValue(RootBushBlock.AGE, phase - 1), 3);
                        }
                    }
                }
            }
        }
//           for (int i = 0; i < 10; i++) {
//                entity.level.sendParticles(ParticleTypes.ENCHANT,
//                        target.getX() + entity.random.nextDouble() - 0.5,
//                        target.getY() + 0.1,
//                        target.getZ() + entity.random.nextDouble() - 0.5,
//                        1, 0, 0, 0, 0);
//            }
    }

    public void resetRoots(int phase) {
        Level level = level();
        BlockPos rootPos = new BlockPos(previousAttackX, previousAttackY, previousAttackZ);

        int radius = getRootPatchRadius(phase);
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    BlockPos pos = rootPos.offset(dx, dy, dz);
                    BlockPos placePos = pos.above();
                    if (level.getBlockState(placePos).getBlock() instanceof RootBushBlock) {
                        level.removeBlock(placePos, false);
                    }
                }
            }
        }
    }

    public void doBeamAttack(int phase) {
        Level level = level();
        RandomSource random = getRandom();

        HickoryColour[] values = HickoryColour.values();
        HickoryColour colour = values[random.nextInt(values.length)];

        int count = phase * 8;
        float spread = (phase >= 3) ? 45f : 30f;
        float speed = 2f;

        for (int i = 0; i < count; i++) {
            HickoryLeafProjectile proj = new HickoryLeafProjectile(level, this, colour);
            proj.setPos(getX(), getEyeY() - 0.1, getZ());
            float yaw = getYRot() + (random.nextFloat() - 0.5f) * spread;
            float pitch = getXRot() + (random.nextFloat() - 0.5f) * spread * 0.5f;
            Vec3 dir = Vec3.directionFromRotation(pitch, yaw);
            proj.shoot(dir.x, dir.y, dir.z, speed, 0.1f);
            level.addFreshEntity(proj);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("phase", getPhase());
        tag.putInt("rootCD", rootCooldown);
        tag.putInt("beamCD", beamCooldown);
        tag.putInt("paX", previousAttackX);
        tag.putInt("paY", previousAttackY);
        tag.putInt("paZ", previousAttackZ);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setPhase(tag.getInt("phase"));
        rootCooldown = tag.getInt("rootCD");
        beamCooldown = tag.getInt("beamCD");
        previousAttackX = tag.getInt("paX");
        previousAttackY = tag.getInt("paY");
        previousAttackZ = tag.getInt("paZ");
    }

    private final ServerBossEvent bossEvent = new ServerBossEvent(
            Component.translatable("entity.wildaside.hickory_treant"),
            BossEvent.BossBarColor.GREEN, BossEvent.BossBarOverlay.NOTCHED_12);

    @Override
    public void startSeenByPlayer(ServerPlayer pServerPlayer) {
        super.startSeenByPlayer(pServerPlayer);
        this.bossEvent.addPlayer(pServerPlayer);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer pServerPlayer) {
        super.stopSeenByPlayer(pServerPlayer);
        this.bossEvent.removePlayer(pServerPlayer);
    }
}
