package net.farkas.wildaside.screen.bioengineering_workstation;

import net.farkas.wildaside.dna.Gene;
import net.farkas.wildaside.dna.traits.Trait;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Optional;

public class GeneWidget extends AbstractWidget {
    private final Trait trait;
    public Gene gene;
    private boolean isSelected = false;

    public GeneWidget(int x, int y, int width, int height, Gene gene) {
        super(x, y, width, height, Component.literal(gene.trait().name()));
        this.trait = gene.trait();
        this.gene = gene;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        int color = isSelected ? 0xAA00FF00 : (isHovered ? 0xAAFFFFFF : 0xAA808080);
        graphics.fill(getX(), getY(), getX() + width, getY() + height, color);

        graphics.drawString(Minecraft.getInstance().font, trait.name() + ": " + String.format("%.2f", gene.value()), getX() + 3, getY() + 3, 0xFFFFFF);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {
        defaultButtonNarrationText(pNarrationElementOutput);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }

    protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        if (isHovered) {
            graphics.renderTooltip(Minecraft.getInstance().font,
                    List.of(
                            Component.literal(trait.name()).withStyle(trait.traitType().entryColour),
                            Component.literal("Value: " + gene.value()),
                            Component.literal("Instability: " + gene.stabilityCost())
                    ), Optional.empty(), mouseX, mouseY);
        }
    }

    public Trait getTrait() { return trait; }
    public Gene getGene() { return gene; }

    public void setSelected(boolean selected) {
        this.isSelected = selected;
    }
}