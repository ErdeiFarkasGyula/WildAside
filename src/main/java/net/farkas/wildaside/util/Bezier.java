package net.farkas.wildaside.util;

public class Bezier {
    public static float quad(float a, float b, float c, float t) {
        float inv = 1f - t;
        return inv * inv * a + 2 * inv * t * b + t * t * c;
    }

    public static Vec2 quad(Vec2 a, Vec2 b, Vec2 c, float t) {
        return new Vec2(quad(a.x, b.x, c.x, t), quad(a.y, b.y, c.y, t));
    }
}