package net.farkas.wildaside.util;

public class ColourUtils {
    public static int blendColors(int colorStart, int colorEnd, float progress) {
        int alphaStart = (colorStart >> 24) & 0xFF;
        int redStart = (colorStart >> 16) & 0xFF;
        int greenStart = (colorStart >> 8) & 0xFF;
        int blueStart = colorStart & 0xFF;

        int alphaEnd = (colorEnd >> 24) & 0xFF;
        int redEnd = (colorEnd >> 16) & 0xFF;
        int greenEnd = (colorEnd >> 8) & 0xFF;
        int blueEnd = colorEnd & 0xFF;

        int alpha = (int) (alphaStart + (alphaEnd - alphaStart) * progress);
        int red = (int) (redStart + (redEnd - redStart) * progress);
        int green = (int) (greenStart + (greenEnd - greenStart) * progress);
        int blue = (int) (blueStart + (blueEnd - blueStart) * progress);

        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }
}
