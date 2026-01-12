package net.farkas.wildaside.dna.sequence;

import net.minecraft.ChatFormatting;

public enum GeneSource {
    NATURAL(ChatFormatting.GREEN),
    BRED(ChatFormatting.AQUA),
    ENGINEERED(ChatFormatting.YELLOW),
    MUTATED(ChatFormatting.RED),
    INTEGRATED(ChatFormatting.LIGHT_PURPLE);

    private final ChatFormatting color;

    GeneSource(ChatFormatting color) {
        this.color = color;
    }

    public ChatFormatting getColor() {
        return color;
    }
}
