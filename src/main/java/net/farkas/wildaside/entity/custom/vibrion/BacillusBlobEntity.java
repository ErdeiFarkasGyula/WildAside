package net.farkas.wildaside.entity.custom.vibrion;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.capability.dna.DnaCapability;
import net.farkas.wildaside.capability.dna.DnaImplementation;
import net.farkas.wildaside.dna.bacillus_blob.BacillusBlobConsumption;
import net.farkas.wildaside.dna.chromosome.Genome;
import net.farkas.wildaside.dna.trait.Trait;
import net.farkas.wildaside.entity.ModEntityTypes;
import net.farkas.wildaside.entity.ai.vibrion.bacillus_blob.BlobLeapAtTargetGoal;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import static net.farkas.wildaside.dna.DnaConstants.DNA_DATA;

public class BacillusBlobEntity extends PathfinderMob {
    private static final EntityDataAccessor<Integer> DATA_LIFESPAN = SynchedEntityData.defineId(
            BacillusBlobEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_AGE = SynchedEntityData.defineId(
            BacillusBlobEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_HAS_DNA = SynchedEntityData.defineId(
            BacillusBlobEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_ATTACHED_TO = SynchedEntityData.defineId(
            BacillusBlobEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_ATTACH_PROGRESS = SynchedEntityData.defineId(
            BacillusBlobEntity.class, EntityDataSerializers.INT);

    public static final int DEFAULT_LIFESPAN = 20 * 60 * 3;
    public static final int ATTACH_DURATION = 20 * 10;
    public static final float ATTACH_RANGE = 1.2f;

    private DnaImplementation carriedDna;
    private int attachAttemptCooldown = 0;
    private boolean enteringHost = false;

    private static final String TAG_LIFESPAN = "Lifespan";
    private static final String TAG_AGE = "BlobAge";
    private static final String TAG_CARRIED_DNA = "CarriedDna";
    private static final String TAG_ATTACH_PROGRESS = "AttachProgress";
    private static final String TAG_ATTACHED_TO = "AttachedTo";

    public BacillusBlobEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        this.setMaxUpStep(1.0f);
        this.carriedDna = new DnaImplementation();
    }

    public BacillusBlobEntity(Level level, double x, double y, double z) {
        this(ModEntityTypes.BACILLUS_BLOB.get(), level);
        this.setPos(x, y, z);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 6.0)
                .add(Attributes.MOVEMENT_SPEED, 0.35)
                .add(Attributes.ATTACK_DAMAGE, 1.0)
                .add(Attributes.FOLLOW_RANGE, 24.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_LIFESPAN, DEFAULT_LIFESPAN);
        this.entityData.define(DATA_AGE, 0);
        this.entityData.define(DATA_HAS_DNA, false);
        this.entityData.define(DATA_ATTACHED_TO, -1);
        this.entityData.define(DATA_ATTACH_PROGRESS, 0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new BlobLeapAtTargetGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.6, true));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Mob.class, 10, true, false,
                living -> living != null && !(living instanceof BacillusBlobEntity) && canInfect(living)));
    }

    private boolean canInfect(LivingEntity target) {
        if (target == null) return false;
        if (target instanceof BacillusBlobEntity) return false;
        if (target.isDeadOrDying()) return false;
        return target.getCapability(DnaCapability.INSTANCE).isPresent();
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide()) {
            tickServerSide();
        }
        else {
            tickClientSide();
        }
    }

    private void tickServerSide() {
        if (enteringHost) return;

        int age = getAge() + 1;
        setAge(age);

        if (age >= getLifespan()) {
            expireAndDie();
            return;
        }

        if (attachAttemptCooldown > 0) {
            attachAttemptCooldown--;
        }

        if (isAttached()) {
            tickAttachment();
        }
        else {
            tryAttachToNearbyTarget();
        }

        if (getLifespanPercent() < 0.25f && tickCount % 10 == 0) {
            spawnDyingParticles();
        }
    }

    private void tryAttachToNearbyTarget() {
        if (attachAttemptCooldown > 0) return;

        LivingEntity target = getTarget();
        if (target == null || !canInfect(target)) return;

        double distance = this.distanceTo(target);
        if (distance <= ATTACH_RANGE) {
            attachTo(target);
        }
    }

    private void tickClientSide() {
        if (isAttached()) {
            if (tickCount % 3 == 0) {
                spawnAttachedParticles();
            }
        }
        else if (tickCount % 8 == 0) {
            spawnAmbientParticles();
        }
    }

    private void tickAttachment() {
        LivingEntity attachedEntity = getAttachedEntity();

        if (attachedEntity == null || !attachedEntity.isAlive()) {
            WildAside.LOGGER.info("Attached entity invalid, detaching");
            detach();
            return;
        }

        Vec3 attachPos = getAttachPosition(attachedEntity);
        this.setPos(attachPos.x, attachPos.y, attachPos.z);
        this.setDeltaMovement(Vec3.ZERO);
        this.setYRot(attachedEntity.getYRot());

        int progress = getAttachProgress() + 1;
        setAttachProgress(progress);

        if (progress % 40 == 0 && progress < ATTACH_DURATION) {
            attachedEntity.hurt(damageSources().mobAttack(this), 0.5f);
            level().playSound(null, getX(), getY(), getZ(), SoundEvents.SLIME_SQUISH_SMALL, SoundSource.HOSTILE, 0.5f, 1.2f);

            if (attachedEntity instanceof Player player) {
                int remaining = (ATTACH_DURATION - progress) / 20;
                player.displayClientMessage(
                        Component.translatable("entity.wildaside.bacillus_blob.burrowing", remaining)
                                .withStyle(s -> s.withColor(0xFF6600)),
                        true
                );
            }
        }

        if (progress >= ATTACH_DURATION) {
            enterHost(attachedEntity);
        }
    }

    private Vec3 getAttachPosition(LivingEntity target) {
        double yOffset = target.getBbHeight() * 0.75;
        float angle = target.getYRot() * ((float) Math.PI / 180f);
        double xOffset = Math.sin(angle) * 0.2;
        double zOffset = -Math.cos(angle) * 0.2;
        return new Vec3(target.getX() + xOffset, target.getY() + yOffset, target.getZ() + zOffset);
    }

    public void attachTo(LivingEntity target) {
        if (target == null || isAttached() || enteringHost) return;
        if (!canInfect(target)) return;

        WildAside.LOGGER.info("Blob attaching to {}", target.getName().getString());

        entityData.set(DATA_ATTACHED_TO, target.getId());
        entityData.set(DATA_ATTACH_PROGRESS, 0);

        this.setNoGravity(true);
        this.noPhysics = true;
        this.setTarget(null);
        this.navigation.stop();

        level().playSound(null, target.getX(), target.getY(), target.getZ(),
                SoundEvents.SLIME_ATTACK, SoundSource.HOSTILE, 1.0f, 0.8f);

        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.ITEM_SLIME,
                    target.getX(), target.getY() + target.getBbHeight() * 0.5, target.getZ(),
                    10, 0.3, 0.3, 0.3, 0.05);
        }

        if (target instanceof Player player) {
            player.displayClientMessage(
                    Component.translatable("entity.wildaside.bacillus_blob.attached")
                            .withStyle(s -> s.withColor(0xFF4444).withBold(true)),
                    true
            );
        }
    }

    public void detach() {
        if (enteringHost) return;

        LivingEntity wasAttachedTo = getAttachedEntity();

        entityData.set(DATA_ATTACHED_TO, -1);
        entityData.set(DATA_ATTACH_PROGRESS, 0);

        this.setNoGravity(false);
        this.noPhysics = false;

        attachAttemptCooldown = 60;

        if (wasAttachedTo != null) {
            Vec3 awayDir = this.position().subtract(wasAttachedTo.position()).normalize();
            if (awayDir.length() < 0.1) {
                awayDir = new Vec3(random.nextDouble() - 0.5, 0, random.nextDouble() - 0.5).normalize();
            }
            this.setDeltaMovement(awayDir.x * 0.3, 0.2, awayDir.z * 0.3);
        }

        WildAside.LOGGER.info("Blob detached");
    }

    private void enterHost(LivingEntity host) {
        if (enteringHost) {
            WildAside.LOGGER.warn("enterHost called while already entering!");
            return;
        }

        enteringHost = true;

        WildAside.LOGGER.info("=== BLOB ENTERING HOST ===");
        WildAside.LOGGER.info("Host: {}", host.getName().getString());
        WildAside.LOGGER.info("carriedDna is null: {}", carriedDna == null);

        if (carriedDna == null) {
            WildAside.LOGGER.error("carriedDna is null!");
            discardWithMessage(host, "entity.wildaside.bacillus_blob.entered_empty", 0xFFAA00);
            return;
        }

        Genome genome = carriedDna.getGenome();
        
        if (genome == null || genome.isEmpty()) {
            WildAside.LOGGER.warn("Blob has no DNA to transfer!");
            discardWithMessage(host, "entity.wildaside.bacillus_blob.entered_empty", 0xFFAA00);
            return;
        }

        CompoundTag dnaTag = new CompoundTag();
        CompoundTag serializedDna = carriedDna.serializeNBT();
        dnaTag.put(DNA_DATA, serializedDna);

        WildAside.LOGGER.info("Serialized DNA tag keys: {}", serializedDna.getAllKeys());

        boolean success = BacillusBlobConsumption.consume(host, dnaTag);

        WildAside.LOGGER.info("BacillusBlobConsumption.consume returned: {}", success);

        level().playSound(null, host.getX(), host.getY(), host.getZ(),
                SoundEvents.SLIME_SQUISH, SoundSource.HOSTILE, 1.0f, 0.5f);

        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.ITEM_SLIME,
                    host.getX(), host.getY() + host.getBbHeight() * 0.5, host.getZ(),
                    25, 0.4, 0.4, 0.4, 0.1);
        }

        if (host instanceof Player player) {
            if (success) {
                player.displayClientMessage(
                        Component.translatable("entity.wildaside.bacillus_blob.entered")
                                .withStyle(s -> s.withColor(0xFF0000).withBold(true)),
                        true
                );
            }
            else {
                player.displayClientMessage(
                        Component.translatable("entity.wildaside.bacillus_blob.entered_failed")
                                .withStyle(s -> s.withColor(0xFF6600)),
                        true
                );
            }
        }

        WildAside.LOGGER.info("Blob discarding after entering host (success={})", success);
        this.discard();
    }

    private void discardWithMessage(LivingEntity host, String messageKey, int color) {
        level().playSound(null, host.getX(), host.getY(), host.getZ(),
                SoundEvents.SLIME_SQUISH, SoundSource.HOSTILE, 1.0f, 0.5f);

        if (host instanceof Player player) {
            player.displayClientMessage(
                    Component.translatable(messageKey)
                            .withStyle(s -> s.withColor(color)),
                    true
            );
        }

        this.discard();
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        if (isAttached() || enteringHost) return false;

        if (target instanceof LivingEntity living && canInfect(living)) {
            if (attachAttemptCooldown <= 0) {
                attachTo(living);
                return true;
            }
        }
        return super.doHurtTarget(target);
    }

    @Override
    public void push(Entity entity) {
        if (isAttached() || enteringHost) return;

        super.push(entity);

        if (entity instanceof LivingEntity living && canInfect(living) && attachAttemptCooldown <= 0) {
            attachTo(living);
        }
    }

    @Override
    protected void doPush(Entity entity) {
        if (isAttached() || enteringHost) return;

        super.doPush(entity);

        if (entity instanceof LivingEntity living && canInfect(living) && attachAttemptCooldown <= 0) {
            attachTo(living);
        }
    }

    @Override
    public void playerTouch(Player player) {
        super.playerTouch(player);

        if (!isAttached() && !enteringHost && attachAttemptCooldown <= 0 && canInfect(player)) {
            attachTo(player);
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (enteringHost) return false;

        if (isAttached()) {
            LivingEntity attached = getAttachedEntity();
            if (attached != null && source.getEntity() == attached) {
                amount *= 1.5f;
            }
        }

        boolean result = super.hurt(source, amount);

        if (this.isDeadOrDying() && isAttached()) {
            detach();
        }

        return result;
    }

    @Override
    public void knockback(double strength, double x, double z) {
        if (!isAttached()) {
            super.knockback(strength * 0.2, x, z);
        }
    }

    public boolean isAttached() {
        return entityData.get(DATA_ATTACHED_TO) != -1;
    }

    public LivingEntity getAttachedEntity() {
        int id = entityData.get(DATA_ATTACHED_TO);
        if (id == -1) return null;
        Entity entity = level().getEntity(id);
        return entity instanceof LivingEntity living ? living : null;
    }

    public int getAttachProgress() {
        return entityData.get(DATA_ATTACH_PROGRESS);
    }

    public void setAttachProgress(int progress) {
        entityData.set(DATA_ATTACH_PROGRESS, progress);
    }

    public float getAttachProgressPercent() {
        return (float) getAttachProgress() / ATTACH_DURATION;
    }

    private void expireAndDie() {
        WildAside.LOGGER.info("Bacillus blob expired at age {} (lifespan: {})", getAge(), getLifespan());

        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.POOF, getX(), getY() + 0.3, getZ(), 15, 0.3, 0.2, 0.3, 0.02);
            serverLevel.sendParticles(ParticleTypes.ITEM_SLIME, getX(), getY() + 0.2, getZ(), 8, 0.2, 0.1, 0.2, 0.05);
        }

        level().playSound(null, getX(), getY(), getZ(), SoundEvents.SLIME_DEATH, SoundSource.NEUTRAL, 0.8f, 0.6f);

        this.discard();
    }

    @Override
    protected void dropExperience() {
        if (this.level() instanceof ServerLevel) {
            ExperienceOrb.award((ServerLevel) this.level(), this.position(), 5 + random.nextInt(5));
        }
    }

    @Override
    public void die(DamageSource source) {
        if (isAttached()) {
            enteringHost = false;
            detach();
        }
        super.die(source);
    }

    private void spawnAmbientParticles() {
        double x = getX() + (random.nextDouble() - 0.5) * 0.5;
        double y = getY() + 0.2 + random.nextDouble() * 0.3;
        double z = getZ() + (random.nextDouble() - 0.5) * 0.5;

        level().addParticle(ParticleTypes.ENTITY_EFFECT, x, y, z, 0.2, 0.8, 0.2);
    }

    private void spawnAttachedParticles() {
        LivingEntity attached = getAttachedEntity();
        if (attached == null) return;

        double x = getX() + (random.nextDouble() - 0.5) * 0.3;
        double y = getY() + random.nextDouble() * 0.2;
        double z = getZ() + (random.nextDouble() - 0.5) * 0.3;

        float progress = getAttachProgressPercent();
        if (progress > 0.7f) {
            level().addParticle(ParticleTypes.DAMAGE_INDICATOR, x, y, z, 0, 0, 0);
        }
        else if (progress > 0.4f) {
            level().addParticle(ParticleTypes.CRIT, x, y, z, 0, 0.02, 0);
        }
        else {
            level().addParticle(ParticleTypes.ITEM_SLIME, x, y, z, 0, 0.02, 0);
        }
    }

    private void spawnDyingParticles() {
        if (level() instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 3; i++) {
                double x = getX() + (random.nextDouble() - 0.5) * 0.8;
                double y = getY() + 0.2 + random.nextDouble() * 0.4;
                double z = getZ() + (random.nextDouble() - 0.5) * 0.8;

                serverLevel.sendParticles(ParticleTypes.SMOKE, x, y, z, 1, 0, 0.02, 0, 0.01);
            }
        }
    }

    public void setCarriedDna(DnaImplementation dna) {
        if (dna == null) {
            WildAside.LOGGER.warn("setCarriedDna called with null!");
            this.carriedDna = new DnaImplementation();
            entityData.set(DATA_HAS_DNA, false);
            return;
        }

        this.carriedDna = dna;

        boolean hasDna = hasValidDna(dna);
        entityData.set(DATA_HAS_DNA, hasDna);

        if (hasDna) {
            WildAside.LOGGER.info("setCarriedDna: DNA set successfully");
        }
        else {
            WildAside.LOGGER.warn("setCarriedDna:  DNA has no loci!");
        }
    }

    public DnaImplementation getCarriedDna() {
        return carriedDna;
    }

    public boolean hasDna() {
        return entityData.get(DATA_HAS_DNA);
    }

    public int getLifespan() {
        return entityData.get(DATA_LIFESPAN);
    }

    public void setLifespan(int lifespan) {
        entityData.set(DATA_LIFESPAN, Math.max(1, lifespan));
    }

    public int getAge() {
        return entityData.get(DATA_AGE);
    }

    public void setAge(int age) {
        entityData.set(DATA_AGE, age);
    }

    public int getRemainingLifespan() {
        return Math.max(0, getLifespan() - getAge());
    }

    public float getLifespanPercent() {
        return 1.0f - ((float) getAge() / getLifespan());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);

        tag.putInt(TAG_LIFESPAN, getLifespan());
        tag.putInt(TAG_AGE, getAge());
        tag.putInt(TAG_ATTACH_PROGRESS, getAttachProgress());

        LivingEntity attached = getAttachedEntity();
        if (attached != null) {
            tag.putUUID(TAG_ATTACHED_TO, attached.getUUID());
        }

        if (carriedDna != null && hasValidDna(carriedDna)) {
            tag.put(TAG_CARRIED_DNA, carriedDna.serializeNBT());
            WildAside.LOGGER.info("Saved blob DNA");
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        if (tag.contains(TAG_LIFESPAN)) {
            setLifespan(tag.getInt(TAG_LIFESPAN));
        }
        if (tag.contains(TAG_AGE)) {
            setAge(tag.getInt(TAG_AGE));
        }
        if (tag.contains(TAG_ATTACH_PROGRESS)) {
            setAttachProgress(tag.getInt(TAG_ATTACH_PROGRESS));
        }

        if (tag.contains(TAG_CARRIED_DNA)) {
            carriedDna = new DnaImplementation();
            carriedDna.deserializeNBT(tag.getCompound(TAG_CARRIED_DNA));
            entityData.set(DATA_HAS_DNA, hasValidDna(carriedDna));
            WildAside.LOGGER.info("Loaded blob DNA");
        }
    }

    @Override
    protected boolean shouldDropLoot() {
        return false;
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
    }

    @Override
    public boolean canBeLeashed(Player player) {
        return false;
    }

    @Override
    public boolean isPushable() {
        return !isAttached() && !enteringHost;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        if (enteringHost) return true;
        if (isAttached() && source.is(net.minecraft.tags.DamageTypeTags.IS_FALL)) {
            return true;
        }
        return super.isInvulnerableTo(source);
    }

    @Override
    protected float getSoundVolume() {
        return 0.4f;
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEFINED;
    }

    @Override
    public int getExperienceReward() {
        return 5 + random.nextInt(5);
    }

    @Override
    public Component getName() {
        if (hasDna()) {
            return Component.translatable("entity.wildaside.bacillus_blob.with_dna");
        }
        return super.getName();
    }

    private boolean hasValidDna(DnaImplementation dna) {
        return dna != null && dna.getGenome() != null && !dna.getGenome().isEmpty();
    }
}