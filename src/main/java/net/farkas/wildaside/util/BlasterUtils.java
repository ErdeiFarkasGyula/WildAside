package net.farkas.wildaside.util;

import net.minecraft.core.Direction;

public class BlasterUtils {
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
