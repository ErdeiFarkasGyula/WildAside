package net.farkas.wildaside.dna.trait;

import net.farkas.wildaside.dna.chromosome.ChromosomeType;
import net.minecraft.ChatFormatting;

public enum TraitType {
    CORE(ChatFormatting.YELLOW, ChatFormatting.WHITE),
    RESISTANCE(ChatFormatting.BLUE, ChatFormatting.WHITE),
    ABILITY(ChatFormatting.RED, ChatFormatting.LIGHT_PURPLE),
    APPEARANCE(ChatFormatting.DARK_PURPLE, ChatFormatting.WHITE);

    private final ChatFormatting headerColour;
    private final ChatFormatting entryColour;

    TraitType(ChatFormatting headerColour, ChatFormatting entryColour) {
        this.headerColour = headerColour;
        this.entryColour = entryColour;
    }

    public ChatFormatting getHeaderColour() { return headerColour; }
    public ChatFormatting getEntryColour() { return entryColour; }

    public ChromosomeType getChromosomeType() {
        return switch (this) {
            case CORE -> ChromosomeType.CORE;
            case RESISTANCE -> ChromosomeType.RESISTANCE;
            case ABILITY -> ChromosomeType.SPECIAL;
            case APPEARANCE -> ChromosomeType.APPEARANCE;
        };
    }
}
