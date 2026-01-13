package net.farkas.wildaside.screen.gene_editor;

import com.mojang.blaze3d.systems.RenderSystem;
import net.farkas.wildaside.capability.dna.DnaImplementation;
import net.farkas.wildaside.dna.DnaUtils;
import net.farkas.wildaside.dna.Gene;
import net.farkas.wildaside.dna.allele.Allele;
import net.farkas.wildaside.dna.allele.dominance.Dominance;
import net.farkas.wildaside.dna.allele.value.FloatAlleleValue;
import net.farkas.wildaside.dna.chromosome.Genome;
import net.farkas.wildaside.dna.editor.GeneEditorOperation;
import net.farkas.wildaside.dna.editor.GeneEditorOperations;
import net.farkas.wildaside.dna.editor.GeneEditorResult;
import net.farkas.wildaside.dna.editor.GeneEditorState;
import net.farkas.wildaside.dna.locus.GeneLocus;
import net.farkas.wildaside.dna.trait.Trait;
import net.farkas.wildaside.dna.trait.TraitRegistry;
import net.farkas.wildaside.dna.trait.TraitType;
import net.farkas.wildaside.network.NetworkHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import java.util.*;

public class AdvancedGeneEditorScreen extends Screen {
    private static final int SCREEN_MARGIN = 6;
    private static final int OUTER_PADDING = 6;
    private static final int INNER_PADDING = 4;
    private static final int ELEMENT_SPACING = 2;

    private static final float DNA_PANEL_WIDTH_RATIO = 0.38f;

    private static final int SCROLLBAR_WIDTH = 6;
    private static final int SCROLLBAR_MIN_THUMB_HEIGHT = 10;

    private static final int MAX_LOCI_DISPLAY = 3;

    private final BlockPos workstationPos;
    private final Player player;

    private DnaImplementation dnaA;
    private DnaImplementation dnaB;
    private Map<Trait, Gene> genesA = new LinkedHashMap<>();
    private Map<Trait, Gene> genesB = new LinkedHashMap<>();

    private final GeneEditorState state = new GeneEditorState();

    private int leftPos;
    private int topPos;
    private int screenWidth;
    private int screenHeight;

    private int headerHeight;
    private int footerHeight;

    private int dnaPanelWidth;
    private int dnaPanelHeight;
    private int detailPanelWidth;
    private int detailPanelHeight;

    private int dnaPanelHeaderHeight;
    private int geneEntryHeight;
    private int traitTypeHeaderHeight;

    private int traitHeaderHeight;
    private int alleleBoxHeight;
    private int alleleLineHeight;
    private int lociLineHeight;

    private int opsHeaderHeight;
    private int operationEntryHeight;

    private int closeButtonSize;
    private int actionButtonWidth;
    private int actionButtonHeight;

    private int statusBarHeight;

    private int scrollOffsetA = 0;
    private int scrollOffsetB = 0;
    private int scrollOffsetOps = 0;
    private int maxScrollA = 0;
    private int maxScrollB = 0;
    private int maxScrollOps = 0;
    private boolean draggingScrollbarA = false;
    private boolean draggingScrollbarB = false;
    private boolean draggingScrollbarOps = false;
    private double dragStartY = 0;
    private int dragStartScroll = 0;

    @Nullable
    private GeneSelection selectionA = null;
    @Nullable
    private GeneSelection selectionB = null;

    private final List<Component> statusMessages = new ArrayList<>();

    private int opsListX;
    private int opsListY;
    private int opsListWidth;
    private int opsListHeight;

    public AdvancedGeneEditorScreen(BlockPos workstationPos, DnaImplementation dnaA, DnaImplementation dnaB) {
        super(Component.translatable("gui.wildaside.advanced_gene_editor"));
        this.workstationPos = workstationPos;
        this.player = Minecraft.getInstance().player;
        this.dnaA = dnaA;
        this.dnaB = dnaB;
    }

    @Override
    protected void init() {
        super.init();

        calculateAllDimensions();
        rebuildGenes();
        calculateOperationsScroll();

        initWidgets();
    }

    private void initWidgets() {
        clearWidgets();

        int closeBtnX = leftPos + screenWidth - closeButtonSize - OUTER_PADDING;
        int closeBtnY = topPos + (headerHeight - closeButtonSize) / 2;
        addRenderableWidget(Button.builder(Component.literal("×"), btn -> onClose())
                .pos(closeBtnX, closeBtnY)
                .size(closeButtonSize, closeButtonSize)
                .build());

        int footerContentY = topPos + screenHeight - footerHeight;
        int buttonY = footerContentY + (footerHeight - actionButtonHeight) / 2;

        int totalButtonsWidth = actionButtonWidth * 2 + OUTER_PADDING;
        int buttonsStartX = leftPos + screenWidth - OUTER_PADDING - totalButtonsWidth;

        addRenderableWidget(Button.builder(Component.translatable("gui.wildaside.gene_editor.reset"), btn -> resetChanges())
                .pos(buttonsStartX, buttonY)
                .size(actionButtonWidth, actionButtonHeight)
                .build());

        addRenderableWidget(Button.builder(Component.translatable("gui.wildaside.gene_editor.execute"), btn -> executeOperation())
                .pos(buttonsStartX + actionButtonWidth + OUTER_PADDING, buttonY)
                .size(actionButtonWidth, actionButtonHeight)
                .build());
    }

    private void calculateAllDimensions() {
        int fontHeight = font.lineHeight;

        geneEntryHeight = fontHeight + 3;
        traitTypeHeaderHeight = fontHeight + 4;
        operationEntryHeight = fontHeight + 4;
        alleleLineHeight = fontHeight + 1;
        lociLineHeight = fontHeight + 1;
        traitHeaderHeight = fontHeight + 4;
        opsHeaderHeight = fontHeight + 2;
        dnaPanelHeaderHeight = fontHeight * 2 + INNER_PADDING + ELEMENT_SPACING;
        alleleBoxHeight = alleleLineHeight * 4 + INNER_PADDING * 2;

        headerHeight = fontHeight + OUTER_PADDING * 2;
        footerHeight = fontHeight + OUTER_PADDING * 2 + 4;
        statusBarHeight = fontHeight + 4;

        closeButtonSize = fontHeight + 4;
        actionButtonWidth = font.width("Execute") + 16;
        actionButtonHeight = fontHeight + 6;

        screenWidth = this.width - SCREEN_MARGIN * 2;
        screenHeight = this.height - SCREEN_MARGIN * 2;

        leftPos = SCREEN_MARGIN;
        topPos = SCREEN_MARGIN;

        int contentWidth = screenWidth - OUTER_PADDING * 3;
        dnaPanelWidth = (int) (contentWidth * DNA_PANEL_WIDTH_RATIO);
        detailPanelWidth = contentWidth - dnaPanelWidth;

        int contentHeight = screenHeight - headerHeight - footerHeight - OUTER_PADDING * 2;
        dnaPanelHeight = (contentHeight - OUTER_PADDING) / 2;
        detailPanelHeight = dnaPanelHeight * 2 + OUTER_PADDING;
    }

    private void rebuildGenes() {
        genesA = buildGeneMap(dnaA);
        genesB = buildGeneMap(dnaB);

        int totalEntriesA = calculateTotalEntries(genesA);
        int totalEntriesB = calculateTotalEntries(genesB);

        int contentHeight = getDnaPanelContentHeight();
        int visibleEntries = Math.max(1, contentHeight / geneEntryHeight);

        maxScrollA = Math.max(0, totalEntriesA - visibleEntries);
        maxScrollB = Math.max(0, totalEntriesB - visibleEntries);

        scrollOffsetA = Math.min(scrollOffsetA, maxScrollA);
        scrollOffsetB = Math.min(scrollOffsetB, maxScrollB);
    }

    private int getDnaPanelContentHeight() {
        return dnaPanelHeight - dnaPanelHeaderHeight - INNER_PADDING;
    }

    private void calculateOperationsScroll() {
        if (opsListHeight <= 0) return;

        int opsCount = GeneEditorOperation.values().length;
        int opsContentHeight = getOpsContentHeight();
        int visibleOps = Math.max(1, opsContentHeight / operationEntryHeight);

        maxScrollOps = Math.max(0, opsCount - visibleOps);
        scrollOffsetOps = Math.min(scrollOffsetOps, maxScrollOps);
    }

    private int getOpsContentHeight() {
        return Math.max(1, opsListHeight - opsHeaderHeight - INNER_PADDING);
    }

    private Map<Trait, Gene> buildGeneMap(DnaImplementation dna) {
        if (dna == null) return new LinkedHashMap<>();
        
        Map<Trait, List<GeneLocus>> loci;
        if (dna.getGenome() != null && hasGenomeSequences(dna.getGenome())) {
            loci = DnaUtils.convertGenomeToLoci(dna.getGenome());
        } else {
            loci = dna.getGenomeLociView();
        }
        
        if (loci.isEmpty()) return new LinkedHashMap<>();

        Map<Trait, Gene> result = new LinkedHashMap<>();

        for (TraitType type : TraitType.values()) {
            for (Map.Entry<Trait, List<GeneLocus>> entry : loci.entrySet()) {
                if (entry.getKey().getTraitType() == type) {
                    Gene gene = DnaUtils.asGene(entry.getKey(), entry.getValue());
                    if (gene != null) {
                        result.put(entry.getKey(), gene);
                    }
                }
            }
        }

        return result;
    }

    private int calculateTotalEntries(Map<Trait, Gene> genes) {
        if (genes.isEmpty()) return 0;

        Set<TraitType> types = new HashSet<>();
        for (Trait t : genes.keySet()) {
            types.add(t.getTraitType());
        }
        return genes.size() + types.size();
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        super.resize(minecraft, width, height);
        init();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);

        renderMainBackground(graphics);
        renderTitle(graphics);

        int dnaPanelX = leftPos + OUTER_PADDING;
        int dnaPanelAY = topPos + headerHeight + OUTER_PADDING;
        int dnaPanelBY = dnaPanelAY + dnaPanelHeight + OUTER_PADDING;

        renderDnaPanel(graphics, dnaPanelX, dnaPanelAY, dnaA, genesA, true, scrollOffsetA, mouseX, mouseY);
        renderDnaPanel(graphics, dnaPanelX, dnaPanelBY, dnaB, genesB, false, scrollOffsetB, mouseX, mouseY);

        int detailPanelX = dnaPanelX + dnaPanelWidth + OUTER_PADDING;
        int detailPanelY = topPos + headerHeight + OUTER_PADDING;
        renderDetailPanel(graphics, detailPanelX, detailPanelY, mouseX, mouseY);

        renderFooter(graphics);

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    private void renderMainBackground(GuiGraphics graphics) {
        graphics.fill(leftPos, topPos, leftPos + screenWidth, topPos + screenHeight, 0xF0101020);
        graphics.fill(leftPos + 1, topPos + 1, leftPos + screenWidth - 1, topPos + screenHeight - 1, 0xF01A1A2E);
        graphics.renderOutline(leftPos, topPos, screenWidth, screenHeight, 0xFF4A4A7E);

        graphics.fill(leftPos + 2, topPos + 2, leftPos + screenWidth - 2, topPos + headerHeight - 1, 0xFF252545);
        graphics.hLine(leftPos + 2, leftPos + screenWidth - 3, topPos + headerHeight - 1, 0xFF4A4A7E);

        int footerY = topPos + screenHeight - footerHeight;
        graphics.hLine(leftPos + 2, leftPos + screenWidth - 3, footerY, 0xFF4A4A7E);
        graphics.fill(leftPos + 2, footerY + 1, leftPos + screenWidth - 2, topPos + screenHeight - 2, 0xFF252545);
    }

    private void renderTitle(GuiGraphics graphics) {
        Component title = Component.translatable("gui.wildaside.advanced_gene_editor")
                .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD);
        int titleY = topPos + (headerHeight - font.lineHeight) / 2;
        graphics.drawString(font, title, leftPos + OUTER_PADDING, titleY, 0xFFFFFFFF, true);
    }

    private void renderFooter(GuiGraphics graphics) {
        int footerY = topPos + screenHeight - footerHeight;
        int statusBarY = footerY + (footerHeight - statusBarHeight) / 2;

        int totalButtonsWidth = actionButtonWidth * 2 + OUTER_PADDING;
        int statusBarWidth = screenWidth - OUTER_PADDING * 3 - totalButtonsWidth;
        int statusBarX = leftPos + OUTER_PADDING;

        graphics.fill(statusBarX, statusBarY, statusBarX + statusBarWidth, statusBarY + statusBarHeight, 0x66000000);

        if (!statusMessages.isEmpty()) {
            Component lastMessage = statusMessages.get(statusMessages.size() - 1);
            String msgStr = truncateToWidth(lastMessage.getString(), statusBarWidth - ELEMENT_SPACING * 2);
            int textY = statusBarY + (statusBarHeight - font.lineHeight) / 2;
            graphics.drawString(font, msgStr, statusBarX + ELEMENT_SPACING, textY, 0xFFFFFFFF, false);
        }
    }

    private void renderDnaPanel(GuiGraphics graphics, int px, int py, DnaImplementation dna, Map<Trait, Gene> genes, boolean isTop, int scrollOffset, int mouseX, int mouseY) {
        graphics.fill(px, py, px + dnaPanelWidth, py + dnaPanelHeight, 0xCC1E1E38);
        graphics.renderOutline(px, py, dnaPanelWidth, dnaPanelHeight, 0xFF4A4AAA);

        String label = isTop ? "DNA A" : "DNA B";
        int labelColor = isTop ? 0xFF88AAFF : 0xFFFFAA88;
        graphics.drawString(font, label, px + INNER_PADDING, py + INNER_PADDING, labelColor, false);

        if (dna == null || genes.isEmpty()) {
            graphics.drawString(font, Component.translatable("gui.wildaside.gene_editor.no_dna")
                    .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC), px + INNER_PADDING, py + dnaPanelHeaderHeight, 0xFFAAAAAA, false);
            return;
        }

        String sourceStr = dna.getSource().getDescription().getString();
        int maxSourceWidth = dnaPanelWidth - INNER_PADDING * 2;
        sourceStr = truncateToWidth(sourceStr, maxSourceWidth);
        graphics.drawString(font, sourceStr, px + INNER_PADDING, py + INNER_PADDING + font.lineHeight + ELEMENT_SPACING, 0xFF888888, false);

        int contentX = px + INNER_PADDING;
        int contentY = py + dnaPanelHeaderHeight;
        int contentWidth = dnaPanelWidth - INNER_PADDING * 2 - SCROLLBAR_WIDTH - 2;
        int contentHeight = getDnaPanelContentHeight();

        enableScissor(graphics, contentX, contentY, contentWidth, contentHeight);

        int entryY = contentY - scrollOffset * geneEntryHeight;
        TraitType currentType = null;

        for (Map.Entry<Trait, Gene> entry : genes.entrySet()) {
            Trait trait = entry.getKey();
            Gene gene = entry.getValue();

            if (trait.getTraitType() != currentType) {
                currentType = trait.getTraitType();

                int headerTop = entryY + 1;
                int headerBottom = entryY + traitTypeHeaderHeight - 1;

                if (headerBottom > contentY && headerTop < contentY + contentHeight) {
                    int clippedTop = Math.max(contentY, headerTop);
                    int clippedBottom = Math.min(contentY + contentHeight, headerBottom);
                    graphics.fill(contentX, clippedTop, contentX + contentWidth, clippedBottom, 0x44FFFFFF);

                    int textY = entryY + (traitTypeHeaderHeight - font.lineHeight) / 2;
                    if (textY >= contentY && textY + font.lineHeight <= contentY + contentHeight) {
                        graphics.drawString(font, currentType.name(), contentX + ELEMENT_SPACING, textY, currentType.getHeaderColour().getColor(), false);
                    }
                }
                entryY += traitTypeHeaderHeight;
            }

            int entryTop = entryY;
            int entryBottom = entryY + geneEntryHeight - 1;

            if (entryBottom > contentY && entryTop < contentY + contentHeight) {
                boolean selected = (isTop && selectionA != null && selectionA.trait.equals(trait)) ||
                        (!isTop && selectionB != null && selectionB.trait.equals(trait));
                boolean hovered = mouseX >= contentX && mouseX <= contentX + contentWidth &&
                        mouseY >= Math.max(contentY, entryTop) && mouseY < Math.min(contentY + contentHeight, entryBottom);

                int bgColor = selected ? 0x884444FF : (hovered ? 0x44FFFFFF : 0x00000000);
                if (bgColor != 0) {
                    int clippedTop = Math.max(contentY, entryTop);
                    int clippedBottom = Math.min(contentY + contentHeight, entryBottom);
                    graphics.fill(contentX, clippedTop, contentX + contentWidth, clippedBottom, bgColor);
                }

                int textY = entryY + (geneEntryHeight - font.lineHeight) / 2;
                if (textY >= contentY && textY + font.lineHeight <= contentY + contentHeight) {
                    String traitName = TraitRegistry.translatableTrait(trait).getString();
                    String valueStr = formatGeneValue(gene);
                    int valueWidth = font.width(valueStr);
                    int maxTraitWidth = contentWidth - valueWidth - ELEMENT_SPACING * 3;
                    traitName = truncateToWidth(traitName, maxTraitWidth);

                    int textColor = selected ? 0xFFFFFF44 : 0xFFFFFFFF;
                    graphics.drawString(font, traitName, contentX + ELEMENT_SPACING, textY, textColor, false);
                    graphics.drawString(font, valueStr, contentX + contentWidth - valueWidth - ELEMENT_SPACING, textY, 0xFFAAFFAA, false);
                }
            }

            entryY += geneEntryHeight;
        }

        disableScissor(graphics);

        int maxScroll = isTop ? maxScrollA : maxScrollB;
        int currentScroll = isTop ? scrollOffsetA : scrollOffsetB;
        boolean dragging = isTop ? draggingScrollbarA : draggingScrollbarB;
        int scrollbarX = px + dnaPanelWidth - SCROLLBAR_WIDTH - INNER_PADDING;
        renderScrollbar(graphics, scrollbarX, contentY, SCROLLBAR_WIDTH, contentHeight, maxScroll, currentScroll, mouseX, mouseY, dragging, geneEntryHeight);
    }

    private void renderScrollbar(GuiGraphics graphics, int sx, int sy, int sw, int sh,
                                 int maxScroll, int currentScroll, int mouseX, int mouseY, boolean dragging, int entryHeight) {
        graphics.fill(sx, sy, sx + sw, sy + sh, 0x44000000);

        if (maxScroll <= 0) return;

        int totalContentHeight = maxScroll * entryHeight + sh;
        int thumbHeight = Math.max(SCROLLBAR_MIN_THUMB_HEIGHT, sh * sh / totalContentHeight);
        thumbHeight = Math.min(thumbHeight, sh - 4);

        int scrollRange = sh - thumbHeight - 4;
        int thumbY = sy + 2 + (maxScroll > 0 ? currentScroll * scrollRange / maxScroll : 0);

        boolean hovered = mouseX >= sx && mouseX <= sx + sw && mouseY >= thumbY && mouseY <= thumbY + thumbHeight;

        int thumbColor = dragging ? 0xFFAAAAFF : (hovered ? 0xFF8888DD : 0xFF6666AA);
        graphics.fill(sx + 1, thumbY, sx + sw - 1, thumbY + thumbHeight, thumbColor);
    }

    private void renderDetailPanel(GuiGraphics graphics, int px, int py, int mouseX, int mouseY) {
        graphics.fill(px, py, px + detailPanelWidth, py + detailPanelHeight, 0xCC1E1E38);
        graphics.renderOutline(px, py, detailPanelWidth, detailPanelHeight, 0xFF4A4AAA);

        graphics.drawString(font, "Details", px + INNER_PADDING, py + INNER_PADDING, 0xFFAAAAFF, false);

        Gene selectedGene = null;
        boolean fromTop = true;

        if (selectionA != null) {
            selectedGene = selectionA.gene;
            fromTop = true;
        } else if (selectionB != null) {
            selectedGene = selectionB.gene;
            fromTop = false;
        }

        int opsStartY;

        if (selectedGene == null) {
            int noSelectionY = py + INNER_PADDING + font.lineHeight + ELEMENT_SPACING * 2;
            graphics.drawString(font, Component.translatable("gui.wildaside.gene_editor.select_gene")
                    .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC), px + INNER_PADDING, noSelectionY, 0xFFAAAAAA, false);
            opsStartY = noSelectionY + font.lineHeight + ELEMENT_SPACING * 2;
        } else {
            Trait trait = selectedGene.getTrait();

            int traitHeaderY = py + INNER_PADDING + font.lineHeight + ELEMENT_SPACING;
            graphics.fill(px + INNER_PADDING, traitHeaderY, px + detailPanelWidth - INNER_PADDING, traitHeaderY + traitHeaderHeight, 0x66000000);

            int traitTextY = traitHeaderY + (traitHeaderHeight - font.lineHeight) / 2;
            String traitName = TraitRegistry.translatableTrait(trait).getString();
            String typeStr = trait.getTraitType().name();
            int typeWidth = font.width(typeStr);
            int maxTraitNameWidth = detailPanelWidth - INNER_PADDING * 2 - typeWidth - ELEMENT_SPACING * 3;
            traitName = truncateToWidth(traitName, maxTraitNameWidth);

            graphics.drawString(font, traitName, px + INNER_PADDING + ELEMENT_SPACING, traitTextY, 0xFFFFAA00, false);

            int typeColor = trait.getTraitType().getHeaderColour().getColor();
            graphics.drawString(font, typeStr, px + detailPanelWidth - INNER_PADDING - typeWidth - ELEMENT_SPACING, traitTextY, typeColor, false);

            int infoY = traitHeaderY + traitHeaderHeight + ELEMENT_SPACING;
            String expressedStr = "Value: " + formatGeneValue(selectedGene);
            int maxExprWidth = detailPanelWidth - INNER_PADDING * 2;
            expressedStr = truncateToWidth(expressedStr, maxExprWidth);
            graphics.drawString(font, expressedStr, px + INNER_PADDING, infoY, 0xFFFFFFFF, false);

            int alleleY = infoY + font.lineHeight + ELEMENT_SPACING;
            int alleleBoxWidth = (detailPanelWidth - INNER_PADDING * 2 - ELEMENT_SPACING) / 2;

            renderAlleleBox(graphics, px + INNER_PADDING, alleleY, alleleBoxWidth, alleleBoxHeight,
                    "Allele A", selectedGene.getAlleleA(),
                    state.getSelectedAllele() != null && state.isSelectedAlleleA(), mouseX, mouseY);

            renderAlleleBox(graphics, px + INNER_PADDING + alleleBoxWidth + ELEMENT_SPACING, alleleY, alleleBoxWidth, alleleBoxHeight,
                    "Allele B", selectedGene.getAlleleB(),
                    state.getSelectedAllele() != null && !state.isSelectedAlleleA(), mouseX, mouseY);

            int lociY = alleleY + alleleBoxHeight + ELEMENT_SPACING;
            Map<Trait, List<GeneLocus>> lociMap;
            if (fromTop) {
                if (dnaA.getGenome() != null && hasGenomeSequences(dnaA.getGenome())) {
                    lociMap = DnaUtils.convertGenomeToLoci(dnaA.getGenome());
                } else {
                    lociMap = dnaA.getGenomeLociView();
                }
            } else {
                if (dnaB.getGenome() != null && hasGenomeSequences(dnaB.getGenome())) {
                    lociMap = DnaUtils.convertGenomeToLoci(dnaB.getGenome());
                } else {
                    lociMap = dnaB.getGenomeLociView();
                }
            }
            List<GeneLocus> loci = lociMap.get(trait);
            if (loci != null && !loci.isEmpty()) {
                graphics.drawString(font, "Loci (" + loci.size() + "):", px + INNER_PADDING, lociY, 0xFFAAAAFF, false);

                int locusEntryY = lociY + font.lineHeight;
                int displayCount = Math.min(loci.size(), MAX_LOCI_DISPLAY);
                for (int i = 0; i < displayCount; i++) {
                    GeneLocus locus = loci.get(i);
                    String locusId = locus.getId();
                    String sourceName = locus.getSource().name();
                    if (sourceName.length() > 3) sourceName = sourceName.substring(0, 3);

                    String locusInfo = String.format("• %s [%s] s=%.2f", locusId, sourceName, locus.getStability());
                    int maxLocusWidth = detailPanelWidth - INNER_PADDING * 2 - ELEMENT_SPACING;
                    locusInfo = truncateToWidth(locusInfo, maxLocusWidth);

                    graphics.drawString(font, locusInfo, px + INNER_PADDING + ELEMENT_SPACING, locusEntryY, locus.getSource().getColor().getColor(), false);
                    locusEntryY += lociLineHeight;
                }
                if (loci.size() > displayCount) {
                    graphics.drawString(font, "+" + (loci.size() - displayCount) + " more", px + INNER_PADDING + ELEMENT_SPACING, locusEntryY, 0xFF888888, false);
                    locusEntryY += lociLineHeight;
                }
                opsStartY = locusEntryY + ELEMENT_SPACING;
            } else {
                opsStartY = lociY + font.lineHeight + ELEMENT_SPACING;
            }
        }

        opsListX = px + INNER_PADDING;
        opsListY = opsStartY;
        opsListWidth = detailPanelWidth - INNER_PADDING * 2;
        int maxOpsListHeight = py + detailPanelHeight - opsStartY - INNER_PADDING;
        opsListHeight = Math.max(operationEntryHeight * 2, maxOpsListHeight);

        calculateOperationsScroll();
        renderOperationsList(graphics, opsListX, opsListY, opsListWidth, opsListHeight, mouseX, mouseY);
    }

    private void renderAlleleBox(GuiGraphics graphics, int bx, int by, int bw, int bh, String label, Allele allele, boolean selected, int mouseX, int mouseY) {
        boolean hovered = mouseX >= bx && mouseX <= bx + bw && mouseY >= by && mouseY <= by + bh;

        int bgColor = selected ? 0xFF2A2A5E : (hovered ? 0xFF252550 : 0xFF1E1E40);
        graphics.fill(bx, by, bx + bw, by + bh, bgColor);
        graphics.renderOutline(bx, by, bw, bh, selected ? 0xFF8888FF : 0xFF444488);

        int textX = bx + ELEMENT_SPACING;
        int textWidth = bw - ELEMENT_SPACING * 2;
        int currentY = by + ELEMENT_SPACING;

        graphics.drawString(font, label, textX, currentY, 0xFFAAAAFF, false);
        currentY += alleleLineHeight;

        String valueStr;
        if (allele.getValueHolder() instanceof FloatAlleleValue fav) {
            valueStr = String.format("%.3f", fav.get());
        } else {
            valueStr = allele.getValueHolder().format().getString();
        }
        valueStr = truncateToWidth(valueStr, textWidth);
        graphics.drawString(font, valueStr, textX, currentY, 0xFFFFFFFF, false);
        currentY += alleleLineHeight;

        Dominance dom = allele.getDominance();
        int domColor = switch (dom) {
            case DOMINANT -> 0xFF44FF44;
            case RECESSIVE -> 0xFFFF4444;
            case CO_DOMINANT -> 0xFFFFFF44;
            case INCOMPLETE -> 0xFF44FFFF;
        };

        String domStr = truncateToWidth(dom.name(), textWidth);
        graphics.drawString(font, domStr, textX, currentY, domColor, false);
        currentY += alleleLineHeight;

        String mutStr = String.format("M:  %.1f%%", allele.getMutationRate() * 100);
        mutStr = truncateToWidth(mutStr, textWidth);
        graphics.drawString(font, mutStr, textX, currentY, 0xFF888888, false);
    }

    private void renderOperationsList(GuiGraphics graphics, int ox, int oy, int ow, int oh, int mouseX, int mouseY) {
        graphics.fill(ox, oy, ox + ow, oy + oh, 0x44000000);
        graphics.renderOutline(ox, oy, ow, oh, 0xFF3A3A6A);

        graphics.drawString(font, "Ops:", ox + ELEMENT_SPACING, oy + (opsHeaderHeight - font.lineHeight) / 2, 0xFFAAAAFF, false);

        int contentX = ox + ELEMENT_SPACING;
        int contentY = oy + opsHeaderHeight;
        int contentWidth = ow - SCROLLBAR_WIDTH - 2 - ELEMENT_SPACING * 2;
        int contentHeight = getOpsContentHeight();

        if (contentHeight <= 0) return;

        enableScissor(graphics, contentX, contentY, contentWidth, contentHeight);

        int entryY = contentY - scrollOffsetOps * operationEntryHeight;

        for (GeneEditorOperation op : GeneEditorOperation.values()) {
            int entryTop = entryY;
            int entryBottom = entryY + operationEntryHeight - 1;

            if (entryBottom > contentY && entryTop < contentY + contentHeight) {
                boolean canPerform = GeneEditorOperations.canPerformOperation(player, op);
                boolean opSelected = state.getCurrentOperation() == op;
                boolean opHovered = mouseX >= contentX && mouseX <= contentX + contentWidth &&
                        mouseY >= Math.max(contentY, entryTop) && mouseY < Math.min(contentY + contentHeight, entryBottom);

                int bgColor = !canPerform ? 0xFF151520 : (opSelected ? 0xFF3A3A8A : (opHovered ? 0xFF2A2A5A : 0xFF1E1E40));

                int clippedTop = Math.max(contentY, entryTop);
                int clippedBottom = Math.min(contentY + contentHeight, entryBottom);
                graphics.fill(contentX, clippedTop, contentX + contentWidth, clippedBottom, bgColor);

                if (opSelected && clippedBottom - clippedTop > 2) {
                    graphics.renderOutline(contentX, clippedTop, contentWidth, clippedBottom - clippedTop, 0xFF6666FF);
                }

                int textY = entryY + (operationEntryHeight - font.lineHeight) / 2;
                if (textY >= contentY && textY + font.lineHeight <= contentY + contentHeight) {
                    int textColor = !canPerform ? 0xFF555555 : (opSelected ? 0xFFFFFF44 : 0xFFDDDDDD);
                    int lockIconWidth = canPerform ? 0 : font.width("X") + ELEMENT_SPACING;
                    int maxOpWidth = contentWidth - ELEMENT_SPACING * 2 - lockIconWidth;
                    String opName = truncateToWidth(op.getDisplayName().getString(), maxOpWidth);
                    graphics.drawString(font, opName, contentX + ELEMENT_SPACING, textY, textColor, false);

                    if (!canPerform) {
                        graphics.drawString(font, "X", contentX + contentWidth - font.width("X") - ELEMENT_SPACING, textY, 0xFFFF4444, false);
                    }
                }
            }

            entryY += operationEntryHeight;
        }

        disableScissor(graphics);

        int scrollbarX = ox + ow - SCROLLBAR_WIDTH - ELEMENT_SPACING;
        renderScrollbar(graphics, scrollbarX, contentY, SCROLLBAR_WIDTH, contentHeight, maxScrollOps, scrollOffsetOps, mouseX, mouseY, draggingScrollbarOps, operationEntryHeight);
    }

    private String truncateToWidth(String text, int maxWidth) {
        if (maxWidth <= 0) return "";
        if (font.width(text) <= maxWidth) return text;

        String ellipsis = ".. ";
        int ellipsisWidth = font.width(ellipsis);

        if (maxWidth <= ellipsisWidth) {
            return text.substring(0, Math.min(1, text.length()));
        }

        int targetWidth = maxWidth - ellipsisWidth;
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (font.width(sb.toString() + c) > targetWidth) break;
            sb.append(c);
        }
        return sb + ellipsis;
    }

    private String formatGeneValue(Gene gene) {
        if (gene.getExpressedValueHolder() instanceof FloatAlleleValue fav) {
            return String.format("%.3f", fav.get());
        }
        return gene.getExpressedValueHolder().format().getString();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            onClose();
            return true;
        }

        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            executeOperation();
            return true;
        }

        if (keyCode == GLFW.GLFW_KEY_R && (modifiers & GLFW.GLFW_MOD_CONTROL) != 0) {
            resetChanges();
            return true;
        }

        if (keyCode == GLFW.GLFW_KEY_TAB) {
            cycleSelection((modifiers & GLFW.GLFW_MOD_SHIFT) != 0);
            return true;
        }

        if (keyCode == GLFW.GLFW_KEY_UP) {
            navigateOperations(-1);
            return true;
        }

        if (keyCode == GLFW.GLFW_KEY_DOWN) {
            navigateOperations(1);
            return true;
        }

        if (keyCode == GLFW.GLFW_KEY_A && (modifiers & GLFW.GLFW_MOD_CONTROL) != 0) {
            selectAlleleA();
            return true;
        }

        if (keyCode == GLFW.GLFW_KEY_B && (modifiers & GLFW.GLFW_MOD_CONTROL) != 0) {
            selectAlleleB();
            return true;
        }

        if (keyCode >= GLFW.GLFW_KEY_1 && keyCode <= GLFW.GLFW_KEY_9) {
            int opIndex = keyCode - GLFW.GLFW_KEY_1;
            selectOperationByIndex(opIndex);
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void cycleSelection(boolean reverse) {
        List<Map.Entry<Trait, Gene>> allGenesA = new ArrayList<>(genesA.entrySet());
        List<Map.Entry<Trait, Gene>> allGenesB = new ArrayList<>(genesB.entrySet());

        if (allGenesA.isEmpty() && allGenesB.isEmpty()) return;

        if (selectionA == null && selectionB == null) {
            if (!allGenesA.isEmpty()) {
                Map.Entry<Trait, Gene> first = allGenesA.get(0);
                selectionA = new GeneSelection(first.getKey(), first.getValue());
                state.selectGeneTop(first.getValue(), 0);
            }
            return;
        }

        if (selectionA != null) {
            int currentIndex = getGeneIndex(genesA, selectionA.trait);
            int nextIndex = reverse ? currentIndex - 1 : currentIndex + 1;

            if (nextIndex >= 0 && nextIndex < allGenesA.size()) {
                Map.Entry<Trait, Gene> next = allGenesA.get(nextIndex);
                selectionA = new GeneSelection(next.getKey(), next.getValue());
                state.selectGeneTop(next.getValue(), nextIndex);
            } else if (!reverse && !allGenesB.isEmpty()) {
                selectionA = null;
                Map.Entry<Trait, Gene> first = allGenesB.get(0);
                selectionB = new GeneSelection(first.getKey(), first.getValue());
                state.selectGeneBottom(first.getValue(), 0);
            }
        } else if (selectionB != null) {
            int currentIndex = getGeneIndex(genesB, selectionB.trait);
            int nextIndex = reverse ? currentIndex - 1 : currentIndex + 1;

            if (nextIndex >= 0 && nextIndex < allGenesB.size()) {
                Map.Entry<Trait, Gene> next = allGenesB.get(nextIndex);
                selectionB = new GeneSelection(next.getKey(), next.getValue());
                state.selectGeneBottom(next.getValue(), nextIndex);
            } else if (reverse && !allGenesA.isEmpty()) {
                selectionB = null;
                Map.Entry<Trait, Gene> last = allGenesA.get(allGenesA.size() - 1);
                selectionA = new GeneSelection(last.getKey(), last.getValue());
                state.selectGeneTop(last.getValue(), allGenesA.size() - 1);
            }
        }

        state.clearAlleleSelection();
        player.playSound(SoundEvents.UI_BUTTON_CLICK.get(), 0.3f, 1.0f);
    }

    private void navigateOperations(int direction) {
        GeneEditorOperation[] ops = GeneEditorOperation.values();
        GeneEditorOperation current = state.getCurrentOperation();

        int currentIndex = 0;
        for (int i = 0; i < ops.length; i++) {
            if (ops[i] == current) {
                currentIndex = i;
                break;
            }
        }

        int newIndex = currentIndex + direction;
        if (newIndex < 0) newIndex = ops.length - 1;
        if (newIndex >= ops.length) newIndex = 0;

        state.setCurrentOperation(ops[newIndex]);

        int opsContentHeight = getOpsContentHeight();
        int visibleOps = Math.max(1, opsContentHeight / operationEntryHeight);
        if (newIndex < scrollOffsetOps) {
            scrollOffsetOps = newIndex;
        } else if (newIndex >= scrollOffsetOps + visibleOps) {
            scrollOffsetOps = newIndex - visibleOps + 1;
        }

        player.playSound(SoundEvents.UI_BUTTON_CLICK.get(), 0.3f, 1.2f);
    }

    private void selectAlleleA() {
        Gene selectedGene = selectionA != null ? selectionA.gene : (selectionB != null ? selectionB.gene : null);
        if (selectedGene != null) {
            state.selectAllele(selectedGene.getAlleleA(), true, selectionA != null);
            player.playSound(SoundEvents.UI_BUTTON_CLICK.get(), 0.3f, 1.2f);
        }
    }

    private void selectAlleleB() {
        Gene selectedGene = selectionA != null ? selectionA.gene : (selectionB != null ? selectionB.gene : null);
        if (selectedGene != null) {
            state.selectAllele(selectedGene.getAlleleB(), false, selectionA != null);
            player.playSound(SoundEvents.UI_BUTTON_CLICK.get(), 0.3f, 1.2f);
        }
    }

    private void selectOperationByIndex(int index) {
        GeneEditorOperation[] ops = GeneEditorOperation.values();
        if (index >= 0 && index < ops.length) {
            if (GeneEditorOperations.canPerformOperation(player, ops[index])) {
                state.setCurrentOperation(ops[index]);
                player.playSound(SoundEvents.UI_BUTTON_CLICK.get(), 0.3f, 1.0f);
            } else {
                addStatusMessage(Component.translatable("gui.wildaside.gene_editor.skill_required").withStyle(ChatFormatting.RED));
                player.playSound(SoundEvents.VILLAGER_NO, 0.3f, 1.0f);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) return true;

        if (button == 0) {
            if (handleScrollbarClick(mouseX, mouseY)) return true;

            int dnaPanelX = leftPos + OUTER_PADDING;
            int dnaPanelAY = topPos + headerHeight + OUTER_PADDING;
            int dnaPanelBY = dnaPanelAY + dnaPanelHeight + OUTER_PADDING;

            if (handleDnaPanelClick(mouseX, mouseY, dnaPanelX, dnaPanelAY, genesA, true)) return true;
            if (handleDnaPanelClick(mouseX, mouseY, dnaPanelX, dnaPanelBY, genesB, false)) return true;
            if (handleDetailPanelClick(mouseX, mouseY)) return true;
        }

        return false;
    }

    private boolean handleScrollbarClick(double mouseX, double mouseY) {
        int dnaPanelX = leftPos + OUTER_PADDING;
        int dnaPanelAY = topPos + headerHeight + OUTER_PADDING;
        int dnaPanelBY = dnaPanelAY + dnaPanelHeight + OUTER_PADDING;

        int scrollbarX = dnaPanelX + dnaPanelWidth - SCROLLBAR_WIDTH - INNER_PADDING;
        int contentYOffset = dnaPanelHeaderHeight;
        int contentHeight = getDnaPanelContentHeight();

        if (mouseX >= scrollbarX && mouseX <= scrollbarX + SCROLLBAR_WIDTH) {
            if (mouseY >= dnaPanelAY + contentYOffset && mouseY <= dnaPanelAY + contentYOffset + contentHeight && maxScrollA > 0) {
                draggingScrollbarA = true;
                dragStartY = mouseY;
                dragStartScroll = scrollOffsetA;
                return true;
            }

            if (mouseY >= dnaPanelBY + contentYOffset && mouseY <= dnaPanelBY + contentYOffset + contentHeight && maxScrollB > 0) {
                draggingScrollbarB = true;
                dragStartY = mouseY;
                dragStartScroll = scrollOffsetB;
                return true;
            }
        }

        if (opsListWidth > 0 && opsListHeight > 0) {
            int opsScrollbarX = opsListX + opsListWidth - SCROLLBAR_WIDTH - ELEMENT_SPACING;
            int opsContentY = opsListY + opsHeaderHeight;
            int opsContentHeight = getOpsContentHeight();

            if (mouseX >= opsScrollbarX && mouseX <= opsScrollbarX + SCROLLBAR_WIDTH &&
                    mouseY >= opsContentY && mouseY <= opsContentY + opsContentHeight && maxScrollOps > 0) {
                draggingScrollbarOps = true;
                dragStartY = mouseY;
                dragStartScroll = scrollOffsetOps;
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (button == 0) {
            if (draggingScrollbarA && maxScrollA > 0) {
                int contentHeight = getDnaPanelContentHeight();
                int totalContentHeight = maxScrollA * geneEntryHeight + contentHeight;
                int thumbHeight = Math.max(SCROLLBAR_MIN_THUMB_HEIGHT, contentHeight * contentHeight / totalContentHeight);
                int scrollRange = contentHeight - thumbHeight - 4;

                if (scrollRange > 0) {
                    double deltaY = mouseY - dragStartY;
                    int scrollDelta = (int) (deltaY * maxScrollA / scrollRange);
                    scrollOffsetA = Math.max(0, Math.min(maxScrollA, dragStartScroll + scrollDelta));
                }
                return true;
            }

            if (draggingScrollbarB && maxScrollB > 0) {
                int contentHeight = getDnaPanelContentHeight();
                int totalContentHeight = maxScrollB * geneEntryHeight + contentHeight;
                int thumbHeight = Math.max(SCROLLBAR_MIN_THUMB_HEIGHT, contentHeight * contentHeight / totalContentHeight);
                int scrollRange = contentHeight - thumbHeight - 4;

                if (scrollRange > 0) {
                    double deltaY = mouseY - dragStartY;
                    int scrollDelta = (int) (deltaY * maxScrollB / scrollRange);
                    scrollOffsetB = Math.max(0, Math.min(maxScrollB, dragStartScroll + scrollDelta));
                }
                return true;
            }

            if (draggingScrollbarOps && maxScrollOps > 0) {
                int contentHeight = getOpsContentHeight();
                int totalContentHeight = maxScrollOps * operationEntryHeight + contentHeight;
                int thumbHeight = Math.max(SCROLLBAR_MIN_THUMB_HEIGHT, contentHeight * contentHeight / totalContentHeight);
                int scrollRange = contentHeight - thumbHeight - 4;

                if (scrollRange > 0) {
                    double deltaY = mouseY - dragStartY;
                    int scrollDelta = (int) (deltaY * maxScrollOps / scrollRange);
                    scrollOffsetOps = Math.max(0, Math.min(maxScrollOps, dragStartScroll + scrollDelta));
                }
                return true;
            }
        }

        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            draggingScrollbarA = false;
            draggingScrollbarB = false;
            draggingScrollbarOps = false;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private boolean handleDnaPanelClick(double mouseX, double mouseY, int px, int py, Map<Trait, Gene> genes, boolean isTop) {
        int contentX = px + INNER_PADDING;
        int contentY = py + dnaPanelHeaderHeight;
        int contentWidth = dnaPanelWidth - INNER_PADDING * 2 - SCROLLBAR_WIDTH - 2;
        int contentHeight = getDnaPanelContentHeight();

        if (mouseX < contentX || mouseX > contentX + contentWidth || mouseY < contentY || mouseY > contentY + contentHeight) {
            return false;
        }

        int scrollOffset = isTop ? scrollOffsetA : scrollOffsetB;
        int entryY = contentY - scrollOffset * geneEntryHeight;
        TraitType currentType = null;

        for (Map.Entry<Trait, Gene> entry : genes.entrySet()) {
            Trait trait = entry.getKey();
            Gene gene = entry.getValue();

            if (trait.getTraitType() != currentType) {
                currentType = trait.getTraitType();
                entryY += traitTypeHeaderHeight;
            }

            int entryTop = Math.max(contentY, entryY);
            int entryBottom = Math.min(contentY + contentHeight, entryY + geneEntryHeight);

            if (mouseY >= entryTop && mouseY < entryBottom && entryBottom > entryTop) {
                if (isTop) {
                    selectionA = new GeneSelection(trait, gene);
                    state.selectGeneTop(gene, getGeneIndex(genes, trait));
                } else {
                    selectionB = new GeneSelection(trait, gene);
                    state.selectGeneBottom(gene, getGeneIndex(genes, trait));
                }
                state.clearAlleleSelection();
                player.playSound(SoundEvents.UI_BUTTON_CLICK.get(), 0.5f, 1.0f);
                return true;
            }

            entryY += geneEntryHeight;
        }

        return false;
    }

    private boolean handleDetailPanelClick(double mouseX, double mouseY) {
        int px = leftPos + OUTER_PADDING + dnaPanelWidth + OUTER_PADDING;
        int py = topPos + headerHeight + OUTER_PADDING;

        Gene selectedGene = selectionA != null ? selectionA.gene : (selectionB != null ? selectionB.gene : null);

        if (handleOperationsClick(mouseX, mouseY)) return true;

        if (selectedGene == null) return false;

        int traitHeaderY = py + INNER_PADDING + font.lineHeight + ELEMENT_SPACING;
        int alleleY = traitHeaderY + traitHeaderHeight + ELEMENT_SPACING + font.lineHeight + ELEMENT_SPACING;
        int alleleBoxWidth = (detailPanelWidth - INNER_PADDING * 2 - ELEMENT_SPACING) / 2;

        int alleleAX = px + INNER_PADDING;
        int alleleBX = px + INNER_PADDING + alleleBoxWidth + ELEMENT_SPACING;

        if (mouseX >= alleleAX && mouseX <= alleleAX + alleleBoxWidth && mouseY >= alleleY && mouseY <= alleleY + alleleBoxHeight) {
            state.selectAllele(selectedGene.getAlleleA(), true, selectionA != null);
            player.playSound(SoundEvents.UI_BUTTON_CLICK.get(), 0.5f, 1.2f);
            return true;
        }

        if (mouseX >= alleleBX && mouseX <= alleleBX + alleleBoxWidth && mouseY >= alleleY && mouseY <= alleleY + alleleBoxHeight) {
            state.selectAllele(selectedGene.getAlleleB(), false, selectionA != null);
            player.playSound(SoundEvents.UI_BUTTON_CLICK.get(), 0.5f, 1.2f);
            return true;
        }

        return false;
    }

    private boolean handleOperationsClick(double mouseX, double mouseY) {
        if (opsListWidth <= 0 || opsListHeight <= 0) return false;

        int contentX = opsListX + ELEMENT_SPACING;
        int contentY = opsListY + opsHeaderHeight;
        int contentWidth = opsListWidth - SCROLLBAR_WIDTH - 2 - ELEMENT_SPACING * 2;
        int contentHeight = getOpsContentHeight();

        if (mouseX < contentX || mouseX > contentX + contentWidth || mouseY < contentY || mouseY > contentY + contentHeight) {
            return false;
        }

        int entryY = contentY - scrollOffsetOps * operationEntryHeight;

        for (GeneEditorOperation op : GeneEditorOperation.values()) {
            int entryTop = Math.max(contentY, entryY);
            int entryBottom = Math.min(contentY + contentHeight, entryY + operationEntryHeight);

            if (mouseY >= entryTop && mouseY < entryBottom && entryBottom > entryTop) {
                if (GeneEditorOperations.canPerformOperation(player, op)) {
                    state.setCurrentOperation(op);
                    player.playSound(SoundEvents.UI_BUTTON_CLICK.get(), 0.5f, 1.0f);
                    return true;
                } else {
                    addStatusMessage(Component.translatable("gui.wildaside.gene_editor.skill_required")
                            .withStyle(ChatFormatting.RED));
                    player.playSound(SoundEvents.VILLAGER_NO, 0.5f, 1.0f);
                    return true;
                }
            }
            entryY += operationEntryHeight;
        }

        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        int dnaPanelX = leftPos + OUTER_PADDING;
        int dnaPanelAY = topPos + headerHeight + OUTER_PADDING;
        int dnaPanelBY = dnaPanelAY + dnaPanelHeight + OUTER_PADDING;

        if (mouseX >= dnaPanelX && mouseX <= dnaPanelX + dnaPanelWidth) {
            if (mouseY >= dnaPanelAY && mouseY <= dnaPanelAY + dnaPanelHeight) {
                scrollOffsetA = Math.max(0, Math.min(maxScrollA, scrollOffsetA - (int) delta));
                return true;
            }

            if (mouseY >= dnaPanelBY && mouseY <= dnaPanelBY + dnaPanelHeight) {
                scrollOffsetB = Math.max(0, Math.min(maxScrollB, scrollOffsetB - (int) delta));
                return true;
            }
        }

        if (opsListWidth > 0 && opsListHeight > 0 &&
                mouseX >= opsListX && mouseX <= opsListX + opsListWidth &&
                mouseY >= opsListY && mouseY <= opsListY + opsListHeight) {
            scrollOffsetOps = Math.max(0, Math.min(maxScrollOps, scrollOffsetOps - (int) delta));
            return true;
        }

        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    private void executeOperation() {
        GeneEditorOperation op = state.getCurrentOperation();

        if (dnaA == null && dnaB == null) {
            addStatusMessage(Component.translatable("gui.wildaside.gene_editor.need_both_dna").withStyle(ChatFormatting.RED));
            return;
        }

        if (!GeneEditorOperations.canPerformOperation(player, op)) {
            addStatusMessage(Component.translatable("gui.wildaside.gene_editor.skill_required").withStyle(ChatFormatting.RED));
            return;
        }

        GeneEditorResult result = null;

        switch (op) {
            case SWAP_TRAIT -> {
                if (selectionA == null || selectionB == null) {
                    addStatusMessage(Component.translatable("gui.wildaside.gene_editor.select_both").withStyle(ChatFormatting.YELLOW));
                    return;
                }
                if (!selectionA.trait.equals(selectionB.trait)) {
                    addStatusMessage(Component.translatable("gui.wildaside.gene_editor.traits_must_match").withStyle(ChatFormatting.RED));
                    return;
                }
                result = GeneEditorOperations.swapTrait(dnaA, state.getSelectedGeneTopIndex(),
                        dnaB, state.getSelectedGeneBottomIndex(),
                        new ArrayList<>(genesA.values()), new ArrayList<>(genesB.values()));
            }
            case SWAP_ALLELE -> {
                if (selectionA == null || selectionB == null || state.getSelectedAllele() == null) {
                    addStatusMessage(Component.translatable("gui.wildaside.gene_editor.select_allele").withStyle(ChatFormatting.YELLOW));
                    return;
                }
                if (!selectionA.trait.equals(selectionB.trait)) {
                    addStatusMessage(Component.translatable("gui.wildaside.gene_editor.traits_must_match").withStyle(ChatFormatting.RED));
                    return;
                }
                result = GeneEditorOperations.swapAllele(dnaA, selectionA.gene, state.isSelectedAlleleA(),
                        dnaB, selectionB.gene, !state.isSelectedAlleleA());
            }
            case STABILIZE -> {
                Gene target = selectionA != null ? selectionA.gene : selectionB.gene;
                DnaImplementation targetDna = selectionA != null ? dnaA : dnaB;
                if (target == null || targetDna == null) {
                    addStatusMessage(Component.translatable("gui.wildaside.gene_editor.select_gene_first").withStyle(ChatFormatting.YELLOW));
                    return;
                }
                result = GeneEditorOperations.stabilize(targetDna, target, 0.1f);
            }
            case AMPLIFY -> {
                Gene target = selectionA != null ? selectionA.gene : selectionB.gene;
                DnaImplementation targetDna = selectionA != null ? dnaA : dnaB;
                if (target == null || targetDna == null) {
                    addStatusMessage(Component.translatable("gui.wildaside.gene_editor.select_gene_first").withStyle(ChatFormatting.YELLOW));
                    return;
                }
                result = GeneEditorOperations.amplify(targetDna, target, 1.2f);
            }
            case SUPPRESS -> {
                Gene target = selectionA != null ? selectionA.gene : selectionB.gene;
                DnaImplementation targetDna = selectionA != null ? dnaA : dnaB;
                if (target == null || targetDna == null) {
                    addStatusMessage(Component.translatable("gui.wildaside.gene_editor.select_gene_first").withStyle(ChatFormatting.YELLOW));
                    return;
                }
                result = GeneEditorOperations.suppress(targetDna, target, 1.2f);
            }
            case MERGE_LOCI -> {
                if (selectionA == null || selectionB == null) {
                    addStatusMessage(Component.translatable("gui.wildaside.gene_editor.select_both").withStyle(ChatFormatting.YELLOW));
                    return;
                }
                result = GeneEditorOperations.mergeLoci(dnaA, selectionA.gene, dnaB, selectionB.gene);
            }
            case MODIFY_DOMINANCE -> {
                if (state.getSelectedAllele() == null) {
                    addStatusMessage(Component.translatable("gui.wildaside.gene_editor.select_allele").withStyle(ChatFormatting.YELLOW));
                    return;
                }
                Gene target = selectionA != null ? selectionA.gene : selectionB.gene;
                DnaImplementation targetDna = selectionA != null ? dnaA : dnaB;
                if (target == null || targetDna == null) {
                    addStatusMessage(Component.translatable("gui.wildaside.gene_editor.select_gene_first").withStyle(ChatFormatting.YELLOW));
                    return;
                }
                Dominance current = state.getSelectedAllele().getDominance();
                Dominance next = getNextDominance(current);
                result = GeneEditorOperations.modifyDominance(targetDna, target, state.isSelectedAlleleA(), next);
            }
            case ISOLATE_ALLELE -> {
                addStatusMessage(Component.literal("Isolate not yet implemented").withStyle(ChatFormatting.YELLOW));
                return;
            }
        }

        if (result != null) {
            if (result.isSuccess()) {
                addStatusMessage(Component.literal(result.getMessage()).withStyle(ChatFormatting.GREEN));
                player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 0.5f, 1.0f);
                rebuildGenes();

                selectionA = null;
                selectionB = null;
                state.clearSelection();

                NetworkHandler.sendGeneEditorUpdatePacket(workstationPos, dnaA, dnaB);
            } else {
                addStatusMessage(Component.literal(result.getMessage()).withStyle(ChatFormatting.RED));
                player.playSound(SoundEvents.VILLAGER_NO, 0.5f, 1.0f);
            }
        }
    }

    private Dominance getNextDominance(Dominance current) {
        return switch (current) {
            case DOMINANT -> Dominance.RECESSIVE;
            case RECESSIVE -> Dominance.CO_DOMINANT;
            case CO_DOMINANT -> Dominance.INCOMPLETE;
            case INCOMPLETE -> Dominance.DOMINANT;
        };
    }

    private void resetChanges() {
        state.clearSelection();
        selectionA = null;
        selectionB = null;
        scrollOffsetOps = 0;
        statusMessages.clear();
        addStatusMessage(Component.translatable("gui.wildaside.gene_editor.reset_done").withStyle(ChatFormatting.YELLOW));
        player.playSound(SoundEvents.UI_BUTTON_CLICK.get(), 0.5f, 0.8f);
    }

    private void addStatusMessage(Component message) {
        statusMessages.add(message);
        if (statusMessages.size() > 5) {
            statusMessages.remove(0);
        }
    }

    private int getGeneIndex(Map<Trait, Gene> genes, Trait trait) {
        int index = 0;
        for (Trait t : genes.keySet()) {
            if (t.equals(trait)) return index;
            index++;
        }
        return -1;
    }

    private void enableScissor(GuiGraphics graphics, int x, int y, int width, int height) {
        double guiScale = minecraft.getWindow().getGuiScale();
        int sx = (int) (x * guiScale);
        int sy = (int) ((this.height - (y + height)) * guiScale);
        int sw = (int) (width * guiScale);
        int sh = (int) (height * guiScale);
        RenderSystem.enableScissor(sx, sy, sw, sh);
    }

    private void disableScissor(GuiGraphics graphics) {
        RenderSystem.disableScissor();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private boolean hasGenomeSequences(Genome genome) {
        return DnaUtils.hasGenomeSequences(genome);
    }

    private record GeneSelection(Trait trait, Gene gene) {
    }
}