package net.farkas.wildaside.screen.bioengineering_workstation;

import com.mojang.blaze3d.systems.RenderSystem;
import net.farkas.wildaside.dna.Gene;
import net.farkas.wildaside.dna.allele.Allele;
import net.farkas.wildaside.dna.allele.dominance.Dominance;
import net.farkas.wildaside.dna.allele.value.FloatAlleleValue;
import net.farkas.wildaside.dna.editor.GeneEditorOperation;
import net.farkas.wildaside.dna.editor.GeneEditorState;
import net.farkas.wildaside.dna.locus.GeneLocus;
import net.farkas.wildaside.dna.trait.Trait;
import net.farkas.wildaside.dna.trait.TraitRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class GeneDetailPanel {
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final Gene gene;
    private final boolean fromTop;
    private final GeneEditorState state;
    private final Font font;
    private final Consumer<AlleleClickData> onAlleleClick;
    private final Consumer<DominanceClickData> onDominanceClick;
    private final Runnable onClose;

    private static final int PADDING = 6;
    private static final int LINE_HEIGHT = 12;
    private static final int ALLELE_BOX_HEIGHT = 50;

    public GeneDetailPanel(int x, int y, int width, int height, Gene gene, boolean fromTop,
                           GeneEditorState state, Font font,
                           Consumer<AlleleClickData> onAlleleClick,
                           Consumer<DominanceClickData> onDominanceClick,
                           Runnable onClose) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.gene = gene;
        this.fromTop = fromTop;
        this.state = state;
        this.font = font;
        this.onAlleleClick = onAlleleClick;
        this.onDominanceClick = onDominanceClick;
        this.onClose = onClose;
    }

    public void render(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.fill(x, y, x + width, y + height, 0xEE222222);
        graphics.renderOutline(x, y, width, height, 0xFF666666);

        int closeX = x + width - 12;
        int closeY = y + 4;
        boolean hoverClose = mouseX >= closeX && mouseX <= closeX + 8 && mouseY >= closeY && mouseY <= closeY + 8;
        graphics.drawString(font, "×", closeX, closeY, hoverClose ? 0xFFFF4444 : 0xFFAAAAAA, false);

        Trait trait = gene.getTrait();
        Component traitName = TraitRegistry.translatableTrait(trait);
        graphics.drawString(font, traitName, x + PADDING, y + PADDING, 0xFFFFAA00, false);

        Component expressedLabel = Component.literal("Expressed: ")
                .withStyle(ChatFormatting.GRAY)
                .append(gene.getExpressedValueHolder().format());
        graphics.drawString(font, expressedLabel, x + PADDING, y + PADDING + LINE_HEIGHT, 0xFFFFFFFF, false);

        int alleleY = y + PADDING + LINE_HEIGHT * 3;

        renderAlleleBox(graphics, x + PADDING, alleleY, "Allele A", gene.getAlleleA(), true, mouseX, mouseY);
        renderAlleleBox(graphics, x + PADDING + (width / 2) - PADDING, alleleY, "Allele B", gene.getAlleleB(), false, mouseX, mouseY);

        int operationY = alleleY + ALLELE_BOX_HEIGHT + 10;
        renderOperationButtons(graphics, x + PADDING, operationY, mouseX, mouseY);
    }

    private void renderAlleleBox(GuiGraphics graphics, int bx, int by, String label, Allele allele, boolean isA, int mouseX, int mouseY) {
        int boxWidth = (width / 2) - PADDING * 2;

        boolean selected = state.getSelectedAllele() != null &&
                state.isSelectedAlleleA() == isA &&
                state.isSelectedAlleleFromTop() == fromTop;

        int bgColor = selected ? 0xFF444466 : 0xFF333333;
        boolean hoverBox = mouseX >= bx && mouseX <= bx + boxWidth && mouseY >= by && mouseY <= by + ALLELE_BOX_HEIGHT;
        if (hoverBox && !selected) bgColor = 0xFF3A3A3A;

        graphics.fill(bx, by, bx + boxWidth, by + ALLELE_BOX_HEIGHT, bgColor);
        graphics.renderOutline(bx, by, boxWidth, ALLELE_BOX_HEIGHT, selected ? 0xFF8888FF : 0xFF555555);

        graphics.drawString(font, label, bx + 4, by + 4, 0xFFAAAAFF, false);

        String valueStr;
        if (allele.getValueHolder() instanceof FloatAlleleValue fav) {
            valueStr = String.format("%.4f", fav.get());
        }
        else {
            valueStr = allele.getValueHolder().format().getString();
        }
        graphics.drawString(font, "Value: " + valueStr, bx + 4, by + 16, 0xFFFFFFFF, false);

        Dominance dom = allele.getDominance();
        int domColor = switch (dom) {
            case DOMINANT -> 0xFF44FF44;
            case RECESSIVE -> 0xFFFF4444;
            case CO_DOMINANT -> 0xFFFFFF44;
            case INCOMPLETE -> 0xFF44FFFF;
        };

        int domBtnX = bx + 4;
        int domBtnY = by + 28;
        int domBtnW = boxWidth - 8;
        int domBtnH = 12;

        boolean hoverDom = mouseX >= domBtnX && mouseX <= domBtnX + domBtnW && mouseY >= domBtnY && mouseY <= domBtnY + domBtnH;
        graphics.fill(domBtnX, domBtnY, domBtnX + domBtnW, domBtnY + domBtnH, hoverDom ? 0xFF555555 : 0xFF444444);
        graphics.drawString(font, dom.name(), domBtnX + 2, domBtnY + 2, domColor, false);

        String mutStr = String.format("Mut: %.2f%%", allele.getMutationRate() * 100);
        graphics.drawString(font, mutStr, bx + 4, by + 42, 0xFF888888, false);
    }

    private void renderOperationButtons(GuiGraphics graphics, int ox, int oy, int mouseX, int mouseY) {
        graphics.drawString(font, "Operations:", ox, oy, 0xFFAAAAAA, false);

        int btnY = oy + LINE_HEIGHT;
        int btnHeight = 14;
        int btnWidth = width - PADDING * 2;

        GeneEditorOperation[] ops = {
                GeneEditorOperation.SWAP_ALLELE,
                GeneEditorOperation.MODIFY_DOMINANCE,
                GeneEditorOperation.STABILIZE,
                GeneEditorOperation.AMPLIFY
        };

        for (GeneEditorOperation op : ops) {
            boolean hover = mouseX >= ox && mouseX <= ox + btnWidth && mouseY >= btnY && mouseY <= btnY + btnHeight;
            boolean selected = state.getCurrentOperation() == op;

            int bg = selected ? 0xFF4444AA : (hover ? 0xFF444444 : 0xFF333333);
            graphics.fill(ox, btnY, ox + btnWidth, btnY + btnHeight, bg);
            graphics.renderOutline(ox, btnY, btnWidth, btnHeight, selected ? 0xFF6666FF : 0xFF555555);

            graphics.drawString(font, op.getDisplayName(), ox + 4, btnY + 3, 0xFFFFFFFF, false);

            btnY += btnHeight + 2;
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return false;

        int closeX = x + width - 12;
        int closeY = y + 4;
        if (mouseX >= closeX && mouseX <= closeX + 8 && mouseY >= closeY && mouseY <= closeY + 8) {
            onClose.run();
            return true;
        }

        int alleleY = y + PADDING + LINE_HEIGHT * 3;
        int boxWidth = (width / 2) - PADDING * 2;

        int alleleAX = x + PADDING;
        int alleleBX = x + PADDING + (width / 2) - PADDING;

        if (mouseX >= alleleAX && mouseX <= alleleAX + boxWidth && mouseY >= alleleY && mouseY <= alleleY + ALLELE_BOX_HEIGHT) {
            int domBtnY = alleleY + 28;
            if (mouseY >= domBtnY && mouseY <= domBtnY + 12) {
                onDominanceClick.accept(new DominanceClickData(gene, true, fromTop));
            }
            else {
                onAlleleClick.accept(new AlleleClickData(gene.getAlleleA(), true, fromTop, gene));
            }
            return true;
        }

        if (mouseX >= alleleBX && mouseX <= alleleBX + boxWidth && mouseY >= alleleY && mouseY <= alleleY + ALLELE_BOX_HEIGHT) {
            int domBtnY = alleleY + 28;
            if (mouseY >= domBtnY && mouseY <= domBtnY + 12) {
                onDominanceClick.accept(new DominanceClickData(gene, false, fromTop));
            }
            else {
                onAlleleClick.accept(new AlleleClickData(gene.getAlleleB(), false, fromTop, gene));
            }
            return true;
        }

        int operationY = alleleY + ALLELE_BOX_HEIGHT + 10 + LINE_HEIGHT;
        int btnHeight = 14;
        int btnWidth = width - PADDING * 2;
        int ox = x + PADDING;

        GeneEditorOperation[] ops = {
                GeneEditorOperation.SWAP_ALLELE,
                GeneEditorOperation.MODIFY_DOMINANCE,
                GeneEditorOperation.STABILIZE,
                GeneEditorOperation.AMPLIFY
        };

        int btnY = operationY;
        for (GeneEditorOperation op : ops) {
            if (mouseX >= ox && mouseX <= ox + btnWidth && mouseY >= btnY && mouseY <= btnY + btnHeight) {
                state.setCurrentOperation(op);
                return true;
            }
            btnY += btnHeight + 2;
        }

        return false;
    }

    public boolean isInside(double mouseX, double mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public record AlleleClickData(Allele allele, boolean isA, boolean fromTop, Gene gene) {
    }

    public record DominanceClickData(Gene gene, boolean isAlleleA, boolean fromTop) {
    }
}