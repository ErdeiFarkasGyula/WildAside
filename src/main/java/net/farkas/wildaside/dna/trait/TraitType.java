package net.farkas.wildaside.dna.trait;

import net.farkas.wildaside.dna.dominance.DominanceExpression;
import net.minecraft.ChatFormatting;

public enum TraitType {
    CORE(ChatFormatting.YELLOW, ChatFormatting.WHITE,
            new DominanceExpression(0.7f, 0.3f, 0f, 0f)),
    RESISTANCE(ChatFormatting.BLUE, ChatFormatting.WHITE,
            new DominanceExpression(0.4f, 0.5f, 0.1f, 0f)),
    ABILITY(ChatFormatting.RED, ChatFormatting.LIGHT_PURPLE,
            new DominanceExpression(0.5f, 0.2f, 0.2f, 0.1f));

    private final ChatFormatting headerColour;
    private final ChatFormatting entryColour;
    private final DominanceExpression dominanceExpression;

    TraitType(ChatFormatting headerColour, ChatFormatting entryColour, DominanceExpression dominanceExpression) {
        this.headerColour = headerColour;
        this.entryColour = entryColour;
        this.dominanceExpression = dominanceExpression;
    }

    public ChatFormatting getHeaderColour() { return headerColour; }
    public ChatFormatting getEntryColour() { return entryColour; }
    public DominanceExpression getDominanceExpression() { return dominanceExpression; }
}
