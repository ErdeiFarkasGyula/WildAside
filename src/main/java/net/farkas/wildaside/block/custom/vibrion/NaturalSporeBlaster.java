package net.farkas.wildaside.block.custom.vibrion;

import net.farkas.wildaside.block.ModBlocks;
import net.farkas.wildaside.particle.ModParticles;
import net.farkas.wildaside.util.ContaminationHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class NaturalSporeBlaster extends RotatedPillarBlock {
    private final int maxTimer = 40;
    private int changePowerTimer = maxTimer;
    private int power1 = 0;
    private int power2 = 0;

    private boolean shouldBreakNext;

    public NaturalSporeBlaster(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction) {
        return true;
    }

    @Override
    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pNeighborBlock, BlockPos pNeighborPos, boolean pMovedByPiston) {
        if (!pLevel.isClientSide) {
            pLevel.scheduleTick(pPos, this, 2);
        }
        super.neighborChanged(pState, pLevel, pPos, pNeighborBlock, pNeighborPos, pMovedByPiston);
    }

    @Override
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pMovedByPiston) {
        if (!pLevel.isClientSide) {
            pLevel.scheduleTick(pPos, this, 2);
        }
        super.onPlace(pState, pLevel, pPos, pOldState, pMovedByPiston);
    }

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        changePowerTimer++;
        if (changePowerTimer >= maxTimer) {
            changePowerTimer = 0;
            power1 = pRandom.nextBoolean() ? pRandom.nextIntBetweenInclusive(0, 15) : 0;
            power2 = pRandom.nextBoolean() ? pRandom.nextIntBetweenInclusive(0, 15) : 0;
        }

        int x = pLevel.getBlockState(pPos).getValue(AXIS).equals(Direction.Axis.X) ? 1 : 0;
        int y = pLevel.getBlockState(pPos).getValue(AXIS).equals(Direction.Axis.Y) ? 1 : 0;
        int z = pLevel.getBlockState(pPos).getValue(AXIS).equals(Direction.Axis.Z) ? 1 : 0;

        infectAlongLine(pLevel, pPos, pRandom, power1, x, y, z);
        infectAlongLine(pLevel, pPos, pRandom, power2, -x, -y, -z);
        pLevel.scheduleTick(pPos, this, 2);
        super.tick(pState, pLevel, pPos, pRandom);
    }

    private void infectAlongLine(ServerLevel world, BlockPos origin, RandomSource random, int power, int x, int y, int z) {
        for (int i = 1; i <= power; i++) {
            if (shouldBreakNext) {
                shouldBreakNext = false;
                break;
            }

            var originBlock = world.getBlockState(origin);
            var position = origin.offset(x * i, y * i, z * i);
            var nextBlock = world.getBlockState(position);

            if (nextBlock.isCollisionShapeFullBlock(world, position)) break;
            if (y != 0) {
                if (nextBlock.getBlock() instanceof SlabBlock) {
                    break;
                }
                if (nextBlock.getBlock() instanceof TrapDoorBlock) {
                    if (!nextBlock.getValue(TrapDoorBlock.OPEN)) {
                        break;
                    }
                }
                if (nextBlock.getBlock() instanceof StairBlock) {
                    break;
                }
            }
            if (x != 0) {
                if (nextBlock.getBlock() instanceof StairBlock) {
                    if (nextBlock.getValue(StairBlock.FACING).getAxis() == Direction.Axis.X) {
                        break;
                    }
                }
                if (nextBlock.getBlock() instanceof TrapDoorBlock) {
                    if (nextBlock.getValue(TrapDoorBlock.OPEN)) {
                        Direction facing = nextBlock.getValue(TrapDoorBlock.FACING);
                        if (facing.getAxis() == Direction.Axis.X) {
                            Direction.Axis axis = originBlock.getValue(RotatedPillarBlock.AXIS);
                            if (axisToDirection(axis, x) == facing) {
                                break;
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
                            if (axisToDirection(axis, x) == facing) {
                                break;
                            } else {
                                shouldBreakNext = true;
                            }
                        }
                    } else {
                        if (facing.getAxis() != Direction.Axis.X) {
                            if (doorDirectionCheck(axis, x, facing)) {
                                if (nextBlock.getValue(DoorBlock.HINGE) == DoorHingeSide.LEFT) {
                                    break;
                                }
                                shouldBreakNext = true;
                            } else {
                                if (nextBlock.getValue(DoorBlock.HINGE) == DoorHingeSide.LEFT) {
                                    shouldBreakNext = true;
                                } else {
                                    break;
                                }
                            }
                        }
                    }
                }

            }
            if (z != 0) {
                if (nextBlock.getBlock() instanceof StairBlock) {
                    if (nextBlock.getValue(StairBlock.FACING).getAxis() == Direction.Axis.Z) {
                        break;
                    }
                }
                if (nextBlock.getBlock() instanceof TrapDoorBlock) {
                    if (nextBlock.getValue(TrapDoorBlock.OPEN)) {
                        Direction facing = nextBlock.getValue(TrapDoorBlock.FACING);
                        if (facing.getAxis() == Direction.Axis.Z) {
                            Direction.Axis axis = originBlock.getValue(RotatedPillarBlock.AXIS);
                            if (axisToDirection(axis, z) == facing) {
                                break;
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
                            if (axisToDirection(axis, z) == facing) {
                                break;
                            } else {
                                shouldBreakNext = true;
                            }
                        }
                    } else {
                        if (facing.getAxis() != Direction.Axis.Z) {
                            if (doorDirectionCheck(axis, z, facing)) {
                                if (nextBlock.getValue(DoorBlock.HINGE) == DoorHingeSide.RIGHT) {
                                    break;
                                }
                                shouldBreakNext = true;
                            } else {
                                if (nextBlock.getValue(DoorBlock.HINGE) == DoorHingeSide.RIGHT) {
                                    shouldBreakNext = true;
                                } else {
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            AABB area = new AABB(position);
            List<LivingEntity> hits = world.getEntitiesOfClass(LivingEntity.class, area, e -> !e.isSpectator());

            SimpleParticleType particle = ModParticles.VIBRION_PARTICLE.get();

            double particleX = position.getX() + random.nextDouble();
            double particleY = position.getY() + random.nextDouble();
            double particleZ = position.getZ() + random.nextDouble();
            world.sendParticles(particle, particleX, particleY, particleZ,1,
                    0.02 * x,
                    0.02 * y,
                    0.02 * z,
                    0.0);

            for (LivingEntity entity : hits) {
                ContaminationHandler.giveContaminationDose(entity, 40);
                world.sendParticles(particle,
                        entity.getX(), entity.getY() + 0.5, entity.getZ(),
                        5, 0.2, 0.2, 0.2, 0.01);
            }
        }
    }

    private Direction axisToDirection(Direction.Axis axis, int offset) {
        if (axis.equals(Direction.Axis.X)) {
            if (offset == 1) return Direction.EAST;
            else return Direction.WEST;
        }
        if (axis.equals(Direction.Axis.Y)) {
            if (offset == 1) return Direction.UP;
            else return Direction.DOWN;
        }
        if (axis.equals(Direction.Axis.Z)) {
            if (offset == 1) return Direction.SOUTH;
            else return Direction.NORTH;
        }

        return Direction.UP;
    }

    private Boolean doorDirectionCheck(Direction.Axis axis, int offset, Direction facing) {
        if (axis.equals(Direction.Axis.X)) {
            return axisToDirection(Direction.Axis.Z, -offset) == facing;
        }
        if (axis.equals(Direction.Axis.Z)) {
            return axisToDirection(Direction.Axis.X, -offset) == facing;
        }
        return false;
    }
}
