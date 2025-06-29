package net.farkas.wildaside.block.entity;

import net.farkas.wildaside.particle.ModParticles;
import net.farkas.wildaside.util.BlasterUtils;
import net.farkas.wildaside.util.ContaminationHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.phys.AABB;
import net.minecraft.nbt.CompoundTag;
import java.util.List;
import net.minecraft.core.Direction;

public class NaturalSporeBlasterBlockEntity extends BlockEntity {
    private static final int MAX_TIMER = 40;
    private int changePowerTimer = MAX_TIMER;
    private int power1 = 0;
    private int power2 = 0;
    private boolean shouldBreakNext = false;

    public NaturalSporeBlasterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.NATURAL_SPORE_BLASTER.get(), pos, state);
    }

    public void tickServer() {
        Level level = getLevel();
        if (!(level instanceof ServerLevel world)) return;
        RandomSource rand = world.getRandom();

        if (++changePowerTimer >= MAX_TIMER) {
            changePowerTimer = 0;
            power1 = rand.nextFloat() > 0.25 ? rand.nextIntBetweenInclusive(0, 15) : 0;
            power2 = rand.nextFloat() > 0.25 ? rand.nextIntBetweenInclusive(0, 15) : 0;
        }

        Direction.Axis axis = getBlockState().getValue(RotatedPillarBlock.AXIS);
        int dx = axis == Direction.Axis.X ? 1 : 0;
        int dy = axis == Direction.Axis.Y ? 1 : 0;
        int dz = axis == Direction.Axis.Z ? 1 : 0;

        infectAlongLine(world, worldPosition, rand, power1, dx, dy, dz);
        infectAlongLine(world, worldPosition, rand, power2, -dx, -dy, -dz);

        world.scheduleTick(worldPosition, getBlockState().getBlock(), 2);
    }

    private void infectAlongLine(ServerLevel world, BlockPos origin, RandomSource random, int power, int x, int y, int z) {
        SimpleParticleType particle = ModParticles.VIBRION_PARTICLE.get();
        for (int i = 1; i <= power; i++) {
            if (shouldBreakNext) {
                shouldBreakNext = false;
                break;
            }

            BlockPos pos = origin.offset(x * i, y * i, z * i);
            BlockState next = world.getBlockState(pos);

            if (next.isCollisionShapeFullBlock(world, pos)) break;
            if (!canTraverse(x, y, z, world.getBlockState(origin), next)) break;

            world.sendParticles(particle,
                    pos.getX() + random.nextDouble(),
                    pos.getY() + random.nextDouble(),
                    pos.getZ() + random.nextDouble(),
                    1, x * 0.02, y * 0.02, z * 0.02, 0.0);

            List<LivingEntity> hits = world.getEntitiesOfClass(LivingEntity.class, new AABB(pos), e -> !e.isSpectator());
            for (LivingEntity e : hits) {
                ContaminationHandler.giveContaminationDose(e, 60);
                world.sendParticles(particle, e.getX(), e.getY() + 0.5, e.getZ(), 5, 0.2, 0.2, 0.2, 0.01);
            }
        }
    }

    private boolean canTraverse(int x, int y, int z, BlockState originBlock, BlockState nextBlock) {
        if (y != 0) {
            if (nextBlock.getBlock() instanceof SlabBlock) {
                return false;
            }
            if (nextBlock.getBlock() instanceof TrapDoorBlock) {
                if (!nextBlock.getValue(TrapDoorBlock.OPEN)) {
                    return false;
                }
            }
            if (nextBlock.getBlock() instanceof StairBlock) {
                return false;
            }
        }
        if (x != 0) {
            if (nextBlock.getBlock() instanceof StairBlock) {
                if (nextBlock.getValue(StairBlock.FACING).getAxis() == Direction.Axis.X) {
                    return false;
                }
            }
            if (nextBlock.getBlock() instanceof TrapDoorBlock) {
                if (nextBlock.getValue(TrapDoorBlock.OPEN)) {
                    Direction facing = nextBlock.getValue(TrapDoorBlock.FACING);
                    if (facing.getAxis() == Direction.Axis.X) {
                        Direction.Axis axis = originBlock.getValue(RotatedPillarBlock.AXIS);
                        if (BlasterUtils.axisToDirection(axis, x) == facing) {
                            return false;
                        } else {
                            shouldBreakNext = true;
                        }
                    }
                }
            }
            if (nextBlock.getBlock() instanceof DoorBlock) {
                var open = nextBlock.getValue(DoorBlock.OPEN);
                Direction facing = nextBlock.getValue(DoorBlock.FACING);
                Direction.Axis axis = originBlock.getValue(RotatedPillarBlock.AXIS);

                if (!open) {
                    if (facing.getAxis() == Direction.Axis.X) {
                        if (BlasterUtils.axisToDirection(axis, x) == facing) {
                            return false;
                        } else {
                            shouldBreakNext = true;
                        }
                    }
                } else {
                    if (facing.getAxis() != Direction.Axis.X) {
                        if (BlasterUtils.doorDirectionCheck(axis, x, facing)) {
                            if (nextBlock.getValue(DoorBlock.HINGE) == DoorHingeSide.LEFT) {
                                return false;
                            }
                            shouldBreakNext = true;
                        } else {
                            if (nextBlock.getValue(DoorBlock.HINGE) == DoorHingeSide.LEFT) {
                                shouldBreakNext = true;
                            } else {
                                return false;
                            }
                        }
                    }
                }
            }

        }
        if (z != 0) {
            if (nextBlock.getBlock() instanceof StairBlock) {
                if (nextBlock.getValue(StairBlock.FACING).getAxis() == Direction.Axis.Z) {
                    return false;
                }
            }
            if (nextBlock.getBlock() instanceof TrapDoorBlock) {
                if (nextBlock.getValue(TrapDoorBlock.OPEN)) {
                    Direction facing = nextBlock.getValue(TrapDoorBlock.FACING);
                    if (facing.getAxis() == Direction.Axis.Z) {
                        Direction.Axis axis = originBlock.getValue(RotatedPillarBlock.AXIS);
                        if (BlasterUtils.axisToDirection(axis, z) == facing) {
                            return false;
                        } else {
                            shouldBreakNext = true;
                        }
                    }
                }
            }
            if (nextBlock.getBlock() instanceof DoorBlock) {
                var open = nextBlock.getValue(DoorBlock.OPEN);
                Direction facing = nextBlock.getValue(DoorBlock.FACING);
                Direction.Axis axis = originBlock.getValue(RotatedPillarBlock.AXIS);

                if (!open) {
                    if (facing.getAxis() == Direction.Axis.Z) {
                        if (BlasterUtils.axisToDirection(axis, z) == facing) {
                            return false;
                        } else {
                            shouldBreakNext = true;
                        }
                    }
                } else {
                    if (facing.getAxis() != Direction.Axis.Z) {
                        if (BlasterUtils.doorDirectionCheck(axis, z, facing)) {
                            if (nextBlock.getValue(DoorBlock.HINGE) == DoorHingeSide.RIGHT) {
                                return false;
                            }
                            shouldBreakNext = true;
                        } else {
                            if (nextBlock.getValue(DoorBlock.HINGE) == DoorHingeSide.RIGHT) {
                                shouldBreakNext = true;
                            } else {
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        changePowerTimer = pTag.getInt("changeTimer");
        power1 = pTag.getInt("power1");
        power2 = pTag.getInt("power2");
        shouldBreakNext = pTag.getBoolean("shouldBreakNext");
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);
        pTag.putInt("changeTimer", changePowerTimer);
        pTag.putInt("power1", power1);
        pTag.putInt("power2", power2);
        pTag.putBoolean("shouldBreakNext", shouldBreakNext);
    }
}
