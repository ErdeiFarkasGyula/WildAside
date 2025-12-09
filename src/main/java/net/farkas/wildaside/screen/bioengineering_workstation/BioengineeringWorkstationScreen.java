package net.farkas.wildaside.screen.bioengineering_workstation;

import com.mojang.blaze3d.systems.RenderSystem;
import net.farkas.wildaside.capability.dna.DnaImplementation;
import net.farkas.wildaside.dna.DnaConstants;
import net.farkas.wildaside.item.custom.DnaHolderItem;
import net.farkas.wildaside.network.NetworkHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class BioengineeringWorkstationScreen extends AbstractContainerScreen<BioengineeringWorkstationMenu> {
    private final BioengineeringWorkstationMenu menu;
    public BioengineeringWorkstationTab tab;
    private static ResourceLocation BACKGROUND;

    private float skillOffsetX = 0;
    private float skillOffsetY = 0;
    private boolean draggingSkillView = false;
    private double lastMouseX, lastMouseY;

    private final int yOffset;

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

        addTabButtons();
        addRecompileButton();
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

                if (!dna.getGenes().isEmpty() && !tag.getBoolean(DnaConstants.SAMPLE_UNUSABLE)) {
                    guiGraphics.blit(BACKGROUND, x + 25, y + 12 + yOffset + i * 22, 0, 248, 8, 8);
                }
            }
        }
    }

    private void renderSkillViewport(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int x = this.leftPos + 8;
        int y = this.topPos + 36;
        int width = 240;
        int height = 150;

        enableScissor(guiGraphics, x, y, width, height);

        guiGraphics.fill(x, y, x + width, y + height, 0x66000000);

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x + skillOffsetX, y + skillOffsetY, 0);

        drawSkillNodes(guiGraphics);

        guiGraphics.pose().popPose();

        disableScissor(guiGraphics);
    }

    private void enableScissor(GuiGraphics graphics, int x, int y, int width, int height) {
        double scale = minecraft.getWindow().getGuiScale();

        int sx = (int)(x * scale);
        int sy = (int)((this.height - (y + height)) * scale);
        int sw = (int)(width * scale);
        int sh = (int)(height * scale);

        RenderSystem.enableScissor(sx, sy, sw, sh);
    }

    private void disableScissor(GuiGraphics graphics) {
        RenderSystem.disableScissor();
    }

    private void drawSkillNodes(GuiGraphics g) {
        for (int i = 0; i < 20; i++) {
            int px = (i % 5) * 25;
            int py = (i / 5) * 25;

            g.fill(px, py, px + 16, py + 16, 0xFF8844FF);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, delta);

        if (tab == BioengineeringWorkstationTab.SKILL_TAB) {
            renderSkillViewport(guiGraphics, mouseX, mouseY);
        }

        renderTooltip(guiGraphics, mouseX, mouseY);
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

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (tab == BioengineeringWorkstationTab.SKILL_TAB && button == 0) {
            int x = this.leftPos + 8;
            int y = this.topPos + 32;
            int width = 240;
            int height = 150;

            if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
                draggingSkillView = true;
                lastMouseX = mouseX;
                lastMouseY = mouseY;
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (draggingSkillView && button == 0) {
            skillOffsetX += (float) (mouseX - lastMouseX);
            skillOffsetY += (float) (mouseY - lastMouseY);

            lastMouseX = mouseX;
            lastMouseY = mouseY;
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            draggingSkillView = false;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }
}
