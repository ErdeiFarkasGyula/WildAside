package net.farkas.wildaside.dna.trait;

import net.minecraft.ChatFormatting;

public enum TraitTypes {
    CORE(ChatFormatting.YELLOW, ChatFormatting.WHITE),
    RESISTANCE(ChatFormatting.BLUE, ChatFormatting.WHITE),
    ABILITY(ChatFormatting.RED, ChatFormatting.LIGHT_PURPLE);

    public final ChatFormatting headerColour;
    public final ChatFormatting entryColour;

    TraitTypes(ChatFormatting headerColour, ChatFormatting entryColour) {
        this.headerColour = headerColour;
        this.entryColour = entryColour;
    }
}
