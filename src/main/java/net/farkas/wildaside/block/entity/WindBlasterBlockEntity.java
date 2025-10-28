package net.farkas.wildaside.block.entity;

import net.farkas.wildaside.block.custom.vibrion.WindBlaster;
import net.farkas.wildaside.particle.ModParticles;
import net.farkas.wildaside.util.BlasterUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class WindBlasterBlockEntity extends BlasterBlockEntity {
    private static final int MAX_RANGE = 15;
    private static final double BASE_FORCE = 0.15D;

    private BlockPos previousPos = BlockPos.ZERO;
    private int blocksTraveled = 0;

    public WindBlasterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.WIND_BLASTER.get(), pos, state);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
    }

    public void tickServer(Level level, BlockPos origin, BlockState oldState) {
        if (!(level instanceof ServerLevel world)) return;

        Direction facing = getBlockState().getValue(WindBlaster.FACING);
        Direction back = facing.getOpposite();

        int rangeSignal = world.getSignal(worldPosition.relative(back), back);

        int strength = 1;
        for (Direction dir : Direction.values()) {
            if (dir == back) continue;
            strength = Math.max(strength, world.getSignal(worldPosition.relative(dir), dir));
        }

        if (rangeSignal <= 0) return;

        int range = Mth.clamp(rangeSignal, 1, MAX_RANGE);
        double force = BASE_FORCE * (strength / 15.0);

        RandomSource rand = world.random;
        Vec3 start = Vec3.atCenterOf(worldPosition);

        double step = 0.1D;
        double particleInterval = 0.7D;
        double nextParticle = particleInterval;

        for (double traveled = 0.8D; traveled <= range; traveled += step) {
            Vec3 sample = start.add(facing.getStepX() * traveled, facing.getStepY() * traveled, facing.getStepZ() * traveled);
            BlockPos samplePos = BlockPos.containing(sample);
            BlockState state = world.getBlockState(samplePos);

            if (previousPos != samplePos) {
                previousPos = samplePos;
                if (shouldBreakNext) {
                    shouldBreakNext = false;
                    break;
                }
            }

            if (state.isCollisionShapeFullBlock(world, samplePos)) break;
            if (!BlasterUtils.canTraverse(facing, state, oldState, this)) {
                blocksTraveled = Math.abs(origin.getX() - samplePos.getX()) + Math.abs(origin.getY() - samplePos.getY()) + Math.abs(origin.getZ() - samplePos.getZ());

                if (strength - blocksTraveled >= 7) {
                    BlockState openableBlockState = level.getBlockState(samplePos);

                    if (openableBlockState.hasProperty(BlockStateProperties.OPEN)) {
//                        SoundEvent close = null;
//                        SoundEvent open = null;
//
//                        boolean isOpen = openableBlockState.getValue(BlockS)
//
//                        if (openableBlockState.getBlock() instanceof DoorBlock doorBlock) {
//                            close = doorBlock.type().doorClose();
//                            open = doorBlock.type().doorOpen();
//                        }
//
//                        level.playSound(null, samplePos, isOpening ?  : this.type.doorClose(), SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.1F + 0.9F);
                        level.setBlock(samplePos, openableBlockState.setValue(DoorBlock.OPEN, !(openableBlockState.getValue(DoorBlock.OPEN))), 3);
                        shouldBreakNext = false;
                    }
                } else {
                    break;
                }
            }

            if (traveled >= nextParticle) {
                nextParticle += particleInterval;

                float density = (float) ((0.2 + strength / 15.0) * Math.exp(-0.3 * traveled));
                if (rand.nextFloat() < density) {
                    double radius = 0.25 + 0.1 * (strength / 15.0);
                    double offsetX = (rand.nextDouble() - 0.5) * 2 * radius;
                    double offsetY = (rand.nextDouble() - 0.5) * 2 * radius;
                    double offsetZ = (rand.nextDouble() - 0.5) * 2 * radius;

                    if (facing.getAxis() == Direction.Axis.X) offsetX = 0;
                    else if (facing.getAxis() == Direction.Axis.Y) offsetY = 0;
                    else offsetZ = 0;

                    Vec3 particlePos = sample.add(offsetX, offsetY, offsetZ);

                    double speed = 0.05 + 0.15 * (force / BASE_FORCE);
                    if (rand.nextFloat() < 0.8f) {
                        world.sendParticles(ModParticles.WIND_PARTICLE.get(),
                                particlePos.x, particlePos.y, particlePos.z, 1,
                                facing.getStepX() * speed, facing.getStepY() * speed, facing.getStepZ() * speed, 0.0);
                    }
                    if (rand.nextFloat() < 0.2f) {
                        world.sendParticles(ParticleTypes.POOF,
                                particlePos.x, particlePos.y, particlePos.z, 1,
                                facing.getStepX() * 0.02, facing.getStepY() * 0.02, facing.getStepZ() * 0.02, 0.0);
                    }
                }
            }

            double falloff = Math.exp(-0.25 * traveled);
            double appliedForce = force * falloff;

            AABB box = new AABB(samplePos).inflate(-0.05);
            List<Entity> entities = world.getEntities((Entity) null, box, e -> true);
            for (Entity e : entities) {
                if (e instanceof ArmorStand) continue;
                if (isHeavyEntity(e)) continue;

                Vec3 velocity = e.getDeltaMovement();

                if (velocity.y < -0.08 && facing.getStepY() > 0) {
                    double fallSpeed = Math.abs(velocity.y);
                    double lift = Math.min(fallSpeed * 0.25 * (strength / 15.0), 0.12);
                    velocity = velocity.add(0.0, lift, 0.0);
                }

                Vec3 push = new Vec3(facing.getStepX(), facing.getStepY(), facing.getStepZ());
                velocity = velocity.add(push.scale(appliedForce));

                double maxSpeed = 1.5;
                double hor = Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z);
                if (hor > maxSpeed) {
                    double scale = maxSpeed / hor;
                    velocity = new Vec3(velocity.x * scale, velocity.y, velocity.z * scale);
                }

                e.setDeltaMovement(velocity);
                e.hurtMarked = true;
            }
        }
    }

    private boolean isHeavyEntity(Entity e) {
        return (e instanceof IronGolem) || (e instanceof Ravager) || (e instanceof WitherBoss) || (e instanceof EnderDragon);
    }
}