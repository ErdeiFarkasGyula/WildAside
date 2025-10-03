package net.farkas.wildaside.block.entity;

import net.farkas.wildaside.block.custom.vibrion.SporeBlaster;
import net.farkas.wildaside.particle.ModParticles;
import net.farkas.wildaside.util.BlasterUtil;
import net.farkas.wildaside.util.ContaminationHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class SporeBlasterBlockEntity extends BlockEntity {
    private int power = 0;
    private boolean shouldBreakNext = false;

    public SporeBlasterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SPORE_BLASTER.get(), pos, state);
    }

    public void tickServer() {
        Level level = getLevel();
        int power = level.getBestNeighborSignal(worldPosition);
        if (power > 0) {
            infectAlongLine((ServerLevel) level, worldPosition, getBlockState().getValue(SporeBlaster.FACING), power, level.random);
        }
        level.scheduleTick(worldPosition, getBlockState().getBlock(), 2);
    }

    private void infectAlongLine(ServerLevel world, BlockPos origin, Direction dir, int range, RandomSource rand) {
        SimpleParticleType particle = ModParticles.VIBRION_PARTICLE.get();
        for (int i = 1; i <= range; i++) {
            if (shouldBreakNext) {
                shouldBreakNext = false;
                break;
            }

            BlockState originBlock = world.getBlockState(origin);
            BlockPos step = origin.relative(dir, i);
            BlockState nextBlock = world.getBlockState(step);

            if (world.getBlockState(step).isCollisionShapeFullBlock(world, step)) break;
            if (!canTraverse(dir, nextBlock, originBlock)) break;


            double x = step.getX() + rand.nextDouble();
            double y = step.getY() + rand.nextDouble();
            double z = step.getZ() + rand.nextDouble();
            world.sendParticles(particle, x, y, z,1,
                    0.02 * dir.getStepX(),
                    0.02 * dir.getStepY(),
                    0.02 * dir.getStepZ(),
                    0.0);

            List<LivingEntity> hits = world.getEntitiesOfClass(LivingEntity.class, new AABB(step), e -> !e.isSpectator());
            for (LivingEntity entity : hits) {
                ContaminationHandler.addDose(entity, 50);
                world.sendParticles(particle, entity.getX(), entity.getY() + 0.5, entity.getZ(), 5, 0.2, 0.2, 0.2, 0.01);
            }
        }
    }

    private boolean canTraverse(Direction dir, BlockState nextBlock, BlockState originBlock) {
        if (dir.getAxis() == Direction.Axis.Y) {
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
        if (dir.getAxis() == Direction.Axis.X) {
            if (nextBlock.getBlock() instanceof StairBlock) {
                if (nextBlock.getValue(StairBlock.FACING).getAxis() == Direction.Axis.X) {
                    return false;
                }
            }
            if (nextBlock.getBlock() instanceof TrapDoorBlock) {
                if (nextBlock.getValue(TrapDoorBlock.OPEN)) {
                    Direction facing = nextBlock.getValue(TrapDoorBlock.FACING);
                    if (facing.getAxis() == Direction.Axis.X) {
                        Direction.Axis axis = originBlock.getValue(SporeBlaster.FACING).getAxis();
                        if (BlasterUtil.axisToDirection(axis, dir.getStepX()) == facing) {
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

                if (!open) {
                    if (facing.getAxis() == Direction.Axis.X) {
                        if (dir == facing) {
                            return false;
                        } else {
                            shouldBreakNext = true;
                        }
                    }
                } else {
                    if (facing.getAxis() != Direction.Axis.X) {
                        if (BlasterUtil.doorDirectionCheck(dir.getAxis(), dir.getStepX(), facing)) {
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
        if (dir.getAxis() == Direction.Axis.Z) {
            if (nextBlock.getBlock() instanceof StairBlock) {
                if (nextBlock.getValue(StairBlock.FACING).getAxis() == Direction.Axis.Z) {
                    return false;
                }
            }
            if (nextBlock.getBlock() instanceof TrapDoorBlock) {
                if (nextBlock.getValue(TrapDoorBlock.OPEN)) {
                    Direction facing = nextBlock.getValue(TrapDoorBlock.FACING);
                    if (facing.getAxis() == Direction.Axis.Z) {
                        if (dir == facing) {
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
                Direction.Axis axis = originBlock.getValue(SporeBlaster.FACING).getAxis();

                if (!open) {
                    if (facing.getAxis() == Direction.Axis.Z) {
                        if (dir == facing) {
                            return false;
                        } else {
                            shouldBreakNext = true;
                        }
                    }
                } else {
                    if (facing.getAxis() != Direction.Axis.Z) {
                        if (BlasterUtil.doorDirectionCheck(axis, dir.getStepZ(), facing)) {
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
    public void load(CompoundTag tag) {
        super.load(tag);
        power = tag.getInt("power");
        shouldBreakNext = tag.getBoolean("shouldBreakNext");
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("power", power);
        tag.putBoolean("shouldBreakNext", shouldBreakNext);
    }
}
