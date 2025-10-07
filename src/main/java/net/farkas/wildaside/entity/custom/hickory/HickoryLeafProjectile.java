package net.farkas.wildaside.entity.custom.hickory;

import net.farkas.wildaside.entity.ModEntities;
import net.farkas.wildaside.item.ModItems;
import net.farkas.wildaside.util.HickoryColour;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class HickoryLeafProjectile extends ThrowableItemProjectile implements ItemSupplier {
    private static final EntityDataAccessor<Integer> COLOUR = SynchedEntityData.defineId(HickoryLeafProjectile.class, EntityDataSerializers.INT);
    public int phase = 0;

    public HickoryLeafProjectile(EntityType<? extends ThrowableItemProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public HickoryLeafProjectile(Level world, LivingEntity shooter, HickoryColour colour) {
        super(ModEntities.HICKORY_LEAF_PROJECTILE.get(), shooter, world);
        this.entityData.set(COLOUR, colour.ordinal());
        if (shooter instanceof HickoryTreantEntity hickoryTreant) {
            this.phase = hickoryTreant.getPhase();
        }
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.HICKORY_LEAF.get();
    }

    @Override
    public ItemStack getItem() {
        return switch (getColour()) {
            case RED_GLOWING -> new ItemStack(ModItems.RED_GLOWING_HICKORY_LEAF.get());
            case BROWN_GLOWING  -> new ItemStack(ModItems.BROWN_GLOWING_HICKORY_LEAF.get());
            case YELLOW_GLOWING -> new ItemStack(ModItems.YELLOW_GLOWING_HICKORY_LEAF.get());
            case GREEN_GLOWING  -> new ItemStack(ModItems.GREEN_GLOWING_HICKORY_LEAF.get());
            default -> new ItemStack(ModItems.HICKORY_LEAF.get());
        };
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (result.getEntity() instanceof LivingEntity target) {
            target.addEffect(new MobEffectInstance(MobEffects.HARM, 1, 0, false, false));
            switch (getColour()) {
                case RED_GLOWING:
                    target.setSecondsOnFire(4 + phase);
                    break;
                case BROWN_GLOWING:
                    target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, phase - 1));
                    break;
                case YELLOW_GLOWING:
                    target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100, 0));
                    target.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 100, 0));
                    break;
                case GREEN_GLOWING:
                    this.healEffect();
                    break;
                default:

            }
        }
        this.discard();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
//        for (int i = 0; i < 8; i++) {
//            this.level().addParticle(ModParticles.HICKORY_PARTICLES.get(colour).get(),
//                    this.getX(), this.getY(), this.getZ(),
//                    (this.random.nextDouble() - 0.5) * 0.2,
//                    (this.random.nextDouble() - 0.5) * 0.2,
//                    (this.random.nextDouble() - 0.5) * 0.2);
//        }
        this.discard();
    }

    private void healEffect() {
        if (this.getOwner() instanceof LivingEntity shooter) {
            shooter.heal(phase);
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(COLOUR, HickoryColour.HICKORY.ordinal());
    }

    private HickoryColour getColour() {
        int id = this.entityData.get(COLOUR);
        return HickoryColour.values()[Mth.clamp(id, 0, HickoryColour.values().length - 1)];
    }
}
