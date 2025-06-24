package net.farkas.wildaside.block.custom.vibrion;

import net.farkas.wildaside.particle.ModParticles;
import net.farkas.wildaside.util.ContaminationHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SporeBlaster extends Block {
    public static final DirectionProperty FACING = DirectionalBlock.FACING;

    private boolean shouldBreakNext;

    public SporeBlaster(Properties props) {
        super(props);
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> b) {
        b.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, @Nullable Direction side) {
        return true;
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (!world.isClientSide) {
            world.scheduleTick(pos, this, 2);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource rand) {
        int power = world.getBestNeighborSignal(pos);
        if (power > 0) {
            infectAlongLine(world, pos, state.getValue(FACING), power, rand);
        }
        world.scheduleTick(pos, this, 2);
    }

    private void infectAlongLine(ServerLevel world, BlockPos origin, Direction dir, int range, RandomSource rand) {
        for (int i = 1; i <= range; i++) {
            if (shouldBreakNext) {
                shouldBreakNext = false;
                break;
            }

            BlockState originBlock = world.getBlockState(origin);
            BlockPos step = origin.relative(dir, i);
            BlockState nextBlock = world.getBlockState(step);

            if (world.getBlockState(step).isCollisionShapeFullBlock(world, step)) break;
            if (dir.getAxis() == Direction.Axis.Y) {
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
            if (dir.getAxis() == Direction.Axis.X) {
                if (nextBlock.getBlock() instanceof StairBlock) {
                    if (nextBlock.getValue(StairBlock.FACING).getAxis() == Direction.Axis.X) {
                        break;
                    }
                }
                if (nextBlock.getBlock() instanceof TrapDoorBlock) {
                    if (nextBlock.getValue(TrapDoorBlock.OPEN)) {
                        Direction facing = nextBlock.getValue(TrapDoorBlock.FACING);
                        if (facing.getAxis() == Direction.Axis.X) {
                            Direction.Axis axis = originBlock.getValue(SporeBlaster.FACING).getAxis();
                            if (axisToDirection(axis, dir.getStepX()) == facing) {
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

                    if (!open) {
                        if (facing.getAxis() == Direction.Axis.X) {
                            if (dir == facing) {
                                break;
                            } else {
                                shouldBreakNext = true;
                            }
                        }
                    } else {
                        if (facing.getAxis() != Direction.Axis.X) {
                            if (doorDirectionCheck(dir.getAxis(), dir.getStepX(), facing)) {
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
            if (dir.getAxis() == Direction.Axis.Z) {
                if (nextBlock.getBlock() instanceof StairBlock) {
                    if (nextBlock.getValue(StairBlock.FACING).getAxis() == Direction.Axis.Z) {
                        break;
                    }
                }
                if (nextBlock.getBlock() instanceof TrapDoorBlock) {
                    if (nextBlock.getValue(TrapDoorBlock.OPEN)) {
                        Direction facing = nextBlock.getValue(TrapDoorBlock.FACING);
                        if (facing.getAxis() == Direction.Axis.Z) {
                            if (dir == facing) {
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
                    Direction.Axis axis = originBlock.getValue(SporeBlaster.FACING).getAxis();

                    if (!open) {
                        if (facing.getAxis() == Direction.Axis.Z) {
                            if (dir == facing) {
                                break;
                            } else {
                                shouldBreakNext = true;
                            }
                        }
                    } else {
                        if (facing.getAxis() != Direction.Axis.Z) {
                            if (doorDirectionCheck(axis, dir.getStepZ(), facing)) {
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

            AABB area = new AABB(step);
            List<LivingEntity> hits = world.getEntitiesOfClass(LivingEntity.class, area,e -> !e.isSpectator());

            SimpleParticleType particle = ModParticles.VIBRION_PARTICLE.get();

            double x = step.getX() + rand.nextDouble();
            double y = step.getY() + rand.nextDouble();
            double z = step.getZ() + rand.nextDouble();
            world.sendParticles(particle, x, y, z,1,
                    0.02 * dir.getStepX(),
                    0.02 * dir.getStepY(),
                    0.02 * dir.getStepZ(),
                    0.0);

            for (LivingEntity entity : hits) {
                ContaminationHandler.giveContaminationDose(entity, 50);
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
