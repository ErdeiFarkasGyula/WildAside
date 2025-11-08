package net.farkas.wildaside.screen.bioengineering_workstation;

import com.mojang.blaze3d.systems.RenderSystem;
import net.farkas.wildaside.WildAside;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class BioengineeringWorkstationScreen extends AbstractContainerScreen<BioengineeringWorkstationMenu> {
    private static final ResourceLocation BACKGROUND = new ResourceLocation(WildAside.MOD_ID, "textures/gui/bioengineering_workstation.png");
    private static final ResourceLocation SEQUENCER = new ResourceLocation(WildAside.MOD_ID, "textures/gui/bioengineering_workstation_sequencer_full.png");

    private int tab = 0;

    public BioengineeringWorkstationScreen(BioengineeringWorkstationMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        this.imageWidth = 256;
        super.init();
        this.inventoryLabelY = 9999;
        this.titleLabelY = 9999;

        addTabButtons();
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, BACKGROUND);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        ResourceLocation background = tab == 0 ? BACKGROUND : SEQUENCER;
        guiGraphics.blit(background, x, y, 0, 0, imageWidth, imageHeight);

        renderProgressArrow(guiGraphics, x, y);
    }

    private void switchTab(int i) {
        tab = i;
    }

    private void renderProgressArrow(GuiGraphics guiGraphics, int x, int y) {
        if (menu.isCrafting()) {
            guiGraphics.blit(BACKGROUND, x + 129, y + 37, 0, 248, menu.getScaledProgress(), 8);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, delta);
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
            addTabButtons();
        }
    }

    private void addTabButtons() {
        this.clearWidgets();
        this.addWidget(Button.builder(Component.literal("A"), b -> switchTab(0))
                .pos(this.leftPos + 15, this.topPos + 87)
                .size(26, 25)
                .tooltip(Tooltip.create(Component.literal("A")))
                .build());

        this.addWidget(Button.builder(Component.literal("B"), b -> switchTab(1))
                .pos(this.leftPos + 15, this.topPos + 114)
                .size(26, 25)
                .tooltip(Tooltip.create(Component.literal("B")))
                .build());
    }
}
