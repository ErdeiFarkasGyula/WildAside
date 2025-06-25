package net.farkas.wildaside.util;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum HickoryColour implements StringRepresentable {
    HICKORY("hickory"),
    RED_GLOWING("red_glowing_hickory"),
    BROWN_GLOWING("brown_glowing_hickory"),
    YELLOW_GLOWING("yellow_glowing_hickory"),
    GREEN_GLOWING("green_glowing_hickory");

    public static final Codec<HickoryColour> CODEC = StringRepresentable.fromEnum(HickoryColour::values);
    private final String name;
    HickoryColour(String name) { this.name = name; }
    @Override public String getSerializedName() { return name; }
}

