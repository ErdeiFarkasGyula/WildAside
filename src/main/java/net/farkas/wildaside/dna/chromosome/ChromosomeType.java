package net.farkas.wildaside.dna.chromosome;

import net.minecraft.ChatFormatting;

public enum ChromosomeType {
    CORE(ChatFormatting.YELLOW),
    RESISTANCE(ChatFormatting.AQUA),
    BEHAVIOR(ChatFormatting.LIGHT_PURPLE),
    APPEARANCE(ChatFormatting.GREEN),
    SPECIAL(ChatFormatting.GOLD);

    private final ChatFormatting color;

    ChromosomeType(ChatFormatting color) {
        this.color = color;
    }

    public ChatFormatting getColor() {
        return color;
    }
}
