package net.farkas.wildaside.screen.bioengineering_workstation;

import com.mojang.blaze3d.systems.RenderSystem;
import net.farkas.wildaside.capability.bioengineering_skill.BioengineeringSkillsCapability;
import net.farkas.wildaside.capability.bioengineering_skill.IBioengineeringSkills;
import net.farkas.wildaside.capability.dna.DnaImplementation;
import net.farkas.wildaside.dna.DnaConstants;
import net.farkas.wildaside.dna.DnaUtils;
import net.farkas.wildaside.dna.bioengineering_skill.BioengineeringSkill;
import net.farkas.wildaside.dna.bioengineering_skill.BioengineeringSkillUtils;
import net.farkas.wildaside.item.custom.DnaHolderItem;
import net.farkas.wildaside.network.NetworkHandler;
import net.farkas.wildaside.screen.gene_editor.AdvancedGeneEditorScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BioengineeringWorkstationScreen extends AbstractContainerScreen<BioengineeringWorkstationMenu> {
    private static final float HELIX_AMPLITUDE = BioengineeringSkillTreeRegistry.AMPLITUDE;
    private static final float HELIX_PART_WIDTH = BioengineeringSkillTreeRegistry.PART_WIDTH;
    private static final float HELIX_K = BioengineeringSkillTreeRegistry.K;
    private static final float HELIX_Y0 = BioengineeringSkillTreeRegistry.Y0;

    private static final int NODE_SIZE = 24;
    private static final float NODE_HALF = NODE_SIZE * 0.5f;

    private final BioengineeringWorkstationMenu menu;
    public BioengineeringWorkstationTab tab;
    private static ResourceLocation BACKGROUND;

    private float skillOffsetX = 0;
    private float skillOffsetY = 0;
    private boolean draggingSkillView = false;
    private double lastMouseX, lastMouseY;

    private final int yOffset;

    @Nullable
    private SkillNode hoveredSkillNode = null;

    public BioengineeringWorkstationScreen(BioengineeringWorkstationMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        menu = pMenu;
        yOffset = BioengineeringWorkstationMenu.yOffset;
    }

    @Override
    protected void init() {
        this.imageWidth = 256;
        this.imageHeight = 193;

        super.init();

        this.inventoryLabelY = 9999;
        this.titleLabelY = 9999;

        tab = menu.getTab();
        BACKGROUND = tab.getTexture();

        BioengineeringSkillTreeRegistry.rebuild();

        addTabButtons();
        addRecompileButton();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, delta);

        hoveredSkillNode = null;

        if (tab == BioengineeringWorkstationTab.SKILL_TAB) {
            renderSkillViewport(guiGraphics, mouseX, mouseY);
            renderSkillPoints(guiGraphics);

            hoveredSkillNode = getHoveredSkillNode(mouseX, mouseY);
            if (hoveredSkillNode != null) {
                renderSkillTooltip(guiGraphics, hoveredSkillNode, mouseX, mouseY);
            }
        }
        else if (tab == BioengineeringWorkstationTab.DNA_EDITOR) {
            renderLatentGeneOverlays(guiGraphics, mouseX, mouseY);
        }

        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, BACKGROUND);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        ResourceLocation background = menu.getTab().getTexture();
        guiGraphics.blit(background, x, y, 0, 0, imageWidth, imageHeight);

        if (tab != BioengineeringWorkstationTab.SKILL_TAB) {
            renderProgressArrow(guiGraphics, x, y);
            renderDnaConnectors(guiGraphics, x, y, 0);
            renderDnaConnectors(guiGraphics, x, y, 1);
        }
    }

    private void renderProgressArrow(GuiGraphics guiGraphics, int x, int y) {
        if (menu.isCrafting() && tab == BioengineeringWorkstationTab.ASSEMBLER) {
            guiGraphics.blit(BACKGROUND, x + 129, y + 37 + yOffset, 0, 248, menu.getScaledCraftingProgress(), 8);
        }
        if (menu.isAnalysing() && tab == BioengineeringWorkstationTab.DNA_ANALYSER) {
            guiGraphics.blit(BACKGROUND, x + 129, y + 37 + yOffset, 0, 248, menu.getScaledAnalysingProgress(), 8);
        }
    }

    private void renderDnaConnectors(GuiGraphics guiGraphics, int x, int y, int i) {
        if (tab == BioengineeringWorkstationTab.DNA_EDITOR) {
            ItemStack stack = menu.getSlot(46 + i).getItem();

            if (stack.getItem() instanceof DnaHolderItem) {
                CompoundTag tag = stack.getOrCreateTag();
                CompoundTag dnaTag = tag.getCompound(DnaConstants.DNA_DATA);
                DnaImplementation dna = new DnaImplementation();
                dna.deserializeNBT(dnaTag);

                if (hasValidDna(dna) && !tag.getBoolean(DnaConstants.SAMPLE_UNUSABLE) && tag.getBoolean(DnaConstants.REVEAL_TRAITS)) {
                    guiGraphics.blit(BACKGROUND, x + 25, y + 12 + yOffset + i * 22, 0, 248, 8, 8);
                }
            }
        }
    }

    private void renderLatentGeneOverlays(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (tab != BioengineeringWorkstationTab.DNA_EDITOR) return;

        for (GeneSlotItemHandler slot : menu.topGeneSlots) {
            if (slot.isActive() && slot.isLatentGene()) {
                renderLatentOverlay(guiGraphics, slot.x + leftPos, slot.y + topPos);
            }
        }

        for (GeneSlotItemHandler slot : menu.botGeneSlots) {
            if (slot.isActive() && slot.isLatentGene()) {
                renderLatentOverlay(guiGraphics, slot.x + leftPos, slot.y + topPos);
            }
        }
    }

    private void renderLatentOverlay(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.fill(x, y, x + 16, y + 16, 0x80000000);
    }

    private void renderSkillViewport(GuiGraphics graphics, int mouseX, int mouseY) {
        int x = this.leftPos + 8;
        int y = this.topPos + 36;
        int width = 240;
        int height = 150;

        enableScissor(graphics, x, y, width, height);

        graphics.fill(x, y, x + width, y + height, 0x66000000);

        graphics.pose().pushPose();
        graphics.pose().translate(x + skillOffsetX, y + skillOffsetY, 0);

        renderHelixCurves(graphics);

        drawSkillNodes(graphics);

        graphics.pose().popPose();

        disableScissor(graphics);
    }

    private void enableScissor(GuiGraphics graphics, int x, int y, int width, int height) {
        double scale = minecraft.getWindow().getGuiScale();

        int sx = (int) (x * scale);
        int sy = (int) ((this.height - (y + height)) * scale);
        int sw = (int) (width * scale);
        int sh = (int) (height * scale);

        RenderSystem.enableScissor(sx, sy, sw, sh);
    }

    private void disableScissor(GuiGraphics graphics) {
        RenderSystem.disableScissor();
    }

    private void drawSkillNodes(GuiGraphics graphics) {
        for (SkillNode node : BioengineeringSkillTreeRegistry.NODES) {
            int px = node.x;
            int py = node.y;

            ResourceLocation tex = node.skill.getTexture();

            int borderColor =
                    node.isUnlocked(minecraft.player) ? 0xFF44FF44 :
                            node.canUnlock(minecraft.player) ? 0xFFFFFF44 :
                                    0xFFFF4444;

            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, BACKGROUND);

            float r = ((borderColor >> 16) & 0xFF) / 255f;
            float g = ((borderColor >> 8) & 0xFF) / 255f;
            float b = (borderColor & 0xFF) / 255f;
            float a = ((borderColor >> 24) & 0xFF) / 255f;

            RenderSystem.setShaderColor(r, g, b, a);

            graphics.blit(BACKGROUND, px - 1, py - 1, 35, 227, 27, 27, 256, 256);

            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

            graphics.blit(BACKGROUND, px, py, 4, 228, 24, 24, 256, 256);
            graphics.blit(tex, px + 4, py + 4, 0, 0, 16, 16, 16, 16);
        }
    }

    @Override
    public void containerTick() {
        super.containerTick();
        int correctLeft = (this.width - this.imageWidth) / 2;
        int correctTop = (this.height - this.imageHeight) / 2;
        if (this.leftPos != correctLeft || this.topPos != correctTop) {
            this.leftPos = correctLeft;
            this.topPos = correctTop;
            clearWidgets();
            addTabButtons();
            addRecompileButton();
            addAdvancedEditorButton();
        }
    }

    private void switchTab(BioengineeringWorkstationTab newTab) {
        if (tab == newTab) return;
        tab = newTab;

        NetworkHandler.sendBioengineeringWorkstationTabPacket(newTab);

        menu.setTab(newTab);
        BACKGROUND = tab.getTexture();

        clearWidgets();
        addRecompileButton();
        addTabButtons();
        addAdvancedEditorButton();
    }

    private void addAdvancedEditorButton() {
        if (tab == BioengineeringWorkstationTab.DNA_EDITOR) {
            this.addRenderableWidget(Button.builder(Component.translatable("gui.wildaside.open_advanced_editor"), btn -> {
                        openAdvancedEditor();
                    }).pos(leftPos + 120, topPos + 55 + yOffset).size(60, 20)
                    .tooltip(Tooltip.create(Component.translatable("gui.wildaside.open_advanced_editor.tooltip"))).build());
        }
    }

    private void openAdvancedEditor() {
        ItemStack stackA = menu.getSlot(46).getItem();
        ItemStack stackB = menu.getSlot(47).getItem();

        DnaImplementation dnaA = null;
        DnaImplementation dnaB = null;

        if (stackA.getItem() instanceof DnaHolderItem) {
            CompoundTag tag = stackA.getOrCreateTag();
            if (tag.contains(DnaConstants.DNA_DATA)) {
                dnaA = new DnaImplementation();
                dnaA.deserializeNBT(tag.getCompound(DnaConstants.DNA_DATA));
            }
        }

        if (stackB.getItem() instanceof DnaHolderItem) {
            CompoundTag tag = stackB.getOrCreateTag();
            if (tag.contains(DnaConstants.DNA_DATA)) {
                dnaB = new DnaImplementation();
                dnaB.deserializeNBT(tag.getCompound(DnaConstants.DNA_DATA));
            }
        }

        if (dnaA == null && dnaB == null) {
            minecraft.gui.setOverlayMessage(Component.translatable("gui.wildaside.gene_editor.no_dna"), false);
            return;
        }

        minecraft.setScreen(new AdvancedGeneEditorScreen(menu.blockEntity.getBlockPos(), dnaA, dnaB));
    }

    private void addTabButtons() {
        this.addWidget(Button.builder(Component.empty(), b -> switchTab(BioengineeringWorkstationTab.ASSEMBLER))
                .pos(this.leftPos + 44, this.topPos + 3)
                .size(24, 25)
                .tooltip(Tooltip.create(Component.translatable("gui.wildaside.bioengineering_workstation.bio_assembler")))
                .build());

        this.addWidget(Button.builder(Component.empty(), b -> switchTab(BioengineeringWorkstationTab.DNA_ANALYSER))
                .pos(this.leftPos + 71, this.topPos + 3)
                .size(24, 25)
                .tooltip(Tooltip.create(Component.translatable("gui.wildaside.bioengineering_workstation.dna_analyser")))
                .build());

        this.addWidget(Button.builder(Component.empty(), b -> switchTab(BioengineeringWorkstationTab.DNA_EDITOR))
                .pos(this.leftPos + 98, this.topPos + 3)
                .size(24, 25)
                .tooltip(Tooltip.create(Component.translatable("gui.wildaside.bioengineering_workstation.dna_editor")))
                .build());

        this.addWidget(Button.builder(Component.empty(), b -> switchTab(BioengineeringWorkstationTab.SKILL_TAB))
                .pos(this.leftPos + 189, this.topPos + 3)
                .size(24, 25)
                .tooltip(Tooltip.create(Component.translatable("gui.wildaside.bioengineering_workstation.skill_tab")))
                .build());
    }

    public void addRecompileButton() {
        if (tab == BioengineeringWorkstationTab.DNA_EDITOR) {
            this.addRenderableWidget(Button.builder(Component.literal("="), btn -> {
                        NetworkHandler.sendBioengineeringWorkstationRecompileGenesPacket(menu.blockEntity.getBlockPos());
                    }).pos(leftPos + 14, topPos + 55 + yOffset).size(20, 20)
                    .tooltip(Tooltip.create(Component.translatable("gui.wildaside.bioengineering_workstation.recompile_dna"))).build());
        }
    }

    @Nullable
    private SkillNode getHoveredSkillNode(double mouseX, double mouseY) {
        int viewX = this.leftPos + 8;
        int viewY = this.topPos + 36;
        int viewW = 240;
        int viewH = 150;

        boolean insideViewport =
                mouseX >= viewX && mouseX <= viewX + viewW &&
                        mouseY >= viewY && mouseY <= viewY + viewH;

        if (!insideViewport) return null;

        double localX = mouseX - viewX - skillOffsetX;
        double localY = mouseY - viewY - skillOffsetY;

        for (SkillNode node : BioengineeringSkillTreeRegistry.NODES) {
            if (localX >= node.x && localX <= node.x + 24 &&
                    localY >= node.y && localY <= node.y + 24) {
                return node;
            }
        }

        return null;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (tab == BioengineeringWorkstationTab.SKILL_TAB && button == 0) {
            int viewX = this.leftPos + 8;
            int viewY = this.topPos + 36;
            int viewW = 240;
            int viewH = 150;

            boolean insideViewport = mouseX >= viewX && mouseX <= viewX + viewW && mouseY >= viewY && mouseY <= viewY + viewH;
            if (!insideViewport) return super.mouseClicked(mouseX, mouseY, button);

            double localX = mouseX - viewX - skillOffsetX;
            double localY = mouseY - viewY - skillOffsetY;

            for (SkillNode node : BioengineeringSkillTreeRegistry.NODES) {
                if (localX >= node.x && localX <= node.x + 24 && localY >= node.y && localY <= node.y + 24) {
                    onSkillNodeClicked(node);
                    return true;
                }
            }

            draggingSkillView = true;
            lastMouseX = mouseX;
            lastMouseY = mouseY;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (tab == BioengineeringWorkstationTab.SKILL_TAB && draggingSkillView && button == 0) {
            float dx = (float) (mouseX - lastMouseX);
            float dy = (float) (mouseY - lastMouseY);

            skillOffsetX += dx;
            skillOffsetY += dy;

            lastMouseX = mouseX;
            lastMouseY = mouseY;

            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (tab == BioengineeringWorkstationTab.SKILL_TAB && button == 0) {
            draggingSkillView = false;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private void onSkillNodeClicked(SkillNode node) {
        boolean unlocked = BioengineeringSkillUtils.hasSkill(menu.player, node.skill.getId());
        boolean canUnlock = BioengineeringSkillUtils.canUnlock(menu.player, node.skill);

        if (unlocked) {
            minecraft.gui.setOverlayMessage(Component.translatable("skill.wildaside.already_unlocked"), false);
            return;
        }

        if (!canUnlock) {
            minecraft.gui.setOverlayMessage(Component.translatable("skill.wildaside.cannot_unlock"), false);
            return;
        }

        menu.player.playSound(SoundEvents.UI_BUTTON_CLICK.get(), 0.5f, 1.0f);

        NetworkHandler.sendBioengineeringSkillUnlockRequestPacket(node.skill.getId());
    }

    private void renderSkillTooltip(GuiGraphics graphics, SkillNode node, int mouseX, int mouseY) {
        Player player = minecraft.player;
        if (player == null) return;

        BioengineeringSkill skill = node.skill;

        List<Component> tooltip = new ArrayList<>();

        tooltip.add(skill.getNameComponent().copy().withStyle(ChatFormatting.GOLD));

        tooltip.add(skill.getDescriptionComponent().copy().withStyle(ChatFormatting.GRAY));

        tooltip.add(Component.empty());

        if (node.isUnlocked(player)) {
            tooltip.add(Component.translatable("skill.wildaside.status.unlocked")
                    .withStyle(ChatFormatting.GREEN));
        }
        else if (node.canUnlock(player)) {
            tooltip.add(Component.translatable("skill.wildaside.status.available")
                    .withStyle(ChatFormatting.YELLOW));
        }
        else {
            tooltip.add(Component.translatable("skill.wildaside.status.locked")
                    .withStyle(ChatFormatting.RED));
        }

        List<Component> reqTooltip = skill.getRequirement().getTooltip(player);
        if (!reqTooltip.isEmpty()) {
            tooltip.add(Component.empty());
            tooltip.add(Component.translatable("skill.wildaside.requirements")
                    .withStyle(ChatFormatting.AQUA));

            tooltip.addAll(reqTooltip);
        }

        graphics.renderTooltip(this.font, tooltip, Optional.empty(), mouseX, mouseY);
    }

    private void renderSkillPoints(GuiGraphics graphics) {
        if (tab != BioengineeringWorkstationTab.SKILL_TAB) return;

        int x = this.leftPos + 10;
        int y = this.topPos + 38;

        int points = getClientSkillPoints();

        Component text = Component.translatable("skill.wildaside.points", points).withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD);

        int width = font.width(text) + 6;
        graphics.fill(x - 3, y - 3, x + width, y + 9, 0x88000000);

        graphics.drawString(font, text, x, y, 0xFFFFFF, false);
    }

    private int getClientSkillPoints() {
        Player player = minecraft.player;
        if (player == null) return 0;

        return player.getCapability(BioengineeringSkillsCapability.INSTANCE)
                .map(IBioengineeringSkills::getPoints)
                .orElse(0);
    }

    private void renderHelixCurves(GuiGraphics graphics) {
        float maxX = 0f;
        for (SkillNode n : BioengineeringSkillTreeRegistry.NODES) {
            maxX = Math.max(maxX, n.x + NODE_HALF);
        }

        float totalWidth = Math.max(HELIX_PART_WIDTH * 1.5f, maxX + HELIX_PART_WIDTH * 0.5f);
        float tMax = totalWidth / HELIX_K;

        float step = 0.06f;
        float prevTopX = kx(0) + NODE_HALF, prevTopY = yTop(0) + NODE_HALF;
        float prevBotX = kx(0) + NODE_HALF, prevBotY = yBot(0) + NODE_HALF;
        for (float t = step; t <= tMax + step; t += step) {
            float xTop = kx(t) + NODE_HALF, yTop = yTop(t) + NODE_HALF;
            float xBot = kx(t) + NODE_HALF, yBot = yBot(t) + NODE_HALF;
            drawSegment(graphics, prevTopX, prevTopY, xTop, yTop, 0x40FFFFFF);
            drawSegment(graphics, prevBotX, prevBotY, xBot, yBot, 0x40FFFFFF);
            prevTopX = xTop;
            prevTopY = yTop;
            prevBotX = xBot;
            prevBotY = yBot;
        }
    }

    private void drawSegment(GuiGraphics graphics, float x1, float y1, float x2, float y2, int argb) {
        int steps = (int) Math.max(1, Math.hypot(x2 - x1, y2 - y1));
        for (int i = 0; i <= steps; i++) {
            float t = i / (float) steps;
            int px = Math.round(x1 + t * (x2 - x1));
            int py = Math.round(y1 + t * (y2 - y1));
            graphics.fill(px, py, px + 1, py + 1, argb);
        }
    }

    private boolean isHovering(GeneSlotItemHandler slot, int mouseX, int mouseY) {
        int x = slot.x + leftPos;
        int y = slot.y + topPos;
        return mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16;
    }

    private static float kx(float t) {
        return HELIX_K * t;
    }

    private static float yTop(float t) {
        return HELIX_Y0 + HELIX_AMPLITUDE * (float) Math.sin(t);
    }

    private static float yBot(float t) {
        return HELIX_Y0 - HELIX_AMPLITUDE * (float) Math.sin(t);
    }

    private boolean hasValidDna(DnaImplementation dna) {
        return dna.getGenome() != null && !dna.getGenome().isEmpty();
    }
}
