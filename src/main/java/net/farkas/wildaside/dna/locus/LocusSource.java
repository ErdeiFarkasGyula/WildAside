package net.farkas.wildaside.dna.locus;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public enum LocusSource {
    NATIVE(ChatFormatting.GREEN, "locus.wildaside.source.native"),
    INTEGRATED(ChatFormatting.AQUA, "locus.wildaside.source.integrated"),
    TRANSIENT(ChatFormatting.YELLOW, "locus.wildaside.source.transient"),
    REJECTED(ChatFormatting.RED, "locus.wildaside.source.rejected");

    private final ChatFormatting color;
    private final String translationKey;

    LocusSource(ChatFormatting color, String translationKey) {
        this.color = color;
        this.translationKey = translationKey;
    }

    public ChatFormatting getColor() {
        return color;
    }

    public Component getDisplayName() {
        return Component.translatable(translationKey).withStyle(color);
    }

    public boolean isDegrading() {
        return this == TRANSIENT || this == REJECTED;
    }

    public boolean causesSideEffects() {
        return this == REJECTED || this == TRANSIENT;
    }
}