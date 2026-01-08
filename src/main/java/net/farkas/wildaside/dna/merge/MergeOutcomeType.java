package net.farkas.wildaside.dna.merge;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public enum MergeOutcomeType {
    INTEGRATED(ChatFormatting.GREEN, "dna.wildaside.merge.integrated"),
    TRANSIENT(ChatFormatting.YELLOW, "dna.wildaside.merge.transient"),
    REPLACED(ChatFormatting.AQUA, "dna.wildaside.merge.replaced"),
    REJECTED(ChatFormatting.RED, "dna.wildaside.merge.rejected");

    private final ChatFormatting color;
    private final String translationKey;

    MergeOutcomeType(ChatFormatting color, String translationKey) {
        this.color = color;
        this.translationKey = translationKey;
    }

    public Component getMessage() {
        return Component.translatable(translationKey).withStyle(color);
    }
}