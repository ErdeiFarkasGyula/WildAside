package net.farkas.wildaside.util;

import net.farkas.wildaside.block.entity.BlasterBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;

public class BlasterUtils {
    public static boolean canTraverse(Direction dir, BlockState nextBlockState, BlockState originBlockState, BlasterBlockEntity blockEntity) {
        Direction.Axis axis = dir.getAxis();
        Block nextBlock = nextBlockState.getBlock();

        if (axis == Direction.Axis.Y) {
            if (nextBlock instanceof SlabBlock) {
                return false;
            }
            else if (nextBlock instanceof TrapDoorBlock) {
                if (!nextBlockState.getValue(TrapDoorBlock.OPEN)) {
                    return false;
                }
            }
            else if (nextBlock instanceof StairBlock) {
                return false;
            }
        }
        else
        if (axis == Direction.Axis.X) {
            if (nextBlock instanceof StairBlock) {
                if (nextBlockState.getValue(StairBlock.FACING).getAxis() == Direction.Axis.X) {
                    return false;
                }
            } else if (nextBlock instanceof TrapDoorBlock) {
                if (nextBlockState.getValue(TrapDoorBlock.OPEN)) {
                    Direction facing = nextBlockState.getValue(TrapDoorBlock.FACING);
                    if (facing.getAxis() == Direction.Axis.X) {
                        if (axisToDirection(axis, dir.getStepX()) == facing) {
                            return false;
                        } else {
                            blockEntity.shouldBreakNext = true;
                        }
                    }
                }
            }
            else
            if (nextBlock instanceof DoorBlock) {
                var open = nextBlockState.getValue(DoorBlock.OPEN);
                Direction facing = nextBlockState.getValue(DoorBlock.FACING);

                if (!open) {
                    if (facing.getAxis() == Direction.Axis.X) {
                        if (dir == facing) {
                            return false;
                        } else {
                            blockEntity.shouldBreakNext = true;
                        }
                    }
                }
                else
                if (facing.getAxis() != Direction.Axis.X) {
                    if (BlasterUtils.doorDirectionCheck(dir.getAxis(), dir.getStepX(), facing)) {
                        if (nextBlockState.getValue(DoorBlock.HINGE) == DoorHingeSide.LEFT) {
                            return false;
                        }
                        blockEntity.shouldBreakNext = true;
                    } else {
                        if (nextBlockState.getValue(DoorBlock.HINGE) == DoorHingeSide.LEFT) {
                            blockEntity.shouldBreakNext = true;
                        } else {
                               return false;
                        }
                    }
                }
            }

        }
        else
        if (axis == Direction.Axis.Z) {
            if (nextBlock instanceof StairBlock) {
                if (nextBlockState.getValue(StairBlock.FACING).getAxis() == Direction.Axis.Z) {
                    return false;
                }
            }
            else
            if (nextBlock instanceof TrapDoorBlock) {
                if (nextBlockState.getValue(TrapDoorBlock.OPEN)) {
                    Direction facing = nextBlockState.getValue(TrapDoorBlock.FACING);
                    if (facing.getAxis() == Direction.Axis.Z) {
                        if (dir == facing) {
                            return false;
                        } else {
                            blockEntity.shouldBreakNext = true;
                        }
                    }
                }
            }
            else
            if (nextBlock instanceof DoorBlock) {
                var open = nextBlockState.getValue(DoorBlock.OPEN);
                Direction facing = nextBlockState.getValue(DoorBlock.FACING);

                if (!open) {
                    if (facing.getAxis() == Direction.Axis.Z) {
                        if (dir == facing) {
                            return false;
                        } else {
                            blockEntity.shouldBreakNext = true;
                        }
                    }
                } else {
                    if (facing.getAxis() != Direction.Axis.Z) {
                        if (BlasterUtils.doorDirectionCheck(axis, dir.getStepZ(), facing)) {
                            if (nextBlockState.getValue(DoorBlock.HINGE) == DoorHingeSide.RIGHT) {
                                return false;
                            }
                            blockEntity.shouldBreakNext = true;
                        } else {
                            if (nextBlockState.getValue(DoorBlock.HINGE) == DoorHingeSide.RIGHT) {
                                blockEntity.shouldBreakNext = true;
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

    public static Direction axisToDirection(Direction.Axis axis, int offset) {
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

    public static Boolean doorDirectionCheck(Direction.Axis axis, int offset, Direction facing) {
        if (axis.equals(Direction.Axis.X)) {
            return axisToDirection(Direction.Axis.Z, -offset) == facing;
        }
        if (axis.equals(Direction.Axis.Z)) {
            return axisToDirection(Direction.Axis.X, -offset) == facing;
        }
        return false;
    }
}
