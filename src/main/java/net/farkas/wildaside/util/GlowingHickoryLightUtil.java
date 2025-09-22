package net.farkas.wildaside.util;

import org.joml.Math;

public class GlowingHickoryLightUtil {
    public static int getLight(int time, int minLight, int maxLight) {
        int newLight = 0;

        if (time > 22000) {
            newLight = Math.round(maxLight - (maxLight * ((time - 22000f) / 2000f)));
        } else
            if (time > 12000 && time < 14000) {
                newLight = Math.round(maxLight * ((time - 12000f) / 2000f));
            } else
                if (time > 14000) {
                    newLight = maxLight;
                } else
                    if (time < 12000) {
                        newLight = minLight;
                    }

        return Math.clamp(minLight, maxLight, newLight);
    }
}