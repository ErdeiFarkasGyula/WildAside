package net.farkas.wildaside.screen.gene_editor;

import com.mojang.blaze3d.systems.RenderSystem;
import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.capability.dna.DnaImplementation;
import net.farkas.wildaside.client.ModKeyMappings;
import net.farkas.wildaside.dna.chromosome.ChromosomeSet;
import net.farkas.wildaside.dna.chromosome.Genome;
import net.farkas.wildaside.dna.expression.*;
import net.farkas.wildaside.dna.sequence.components.*;
import net.farkas.wildaside.dna.sequence.GeneSequence;
import net.farkas.wildaside.dna.trait.Trait;
import net.farkas.wildaside.dna.trait.TraitRegistry;
import net.farkas.wildaside.dna.trait.TraitType;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class AdvancedGeneEditorScreen extends Screen {
    private static final ResourceLocation GENE_TEXTURE = new ResourceLocation(WildAside.MOD_ID, "textures/item/gene.png");
    private static final ResourceLocation GENE_TEXTURE_FLIPPED = new ResourceLocation(WildAside.MOD_ID, "textures/item/gene_flipped.png");

    private static final int COL_BACKGROUND = 0xFF121212;
    private static final int COL_PANEL_BG = 0xCC1E1E1E;
    private static final int COL_BORDER = 0xFF2C2C2C;
    private static final int COL_ACCENT = 0xFF00E5FF;
    private static final int COL_TEXT_HEADER = 0xFFE0E0E0;
    private static final int COL_TEXT_NORMAL = 0xFFAAAAAA;
    private static final int COL_TEXT_VALUE = 0xFFFFFFFF;

    private static final int HEADER_HEIGHT = 24;
    private static final int FOOTER_HEIGHT = 24;

    private static final int STRIP_HEIGHT = 24;
    private static final int COMPONENT_SIZE = 16;
    private static final int COMPONENT_SPACING = 0;

    private int sidebarWidth = 150;
    private int infoPanelWidth = 160;

    private final BlockPos workstationPos;
    private final Player player;
    private final Genome genomeA;
    private final Genome genomeB;

    private final List<Trait> allTraits = new ArrayList<>();
    private final Map<TraitType, List<Trait>> traitsByType = new LinkedHashMap<>();

    private double scrollAMat = 0;
    private double scrollAPat = 0;
    private double scrollBMat = 0;
    private double scrollBPat = 0;

    private double targetScrollAMat = 0;
    private double targetScrollAPat = 0;
    private double targetScrollBMat = 0;
    private double targetScrollBPat = 0;

    private double sidebarScroll = 0;
    private double targetSidebarScroll = 0;
    private int sidebarContentHeight = 0;

    private double infoScrollA = 0;
    private double targetInfoScrollA = 0;
    private int infoContentHeightA = 0;

    private double infoScrollB = 0;
    private double targetInfoScrollB = 0;
    private int infoContentHeightB = 0;

    private boolean lockGenomeA = true;
    private boolean lockGenomeB = true;
    private boolean lockPairs = true;

    @Nullable
    private Object selectedComponentA = null;
    @Nullable
    private Object selectedComponentB = null;
    @Nullable
    private GeneSequence selectedSequenceA = null;
    @Nullable
    private GeneSequence selectedSequenceB = null;
    @Nullable
    private Trait selectedTraitA = null;
    @Nullable
    private Trait selectedTraitB = null;

    private boolean selectedIsGenomeA = true;
    private boolean selectedIsMaternal = true;

    private boolean selectedIsMaternalA = true;
    private boolean selectedIsMaternalB = true;

    @Nullable
    private Object draggedComponent = null;
    private boolean isDraggingView = false;
    private double lastMouseX;

    private boolean isResizingSidebar = false;
    private boolean isResizingInfoPanel = false;

    private boolean isDraggingSidebarScroll = false;
    private boolean isDraggingInfoScrollA = false;
    private boolean isDraggingInfoScrollB = false;
    private double scrollDragStartY;
    private double scrollDragStartValue;

    private int stripStartX;
    private int stripWidth;
    private int stripAY1, stripAY2;
    private int stripBY1, stripBY2;

    private final List<Component> statusMessages = new ArrayList<>();

    @Nullable
    private GeneSequence hoveredSequence = null;
    @Nullable
    private Trait hoveredTrait = null;

    public AdvancedGeneEditorScreen(BlockPos workstationPos, DnaImplementation dnaA, DnaImplementation dnaB) {
        super(Component.translatable("gui.wildaside.advanced_gene_editor"));
        this.workstationPos = workstationPos;
        this.player = Minecraft.getInstance().player;

        this.genomeA = (dnaA != null && dnaA.getGenome() != null) ? dnaA.getGenome() : new Genome(null);
        this.genomeB = (dnaB != null && dnaB.getGenome() != null) ? dnaB.getGenome() : new Genome(null);

        for (TraitType type : TraitType.values()) {
            List<Trait> traits = TraitRegistry.getAllTraits().stream()
                    .filter(t -> t.getTraitType() == type)
                    .filter(this::isTraitPresent)
                    .sorted(Comparator.comparing(Trait::getName))
                    .collect(Collectors.toList());

            if (!traits.isEmpty()) {
                traitsByType.put(type, traits);
                this.allTraits.addAll(traits);
            }
        }
    }

    private boolean isTraitPresent(Trait trait) {
        return hasTrait(genomeA, trait) || hasTrait(genomeB, trait);
    }

    private boolean hasTrait(Genome genome, Trait trait) {
        if (genome == null) return false;
        if (genome.getMaternal() != null && genome.getMaternal().getSequence(trait) != null) return true;
        if (genome.getPaternal() != null && genome.getPaternal().getSequence(trait) != null) return true;
        return false;
    }

    @Override
    protected void init() {
        super.init();
        updateLayout();
    }

    private void updateLayout() {
        this.stripStartX = sidebarWidth + 10;
        this.stripWidth = width - sidebarWidth - infoPanelWidth - 20;

        int centerY = height / 2;
        int spacing = 45;

        this.stripAY1 = centerY - spacing - 28;
        this.stripAY2 = centerY - 28;

        this.stripBY1 = centerY + 28;
        this.stripBY2 = centerY + spacing + 28;

        clearWidgets();

        addRenderableWidget(Button.builder(Component.translatable("gui.wildaside.gene_editor.exit"), b -> onClose())
                .pos(width - 60, 2)
                .size(50, 20)
                .build());

        int btnX = stripStartX + stripWidth - 20;
        int btnY_A = (stripAY1 + stripAY2 + COMPONENT_SIZE) / 2 - 10;
        addRenderableWidget(Button.builder(Component.literal(lockGenomeA ? "L" : "U"), b -> {
            lockGenomeA = !lockGenomeA;
            b.setMessage(Component.literal(lockGenomeA ? "L" : "U"));
            updateLayout();
        }).pos(btnX, btnY_A).size(20, 20).tooltip(Tooltip.create(Component.translatable("gui.wildaside.gene_editor.lock_genome", lockGenomeA))).build());

        int btnY_B = (stripBY1 + stripBY2 + COMPONENT_SIZE) / 2 - 10;
        addRenderableWidget(Button.builder(Component.literal(lockGenomeB ? "L" : "U"), b -> {
            lockGenomeB = !lockGenomeB;
            b.setMessage(Component.literal(lockGenomeB ? "L" : "U"));
            updateLayout();
        }).pos(btnX, btnY_B).size(20, 20).tooltip(Tooltip.create(Component.translatable("gui.wildaside.gene_editor.lock_genome", lockGenomeB))).build());

        addRenderableWidget(Button.builder(Component.literal(lockPairs ? "L" : "U"), b -> {
            lockPairs = !lockPairs;
            b.setMessage(Component.literal(lockPairs ? "L" : "U"));
            updateLayout();
        }).pos(width - 120, 2).size(20, 20).tooltip(Tooltip.create(Component.translatable("gui.wildaside.gene_editor.lock_pair", lockPairs))).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);

        double smoothFactor = 0.3;
        scrollAMat = lerp(scrollAMat, targetScrollAMat, smoothFactor);
        scrollAPat = lerp(scrollAPat, targetScrollAPat, smoothFactor);
        scrollBMat = lerp(scrollBMat, targetScrollBMat, smoothFactor);
        scrollBPat = lerp(scrollBPat, targetScrollBPat, smoothFactor);

        if (!isDraggingSidebarScroll) sidebarScroll = lerp(sidebarScroll, targetSidebarScroll, smoothFactor);
        if (!isDraggingInfoScrollA) infoScrollA = lerp(infoScrollA, targetInfoScrollA, smoothFactor);
        if (!isDraggingInfoScrollB) infoScrollB = lerp(infoScrollB, targetInfoScrollB, smoothFactor);

        updateHoveredSequence(mouseX, mouseY);

        renderSidebar(graphics, mouseX, mouseY);

        enableScissor(graphics, stripStartX, HEADER_HEIGHT, stripWidth - 25, height - HEADER_HEIGHT - FOOTER_HEIGHT);

        String nameA = getEntityName(genomeA);
        String nameB = getEntityName(genomeB);

        renderGeneStrip(graphics, genomeA, true, stripAY1, scrollAMat, mouseX, mouseY, nameA, Component.translatable("gui.wildaside.gene_editor.maternal"));
        renderGeneStrip(graphics, genomeA, false, stripAY2, scrollAPat, mouseX, mouseY, nameA, Component.translatable("gui.wildaside.gene_editor.paternal"));

        renderGeneStrip(graphics, genomeB, true, stripBY1, scrollBMat, mouseX, mouseY, nameB, Component.translatable("gui.wildaside.gene_editor.maternal"));
        renderGeneStrip(graphics, genomeB, false, stripBY2, scrollBPat, mouseX, mouseY, nameB, Component.translatable("gui.wildaside.gene_editor.paternal"));

        disableScissor(graphics);

        renderInfoPanel(graphics, mouseX, mouseY);

        renderResizeHandles(graphics, mouseX, mouseY);

        if (draggedNode != null) {
            renderDraggedNode(graphics, mouseX, mouseY);
        }

        if (hoveredNode != null && draggedNode == null) {
            renderNodeTooltip(graphics, hoveredNode, mouseX, mouseY);
        }

        renderFooter(graphics);

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    private double lerp(double start, double end, double delta) {
        return start + (end - start) * delta;
    }

    private String getEntityName(Genome genome) {
        if (genome == null || genome.getEntityType() == null) return "Unknown";
        return genome.getEntityType().getDescription().getString();
    }

    private void renderResizeHandles(GuiGraphics graphics, int mouseX, int mouseY) {
        int leftHandleX = sidebarWidth;
        boolean hoverLeft = Math.abs(mouseX - leftHandleX) <= 2;
        graphics.fill(leftHandleX - 1, HEADER_HEIGHT, leftHandleX + 1, height - FOOTER_HEIGHT, (hoverLeft || isResizingSidebar) ? COL_ACCENT : COL_BORDER);

        int rightHandleX = width - infoPanelWidth;
        boolean hoverRight = Math.abs(mouseX - rightHandleX) <= 2;
        graphics.fill(rightHandleX - 1, HEADER_HEIGHT, rightHandleX + 1, height - FOOTER_HEIGHT, (hoverRight || isResizingInfoPanel) ? COL_ACCENT : COL_BORDER);
    }

    private void updateHoveredSequence(int mouseX, int mouseY) {
        hoveredSequence = null;
        hoveredNode = null;
        hoveredTrait = null;

        if (mouseX < stripStartX || mouseX > stripStartX + stripWidth) return;

        if (mouseY >= stripAY1 && mouseY <= stripAY1 + STRIP_HEIGHT + COMPONENT_SIZE) {
            checkHoverInGenome(genomeA, true, scrollAMat, mouseX, mouseY, stripAY1);
        } else if (mouseY >= stripAY2 && mouseY <= stripAY2 + STRIP_HEIGHT + COMPONENT_SIZE) {
            checkHoverInGenome(genomeA, false, scrollAPat, mouseX, mouseY, stripAY2);
        } else if (mouseY >= stripBY1 && mouseY <= stripBY1 + STRIP_HEIGHT + COMPONENT_SIZE) {
            checkHoverInGenome(genomeB, true, scrollBMat, mouseX, mouseY, stripBY1);
        } else if (mouseY >= stripBY2 && mouseY <= stripBY2 + STRIP_HEIGHT + COMPONENT_SIZE) {
            checkHoverInGenome(genomeB, false, scrollBPat, mouseX, mouseY, stripBY2);
        }
    }

    private void checkHoverInGenome(Genome genome, boolean isMaternal, double scroll, int mouseX, int mouseY, int y) {
        if (genome == null) return;

        boolean checkRow = mouseY >= y && mouseY <= y + COMPONENT_SIZE;
        if (!checkRow) return;

        ChromosomeSet chromSet = isMaternal ? genome.getMaternal() : genome.getPaternal();
        if (chromSet == null) return;

        double currentX = 0;
        int globalComponentIndex = 0;

        for (Trait trait : allTraits) {
            GeneSequence seq = chromSet.getSequence(trait);
            if (seq == null) continue;

            List<Object> components = getAllComponents(seq);
            int width = components.size() * (COMPONENT_SIZE + COMPONENT_SPACING);

            int xBase = (int) (stripStartX + currentX - scroll * (COMPONENT_SIZE + COMPONENT_SPACING));

            int margin = 4;
            if (mouseX >= xBase - margin && mouseX < xBase + width + margin) {
                hoveredSequence = seq;
                hoveredTrait = trait;

                int index = (mouseX - xBase) / (COMPONENT_SIZE + COMPONENT_SPACING);
                if (index >= 0 && index < components.size()) {
                    Object comp = components.get(index);
                    int nodeX = xBase + index * (COMPONENT_SIZE + COMPONENT_SPACING);
                    boolean flipped = (globalComponentIndex + index) % 2 != 0;
                    hoveredNode = new VisualNode(comp, trait, seq, isMaternal, genome == genomeA, nodeX, y, flipped);
                }
                return;
            }

            currentX += width;
            globalComponentIndex += components.size();
        }
    }

    private void renderSidebar(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.fill(0, HEADER_HEIGHT, sidebarWidth, height - FOOTER_HEIGHT, COL_PANEL_BG);
        graphics.vLine(sidebarWidth, HEADER_HEIGHT, height - FOOTER_HEIGHT, COL_BORDER);

        int visibleHeight = height - HEADER_HEIGHT - FOOTER_HEIGHT;

        enableScissor(graphics, 0, HEADER_HEIGHT, sidebarWidth, visibleHeight);

        int y = (int) (HEADER_HEIGHT + 5 - sidebarScroll);
        int startY = y;

        for (Map.Entry<TraitType, List<Trait>> entry : traitsByType.entrySet()) {
            graphics.drawString(font, Component.translatable("trait_type.wildaside." + entry.getKey().name().toLowerCase()), 5, y, entry.getKey().getHeaderColour().getColor(), false);
            y += 14;

            for (Trait trait : entry.getValue()) {
                boolean isHovered = mouseX >= 0 && mouseX < sidebarWidth && mouseY >= y && mouseY < y + 12;
                int color = COL_TEXT_NORMAL;
                if (isHovered) color = 0xFFFFFFFF;
                if (selectedTraitA == trait || selectedTraitB == trait) color = COL_ACCENT;

                graphics.drawString(font, truncate(Component.translatable("trait.wildaside." + trait.getName()).getString(), sidebarWidth - 10), 10, y, color, false);
                y += 14;
            }
            y += 8;
        }

        sidebarContentHeight = y - startY + (int) sidebarScroll;
        disableScissor(graphics);

        renderScrollbar(graphics, sidebarWidth - 4, HEADER_HEIGHT, visibleHeight, sidebarContentHeight, sidebarScroll);
    }

    private void renderGeneStrip(GuiGraphics graphics, Genome genome, boolean isMaternal, int y, double scroll, int mouseX, int mouseY, String entityName, Component suffix) {
        Component label = Component.literal(entityName + " (").append(suffix).append(")");
        graphics.drawString(font, label, stripStartX, y - 10, COL_TEXT_NORMAL, false);

        graphics.fill(stripStartX, y, stripStartX + stripWidth - 25, y + STRIP_HEIGHT, 0xFF000000);
        graphics.renderOutline(stripStartX - 1, y - 1, stripWidth - 25 + 2, STRIP_HEIGHT + 2, COL_BORDER);

        if (genome == null) return;

        ChromosomeSet chromSet = isMaternal ? genome.getMaternal() : genome.getPaternal();
        if (chromSet == null) return;

        double currentX = 0;
        int globalComponentIndex = 0;

        for (Trait trait : allTraits) {
            GeneSequence seq = chromSet.getSequence(trait);
            if (seq == null) continue;

            List<Object> components = getAllComponents(seq);
            int width = components.size() * (COMPONENT_SIZE + COMPONENT_SPACING);

            int xBase = (int) (stripStartX + currentX - scroll * (COMPONENT_SIZE + COMPONENT_SPACING));

            if (selectedTraitA == trait || selectedTraitB == trait) {
                if (xBase + 6 >= stripStartX && xBase <= stripStartX + stripWidth) {
                    graphics.fill(xBase, y + STRIP_HEIGHT + 2, xBase + 6, y + STRIP_HEIGHT + 4, COL_ACCENT);
                }
            }

            if (hoveredSequence == seq && !components.isEmpty()) {
                int seqWidth = components.size() * (COMPONENT_SIZE + COMPONENT_SPACING);
                graphics.renderOutline(xBase - 1, y + 3, seqWidth + 2, COMPONENT_SIZE + 2, 0x88FFFFFF);
            }

            if (hoveredTrait == trait && seq != null && !components.isEmpty()) {
                int seqWidth = components.size() * (COMPONENT_SIZE + COMPONENT_SPACING);
                graphics.renderOutline(xBase - 1, y + 3, seqWidth + 2, COMPONENT_SIZE + 2, 0x44FFFFFF);
            }

            for (int i = 0; i < components.size(); i++) {
                int x = xBase + i * (COMPONENT_SIZE + COMPONENT_SPACING);
                if (x + COMPONENT_SIZE < stripStartX || x > stripStartX + stripWidth) continue;

                Object comp = components.get(i);
                renderComponentNode(graphics, x, y + 4, comp, trait, seq, isMaternal, genome == genomeA, mouseX, mouseY, (globalComponentIndex + i) % 2 != 0);
            }

            currentX += width;
            globalComponentIndex += components.size();
        }
    }

    private void renderComponentNode(GuiGraphics graphics, int x, int y, Object comp, Trait trait, GeneSequence seq, boolean isMaternal, boolean isGenomeA, int mouseX, int mouseY, boolean flipped) {
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        int color = getComponentColor(comp);

        if (comp instanceof CodingRegion) {
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        } else {
            float r = ((color >> 16) & 0xFF) / 255f;
            float g = ((color >> 8) & 0xFF) / 255f;
            float b = (color & 0xFF) / 255f;
            RenderSystem.setShaderColor(r, g, b, 1f);
        }

        ResourceLocation texture = flipped ? GENE_TEXTURE_FLIPPED : GENE_TEXTURE;
        graphics.blit(texture, x, y, 0, 0, 16, 16, 16, 16);

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        boolean isSelected = (genomeA == (isGenomeA ? genomeA : genomeB) && selectedComponentA == comp) ||
                (genomeB == (isGenomeA ? genomeA : genomeB) && selectedComponentB == comp);

        if (isSelected) {
            graphics.renderOutline(x - 2, y - 2, COMPONENT_SIZE + 4, COMPONENT_SIZE + 4, COL_ACCENT);
        }
    }

    private void renderInfoPanel(GuiGraphics graphics, int mouseX, int mouseY) {
        int x = width - infoPanelWidth;
        int top = HEADER_HEIGHT;
        int bottom = height - FOOTER_HEIGHT;
        int height = bottom - top;

        graphics.fill(x, top, width, bottom, COL_PANEL_BG);
        graphics.vLine(x, top, bottom, COL_BORDER);

        if (selectedTraitA == null && selectedTraitB == null) {
            graphics.drawWordWrap(font, Component.translatable("gui.wildaside.gene_editor.select_component"), x + 6, top + 20, infoPanelWidth - 12, COL_TEXT_NORMAL);
            return;
        }

        int halfHeight = height / 2;
        int splitY = top + halfHeight;

        enableScissor(graphics, x, top, infoPanelWidth, halfHeight);
        int yA = (int) (top + 10 - infoScrollA);
        int startYA = yA;
        int padding = 6;
        int maxWidth = infoPanelWidth - 12;

        if (selectedTraitA != null) {
            graphics.drawString(font, Component.translatable("gui.wildaside.gene_editor.trait"), x + padding, yA, COL_TEXT_HEADER, false);
            yA += 12;
            Component traitName = Component.translatable("trait.wildaside." + selectedTraitA.getName()).withStyle(ChatFormatting.AQUA);
            graphics.drawWordWrap(font, traitName, x + padding, yA, maxWidth, COL_TEXT_VALUE);
            yA += font.wordWrapHeight(traitName, maxWidth) + 8;
            graphics.hLine(x + padding, width - padding, yA, COL_BORDER);
            yA += 8;

            graphics.drawString(font, Component.translatable("gui.wildaside.gene_editor.genome_a"), x + padding, yA, 0xFFFFAA00, false);
            yA += 12;
            yA = renderGenomeInfo(graphics, genomeA, selectedTraitA, x + padding, yA, maxWidth, selectedComponentA, selectedIsMaternalA);
        } else {
            graphics.drawWordWrap(font, Component.translatable("gui.wildaside.gene_editor.select_component"), x + padding, yA, maxWidth, COL_TEXT_NORMAL);
            yA += 20;
        }

        infoContentHeightA = yA - startYA + (int) infoScrollA;
        disableScissor(graphics);
        renderScrollbar(graphics, width - 4, top, halfHeight, infoContentHeightA, infoScrollA);

        graphics.hLine(x, width, splitY, COL_BORDER);

        enableScissor(graphics, x, splitY + 1, infoPanelWidth, height - halfHeight - 1);
        int yB = (int) (splitY + 10 - infoScrollB);
        int startYB = yB;

        if (selectedTraitB != null) {
            graphics.drawString(font, Component.translatable("gui.wildaside.gene_editor.trait"), x + padding, yB, COL_TEXT_HEADER, false);
            yB += 12;
            Component traitName = Component.translatable("trait.wildaside." + selectedTraitB.getName()).withStyle(ChatFormatting.AQUA);
            graphics.drawWordWrap(font, traitName, x + padding, yB, maxWidth, COL_TEXT_VALUE);
            yB += font.wordWrapHeight(traitName, maxWidth) + 8;
            graphics.hLine(x + padding, width - padding, yB, COL_BORDER);
            yB += 8;

            graphics.drawString(font, Component.translatable("gui.wildaside.gene_editor.genome_b"), x + padding, yB, 0xFF00AAFF, false);
            yB += 12;
            yB = renderGenomeInfo(graphics, genomeB, selectedTraitB, x + padding, yB, maxWidth, selectedComponentB, selectedIsMaternalB);
        } else {
            graphics.drawWordWrap(font, Component.translatable("gui.wildaside.gene_editor.select_component"), x + padding, yB, maxWidth, COL_TEXT_NORMAL);
            yB += 20;
        }

        infoContentHeightB = yB - startYB + (int) infoScrollB;
        disableScissor(graphics);
        renderScrollbar(graphics, width - 4, splitY + 1, height - halfHeight - 1, infoContentHeightB, infoScrollB);
    }

    private int renderGenomeInfo(GuiGraphics graphics, Genome genome, Trait trait, int x, int y, int maxWidth, Object selectedComponent, boolean isMaternal) {
        if (genome == null) return y;

        EntityType<?> type = genome.getEntityType();
        String entityName = type != null ? type.getDescription().getString() : "Unknown";
        graphics.drawString(font, entityName, x, y, COL_TEXT_NORMAL, false);
        y += 12;

        ExpressionContext context = new ExpressionContext(player);
        float expressed = genome.getExpressedValue(trait, context);
        graphics.drawString(font, Component.translatable("gui.wildaside.gene_editor.expressed", String.format("%.2f", expressed)), x, y, 0xFF55FF55, false);
        y += 12;

        if (trait != null) {
            ChromosomeSet chromSet = isMaternal ? genome.getMaternal() : genome.getPaternal();
            if (chromSet != null) {
                GeneSequence seq = chromSet.getSequence(trait);
                if (seq != null) {
                    graphics.drawString(font, Component.translatable("gui.wildaside.gene_editor.base", String.format("%.2f", seq.calculateBaseValue())), x, y, COL_TEXT_VALUE, false);
                    y += 10;
                    graphics.drawString(font, Component.translatable("gui.wildaside.gene_editor.stability", String.format("%.2f", seq.getStability())), x, y, COL_TEXT_VALUE, false);
                    y += 10;
                    graphics.drawString(font, Component.translatable("gui.wildaside.gene_editor.dominance", seq.getDominance().getComponent().getString()), x, y, COL_TEXT_VALUE, false);
                    y += 10;
                    graphics.drawString(font, Component.translatable("gui.wildaside.gene_editor.mutation", String.format("%.2f", seq.getMutationRate())), x, y, COL_TEXT_VALUE, false);
                    y += 10;
                    graphics.drawString(font, Component.translatable("gui.wildaside.gene_editor.source", Component.translatable("source.wildaside." + seq.getSource().name().toLowerCase()).getString()), x, y, COL_TEXT_VALUE, false);
                    y += 10;
                } else {
                    graphics.drawString(font, Component.translatable("gui.wildaside.gene_editor.no_sequence"), x, y, 0xFF888888, false);
                    y += 10;
                }
            }
        }

        if (selectedComponent != null && trait == (genome == genomeA ? selectedTraitA : selectedTraitB)) {
            y += 8;
            graphics.fill(x - 2, y, x + maxWidth + 2, y + 1, COL_BORDER);
            y += 5;

            graphics.drawString(font, Component.translatable("gui.wildaside.gene_editor.component"), x, y, COL_TEXT_HEADER, false);
            y += 12;
            graphics.drawString(font, getComponentType(selectedComponent), x, y, getComponentColor(selectedComponent), false);
            y += 12;

            if (selectedComponent instanceof Regulator reg) {
                graphics.drawString(font, Component.translatable("gui.wildaside.gene_editor.type", Component.translatable("regulation_type.wildaside." + reg.getRegulationType().name().toLowerCase()).getString()), x, y, COL_TEXT_VALUE, false);
                y += 10;
                graphics.drawString(font, Component.translatable("gui.wildaside.gene_editor.value", String.format("%.2f", reg.getValue())), x, y, COL_TEXT_VALUE, false);
                y += 10;
            } else if (selectedComponent instanceof CodingRegion cr) {
                graphics.drawString(font, Component.translatable("gui.wildaside.gene_editor.method", Component.translatable("combine_method.wildaside." + cr.getCombineMethod().name().toLowerCase()).getString()), x, y, COL_TEXT_VALUE, false);
                y += 10;
                graphics.drawString(font, Component.translatable("gui.wildaside.gene_editor.value", String.format("%.2f", cr.getValue())), x, y, COL_TEXT_VALUE, false);
                y += 10;
            } else if (selectedComponent instanceof Activator act) {
                graphics.drawString(font, Component.translatable("gui.wildaside.gene_editor.condition", Component.translatable("activation_condition.wildaside." + act.getCondition().name().toLowerCase()).getString()), x, y, COL_TEXT_VALUE, false);
                y += 10;
                graphics.drawString(font, Component.translatable("gui.wildaside.gene_editor.threshold", String.format("%.2f", act.getActivationThreshold())), x, y, COL_TEXT_VALUE, false);
                y += 10;
            } else if (selectedComponent instanceof Enhancer enh) {
                graphics.drawString(font, Component.translatable("gui.wildaside.gene_editor.condition", Component.translatable("activation_condition.wildaside." + enh.getCondition().name().toLowerCase()).getString()), x, y, COL_TEXT_VALUE, false);
                y += 10;
                graphics.drawString(font, Component.translatable("gui.wildaside.gene_editor.threshold", String.format("%.2f", enh.getThreshold())), x, y, COL_TEXT_VALUE, false);
                y += 10;
                graphics.drawString(font, Component.translatable("gui.wildaside.gene_editor.multiplier", String.format("%.2f", enh.getMultiplier())), x, y, COL_TEXT_VALUE, false);
                y += 10;
                graphics.drawString(font, Component.translatable("gui.wildaside.gene_editor.flat_bonus", String.format("%.2f", enh.getFlatBonus())), x, y, COL_TEXT_VALUE, false);
                y += 10;
            } else if (selectedComponent instanceof Silencer sil) {
                graphics.drawString(font, Component.translatable("gui.wildaside.gene_editor.condition", Component.translatable("activation_condition.wildaside." + sil.getCondition().name().toLowerCase()).getString()), x, y, COL_TEXT_VALUE, false);
                y += 10;
                graphics.drawString(font, Component.translatable("gui.wildaside.gene_editor.threshold", String.format("%.2f", sil.getThreshold())), x, y, COL_TEXT_VALUE, false);
                y += 10;
                graphics.drawString(font, Component.translatable("gui.wildaside.gene_editor.multiplier", String.format("%.2f", sil.getMultiplier())), x, y, COL_TEXT_VALUE, false);
                y += 10;
                graphics.drawString(font, Component.translatable("gui.wildaside.gene_editor.flat_penalty", String.format("%.2f", sil.getFlatPenalty())), x, y, COL_TEXT_VALUE, false);
                y += 10;
            } else if (selectedComponent instanceof TraitDefiner definer) {
                graphics.drawString(font, Component.translatable("gui.wildaside.gene_editor.trait"), x, y, COL_TEXT_VALUE, false);
                y += 10;
                graphics.drawString(font, Component.translatable("trait.wildaside." + definer.getTrait().getName()).getString(), x, y, COL_TEXT_VALUE, false);
                y += 10;
            }
        }
        return y;
    }

    private void renderScrollbar(GuiGraphics graphics, int x, int y, int height, int contentHeight, double scroll) {
        if (contentHeight <= height) return;

        int barHeight = (int) ((float) height / contentHeight * height);
        barHeight = Math.max(20, barHeight);

        int barY = y + (int) ((scroll / (contentHeight - height)) * (height - barHeight));

        graphics.fill(x, y, x + 2, y + height, 0xFF222222);

        double mouseX = Minecraft.getInstance().mouseHandler.xpos() * Minecraft.getInstance().getWindow().getGuiScale() / Minecraft.getInstance().getWindow().getScreenWidth() * this.width;
        double mouseY = Minecraft.getInstance().mouseHandler.ypos() * Minecraft.getInstance().getWindow().getGuiScale() / Minecraft.getInstance().getWindow().getScreenHeight() * this.height;

        boolean isHovered = mouseX >= x - 2 && mouseX <= x + 4 && mouseY >= barY && mouseY <= barY + barHeight;
        int color = isHovered ? 0xFF00FFFF : COL_ACCENT;

        graphics.fill(x, barY, x + 2, barY + barHeight, color);
    }

    private void renderDraggedNode(GuiGraphics graphics, int mouseX, int mouseY) {
        if (draggedNode == null) return;
        int color = getComponentColor(draggedNode.component);

        if (draggedNode.component instanceof CodingRegion) {
            RenderSystem.setShaderColor(1f, 1f, 1f, 0.8f);
        } else {
            float r = ((color >> 16) & 0xFF) / 255f;
            float g = ((color >> 8) & 0xFF) / 255f;
            float b = (color & 0xFF) / 255f;
            RenderSystem.setShaderColor(r, g, b, 0.8f);
        }

        ResourceLocation texture = draggedNode.flipped ? GENE_TEXTURE_FLIPPED : GENE_TEXTURE;
        graphics.blit(texture, mouseX - 8, mouseY - 8, 0, 0, 16, 16, 16, 16);

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }

    private void renderNodeTooltip(GuiGraphics graphics, VisualNode node, int mouseX, int mouseY) {
        List<Component> tooltip = new ArrayList<>();
        tooltip.add(getComponentType(node.component).copy().withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("trait.wildaside." + node.trait.getName()).withStyle(ChatFormatting.GRAY));

        if (node.component instanceof CodingRegion cr) {
            tooltip.add(Component.translatable("gui.wildaside.gene_editor.combine_method", Component.translatable("combine_method.wildaside." + cr.getCombineMethod().name().toLowerCase()).getString()).withStyle(ChatFormatting.GOLD));
            tooltip.add(Component.translatable("gui.wildaside.gene_editor.value", String.format("%.2f", cr.getValue())).withStyle(ChatFormatting.GREEN));
        } else if (node.component instanceof Activator act) {
            tooltip.add(Component.translatable("gui.wildaside.gene_editor.activation_condition", Component.translatable("activation_condition.wildaside." + act.getCondition().name().toLowerCase()).getString()).withStyle(ChatFormatting.GOLD));
            tooltip.add(Component.translatable("gui.wildaside.gene_editor.threshold", String.format("%.2f", act.getActivationThreshold())).withStyle(ChatFormatting.AQUA));
        } else if (node.component instanceof Enhancer enh) {
            tooltip.add(Component.translatable("gui.wildaside.gene_editor.condition", Component.translatable("activation_condition.wildaside." + enh.getCondition().name().toLowerCase()).getString()).withStyle(ChatFormatting.GOLD));
            tooltip.add(Component.translatable("gui.wildaside.gene_editor.threshold", String.format("%.2f", enh.getThreshold())).withStyle(ChatFormatting.AQUA));
            tooltip.add(Component.translatable("gui.wildaside.gene_editor.multiplier", String.format("%.2f", enh.getMultiplier())).withStyle(ChatFormatting.GREEN));
            tooltip.add(Component.translatable("gui.wildaside.gene_editor.flat_bonus", String.format("%.2f", enh.getFlatBonus())).withStyle(ChatFormatting.BLUE));
        } else if (node.component instanceof Silencer sil) {
            tooltip.add(Component.translatable("gui.wildaside.gene_editor.condition", Component.translatable("activation_condition.wildaside." + sil.getCondition().name().toLowerCase()).getString()).withStyle(ChatFormatting.GOLD));
            tooltip.add(Component.translatable("gui.wildaside.gene_editor.threshold", String.format("%.2f", sil.getThreshold())).withStyle(ChatFormatting.AQUA));
            tooltip.add(Component.translatable("gui.wildaside.gene_editor.multiplier", String.format("%.2f", sil.getMultiplier())).withStyle(ChatFormatting.GREEN));
            tooltip.add(Component.translatable("gui.wildaside.gene_editor.flat_penalty", String.format("%.2f", sil.getFlatPenalty())).withStyle(ChatFormatting.RED));
        } else if (node.component instanceof Regulator reg) {
            tooltip.add(Component.translatable("gui.wildaside.gene_editor.regulation_type", Component.translatable("regulation_type.wildaside." + reg.getRegulationType().name().toLowerCase()).getString()).withStyle(ChatFormatting.GOLD));
            tooltip.add(Component.translatable("gui.wildaside.gene_editor.value", String.format("%.2f", reg.getValue())).withStyle(ChatFormatting.GREEN));
        } else if (node.component instanceof TraitDefiner definer) {
//            tooltip.add(Component.translatable("gui.wildaside.gene_editor.trait_2", Component.translatable("trait.wildaside." + definer.getTrait().getName()).getString()).withStyle(ChatFormatting.LIGHT_PURPLE));
        }

        graphics.renderComponentTooltip(font, tooltip, mouseX, mouseY);
    }

    private void renderFooter(GuiGraphics graphics) {
        graphics.fill(0, height - FOOTER_HEIGHT, width, height, COL_BACKGROUND);
        graphics.hLine(0, width, height - FOOTER_HEIGHT, COL_BORDER);

        if (!statusMessages.isEmpty()) {
            graphics.drawString(font, statusMessages.get(statusMessages.size() - 1), 10, height - 15, COL_TEXT_VALUE, false);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) return true;

        if (Math.abs(mouseX - sidebarWidth) <= 2) {
            isResizingSidebar = true;
            return true;
        }
        if (Math.abs(mouseX - (width - infoPanelWidth)) <= 2) {
            isResizingInfoPanel = true;
            return true;
        }

        if (button == 0) {
            if (mouseX >= sidebarWidth - 4 && mouseX <= sidebarWidth && mouseY >= HEADER_HEIGHT && mouseY <= height - FOOTER_HEIGHT) {
                isDraggingSidebarScroll = true;
                scrollDragStartY = mouseY;
                scrollDragStartValue = targetSidebarScroll;
                return true;
            }

            if (mouseX >= width - 4 && mouseX <= width) {
                int top = HEADER_HEIGHT;
                int bottom = height - FOOTER_HEIGHT;
                int height = bottom - top;
                int halfHeight = height / 2;

                if (mouseY >= top && mouseY <= top + halfHeight) {
                    isDraggingInfoScrollA = true;
                    scrollDragStartY = mouseY;
                    scrollDragStartValue = targetInfoScrollA;
                    return true;
                } else if (mouseY >= top + halfHeight + 1 && mouseY <= bottom) {
                    isDraggingInfoScrollB = true;
                    scrollDragStartY = mouseY;
                    scrollDragStartValue = targetInfoScrollB;
                    return true;
                }
            }
        }

        boolean clickedSomething = false;

        if (mouseX < sidebarWidth && mouseY > HEADER_HEIGHT && mouseY < height - FOOTER_HEIGHT) {
            int y = (int) (HEADER_HEIGHT + 5 - sidebarScroll);
            for (Map.Entry<TraitType, List<Trait>> entry : traitsByType.entrySet()) {
                y += 14;
                for (Trait trait : entry.getValue()) {
                    if (mouseY >= y && mouseY < y + 14) {
                        double targetAMat = getTrackScrollForTrait(genomeA, true, trait);
                        double targetAPat = getTrackScrollForTrait(genomeA, false, trait);
                        double targetBMat = getTrackScrollForTrait(genomeB, true, trait);
                        double targetBPat = getTrackScrollForTrait(genomeB, false, trait);

                        double centerOffset = (stripWidth / (double) (COMPONENT_SIZE + COMPONENT_SPACING)) / 4.0;

                        targetScrollAMat = targetAMat - centerOffset;
                        targetScrollAPat = targetAPat - centerOffset;
                        targetScrollBMat = targetBMat - centerOffset;
                        targetScrollBPat = targetBPat - centerOffset;

                        scrollAMat = targetScrollAMat;
                        scrollAPat = targetScrollAPat;
                        scrollBMat = targetScrollBMat;
                        scrollBPat = targetScrollBPat;

                        player.playSound(SoundEvents.UI_BUTTON_CLICK.get(), 1f, 1f);
                        selectedTraitA = trait;
                        selectedTraitB = trait;
                        clickedSomething = true;
                    }
                    y += 14;
                }
                y += 8;
            }
        }

        if (button == 0) {
            if (hoveredNode != null) {
                if (hoveredNode.isGenomeA) {
                    selectedComponentA = hoveredNode.component;
                    selectedSequenceA = hoveredNode.sequence;
                    selectedIsMaternalA = hoveredNode.isMaternal;
                    selectedTraitA = hoveredNode.trait;
                } else {
                    selectedComponentB = hoveredNode.component;
                    selectedSequenceB = hoveredNode.sequence;
                    selectedIsMaternalB = hoveredNode.isMaternal;
                    selectedTraitB = hoveredNode.trait;
                }

                draggedNode = hoveredNode;
                player.playSound(SoundEvents.UI_BUTTON_CLICK.get(), 1f, 1f);
                clickedSomething = true;
            } else if (hoveredSequence != null && hoveredTrait != null) {
                if (mouseY >= stripAY1 && mouseY <= stripAY2 + STRIP_HEIGHT + COMPONENT_SIZE) {
                    selectedSequenceA = hoveredSequence;
                    selectedComponentA = null;
                    selectedIsGenomeA = true;
                    selectedIsMaternalA = (mouseY >= stripAY1 && mouseY <= stripAY1 + STRIP_HEIGHT + COMPONENT_SIZE);
                    selectedTraitA = hoveredTrait;
                } else {
                    selectedSequenceB = hoveredSequence;
                    selectedComponentB = null;
                    selectedIsGenomeA = false;
                    selectedIsMaternalB = (mouseY >= stripBY1 && mouseY <= stripBY1 + STRIP_HEIGHT + COMPONENT_SIZE);
                    selectedTraitB = hoveredTrait;
                }

                player.playSound(SoundEvents.UI_BUTTON_CLICK.get(), 1f, 1f);
                clickedSomething = true;
            }
        }

        if (button == 0 && mouseX > sidebarWidth && mouseX < width - infoPanelWidth && !clickedSomething) {
            isDraggingView = true;
            lastMouseX = mouseX;
            clickedSomething = true;
        }

        if (!clickedSomething && button == 0) {
            if (mouseX > sidebarWidth && mouseX < width - infoPanelWidth) {
                selectedComponentA = null;
                selectedComponentB = null;
                selectedSequenceA = null;
                selectedSequenceB = null;
                selectedTraitA = null;
                selectedTraitB = null;
                return true;
            }
        }

        return clickedSomething;
    }

    private double getTrackScrollForTrait(Genome genome, boolean isMaternal, Trait targetTrait) {
        if (genome == null) return 0;
        ChromosomeSet chromSet = isMaternal ? genome.getMaternal() : genome.getPaternal();
        if (chromSet == null) return 0;

        double currentX = 0;
        for (Trait trait : allTraits) {
            if (trait == targetTrait) return currentX / (COMPONENT_SIZE + COMPONENT_SPACING);

            GeneSequence seq = chromSet.getSequence(trait);
            if (seq != null) {
                int size = getAllComponents(seq).size();
                currentX += size * (COMPONENT_SIZE + COMPONENT_SPACING);
            }
        }
        return 0;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            if (draggedNode != null) {
                handleDrop(mouseX, mouseY);
                draggedNode = null;
            }
            isDraggingView = false;
            isResizingSidebar = false;
            isResizingInfoPanel = false;
            isDraggingSidebarScroll = false;
            isDraggingInfoScrollA = false;
            isDraggingInfoScrollB = false;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private void handleDrop(double mouseX, double mouseY) {
        addStatusMessage(Component.translatable("gui.wildaside.gene_editor.dropped_debug"));
        //!
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (isResizingSidebar) {
            sidebarWidth = (int) Math.max(50, Math.min(width / 2, mouseX));
            updateLayout();
            return true;
        }
        if (isResizingInfoPanel) {
            infoPanelWidth = (int) Math.max(50, Math.min(width / 2, width - mouseX));
            updateLayout();
            return true;
        }

        if (isDraggingSidebarScroll) {
            int visibleHeight = height - HEADER_HEIGHT - FOOTER_HEIGHT;
            double maxScroll = Math.max(0, sidebarContentHeight - visibleHeight);
            double delta = (mouseY - scrollDragStartY) / (double) (visibleHeight - 20) * maxScroll;
            targetSidebarScroll = Mth.clamp(scrollDragStartValue + delta, 0, maxScroll);
            return true;
        }

        if (isDraggingInfoScrollA) {
            int visibleHeight = (height - HEADER_HEIGHT - FOOTER_HEIGHT) / 2;
            double maxScroll = Math.max(0, infoContentHeightA - visibleHeight);
            double delta = (mouseY - scrollDragStartY) / (double) (visibleHeight - 20) * maxScroll;
            targetInfoScrollA = Mth.clamp(scrollDragStartValue + delta, 0, maxScroll);
            return true;
        }

        if (isDraggingInfoScrollB) {
            int totalHeight = height - HEADER_HEIGHT - FOOTER_HEIGHT;
            int visibleHeight = totalHeight - (totalHeight / 2) - 1;
            double maxScroll = Math.max(0, infoContentHeightB - visibleHeight);
            double delta = (mouseY - scrollDragStartY) / (double) (visibleHeight - 20) * maxScroll;
            targetInfoScrollB = Mth.clamp(scrollDragStartValue + delta, 0, maxScroll);
            return true;
        }

        if (isDraggingView && draggedNode == null) {
            double delta = dragX / (COMPONENT_SIZE + COMPONENT_SPACING);

            if (lockPairs) {
                targetScrollAMat -= delta;
                targetScrollAPat -= delta;
                targetScrollBMat -= delta;
                targetScrollBPat -= delta;
            } else {
                boolean draggingA = mouseY < height / 2;

                if (draggingA) {
                    if (lockGenomeA) {
                        targetScrollAMat -= delta;
                        targetScrollAPat -= delta;
                    } else {
                        if (mouseY < stripAY2) targetScrollAMat -= delta;
                        else targetScrollAPat -= delta;
                    }
                } else {
                    if (lockGenomeB) {
                        targetScrollBMat -= delta;
                        targetScrollBPat -= delta;
                    } else {
                        if (mouseY < stripBY2) targetScrollBMat -= delta;
                        else targetScrollBPat -= delta;
                    }
                }
            }

            scrollAMat = targetScrollAMat;
            scrollAPat = targetScrollAPat;
            scrollBMat = targetScrollBMat;
            scrollBPat = targetScrollBPat;

            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (mouseX < sidebarWidth) {
            targetSidebarScroll -= delta * 20;
            int visibleHeight = height - HEADER_HEIGHT - FOOTER_HEIGHT;
            targetSidebarScroll = Mth.clamp(targetSidebarScroll, 0, Math.max(0, sidebarContentHeight - visibleHeight));
            return true;
        }

        if (mouseX > width - infoPanelWidth) {
            int top = HEADER_HEIGHT;
            int bottom = height - FOOTER_HEIGHT;
            int height = bottom - top;
            int halfHeight = height / 2;

            if (mouseY < top + halfHeight) {
                targetInfoScrollA -= delta * 20;
                targetInfoScrollA = Mth.clamp(targetInfoScrollA, 0, Math.max(0, infoContentHeightA - halfHeight));
            } else {
                targetInfoScrollB -= delta * 20;
                targetInfoScrollB = Mth.clamp(targetInfoScrollB, 0, Math.max(0, infoContentHeightB - (height - halfHeight)));
            }
            return true;
        }

        if (lockPairs) {
            targetScrollAMat -= delta * 2;
            targetScrollAPat -= delta * 2;
            targetScrollBMat -= delta * 2;
            targetScrollBPat -= delta * 2;
        } else {
            boolean inA = mouseY < height / 2;
            if (inA) {
                if (lockGenomeA) {
                    targetScrollAMat -= delta * 2;
                    targetScrollAPat -= delta * 2;
                } else {
                    if (mouseY < stripAY2) targetScrollAMat -= delta * 2;
                    else targetScrollAPat -= delta * 2;
                }
            } else {
                if (lockGenomeB) {
                    targetScrollBMat -= delta * 2;
                    targetScrollBPat -= delta * 2;
                } else {
                    if (mouseY < stripBY2) targetScrollBMat -= delta * 2;
                    else targetScrollBPat -= delta * 2;
                }
            }
        }
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (ModKeyMappings.GENE_EDITOR_LOCK_SCROLL.matches(keyCode, scanCode)) {
            lockPairs = !lockPairs;
            return true;
        }
        if (ModKeyMappings.GENE_EDITOR_RESET_SCROLL.matches(keyCode, scanCode)) {
            targetScrollAMat = 0;
            targetScrollAPat = 0;
            targetScrollBMat = 0;
            targetScrollBPat = 0;
            return true;
        }
        if (ModKeyMappings.GENE_EDITOR_EXECUTE.matches(keyCode, scanCode)) {
            executeOperation();
            return true;
        }
        if (ModKeyMappings.GENE_EDITOR_RESET_CHANGES.matches(keyCode, scanCode)) {
            resetChanges();
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_LEFT) {
            targetScrollAMat -= 2;
            targetScrollAPat -= 2;
            targetScrollBMat -= 2;
            targetScrollBPat -= 2;
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_RIGHT) {
            targetScrollAMat += 2;
            targetScrollAPat += 2;
            targetScrollBMat += 2;
            targetScrollBPat += 2;
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private List<Object> getAllComponents(GeneSequence seq) {
        List<Object> list = new ArrayList<>();
        if (!seq.getComponents().isEmpty() && seq.getComponents().get(0) instanceof TraitDefiner) {
            list.add(seq.getComponents().get(0));
        }

        for (GeneComponent component : seq.getComponents()) {
            if (!(component instanceof TraitDefiner)) {
                list.add(component);
            }
        }
        return list;
    }

    private int getComponentColor(Object comp) {
//        if (comp instanceof CodingRegion) return 0xFF4CAF50; //Green
//        if (comp instanceof Activator) return 0xFF03A9F4; //Light Blue
//        if (comp instanceof Enhancer) return 0xFFFFC107; //Material Amber
//        if (comp instanceof Silencer) return 0xFFF44336; //Material Red
//        if (comp instanceof Regulator) return 0xFF9C27B0; //Material Purple
//        if (comp instanceof TraitDefiner) return 0xFFE91E63; //Material Pink
//        return 0xFF9E9E9E; //Grey 500
        return 0xFFFFFFFF;
    }

    private Component getComponentType(Object comp) {
        if (comp instanceof CodingRegion)
            return Component.translatable("gui.wildaside.gene_editor.component.coding_region");
        if (comp instanceof Activator) return Component.translatable("gui.wildaside.gene_editor.component.activator");
        if (comp instanceof Enhancer) return Component.translatable("gui.wildaside.gene_editor.component.enhancer");
        if (comp instanceof Silencer) return Component.translatable("gui.wildaside.gene_editor.component.silencer");
        if (comp instanceof Regulator) return Component.translatable("gui.wildaside.gene_editor.component.regulator");
        if (comp instanceof TraitDefiner)
            return Component.translatable("gui.wildaside.gene_editor.component.trait_definer");
        return Component.translatable("gui.wildaside.gene_editor.component.unknown");
    }

    private String truncate(String s, int width) {
        if (font.width(s) <= width) return s;
        return s.substring(0, Math.min(s.length(), 10)) + "...";
    }

    private void addStatusMessage(Component msg) {
        statusMessages.add(msg);
        if (statusMessages.size() > 5) statusMessages.remove(0);
    }

    private void enableScissor(GuiGraphics graphics, int x, int y, int width, int height) {
        double scale = minecraft.getWindow().getGuiScale();
        RenderSystem.enableScissor((int) (x * scale), (int) ((this.height - y - height) * scale), (int) (width * scale), (int) (height * scale));
    }

    private void disableScissor(GuiGraphics graphics) {
        RenderSystem.disableScissor();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public void onClose() {
        this.minecraft.setScreen(null);
    }

    private void executeOperation() {
        addStatusMessage(Component.translatable("gui.wildaside.gene_editor.execute"));
        //!
    }

    private void resetChanges() {
        selectedComponentA = null;
        selectedComponentB = null;
        selectedSequenceA = null;
        selectedSequenceB = null;
        selectedTraitA = null;
        selectedTraitB = null;
        addStatusMessage(Component.translatable("gui.wildaside.gene_editor.reset_done"));
    }

    private static class VisualNode {
        final Object component;
        final Trait trait;
        final GeneSequence sequence;
        final boolean isMaternal;
        final boolean isGenomeA;
        final int x, y;
        final boolean flipped;

        VisualNode(Object component, Trait trait, GeneSequence sequence, boolean isMaternal, boolean isGenomeA, int x, int y, boolean flipped) {
            this.component = component;
            this.trait = trait;
            this.sequence = sequence;
            this.isMaternal = isMaternal;
            this.isGenomeA = isGenomeA;
            this.x = x;
            this.y = y;
            this.flipped = flipped;
        }
    }

    @Nullable
    private VisualNode draggedNode = null;
    @Nullable
    private VisualNode hoveredNode = null;
}