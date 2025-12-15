package net.farkas.wildaside.util;

public class Vec2 {
    public final float x;
    public final float y;

    public Vec2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vec2 add(Vec2 o) {
        return new Vec2(x + o.x, y + o.y);
    }

    public Vec2 sub(Vec2 o) {
        return new Vec2(x - o.x, y - o.y);
    }

    public Vec2 scale(float s) {
        return new Vec2(x * s, y * s);
    }

    public float length() {
        return (float) Math.sqrt(x * x + y * y);
    }

    public Vec2 normalize() {
        float len = length();
        return len == 0 ? new Vec2(0, 0) : new Vec2(x / len, y / len);
    }

    public Vec2 perpendicular() {
        return new Vec2(-y, x);
    }
}
