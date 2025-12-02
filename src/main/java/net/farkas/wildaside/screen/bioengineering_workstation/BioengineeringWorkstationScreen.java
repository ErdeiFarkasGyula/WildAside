package net.farkas.wildaside.screen.bioengineering_workstation;

import com.mojang.blaze3d.systems.RenderSystem;
import net.farkas.wildaside.network.NetworkHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class BioengineeringWorkstationScreen extends AbstractContainerScreen<BioengineeringWorkstationMenu> {
    private final BioengineeringWorkstationMenu menu;
    private BioengineeringWorkstationTab tab;
    private static ResourceLocation BACKGROUND;

    public BioengineeringWorkstationScreen(BioengineeringWorkstationMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        menu = pMenu;
    }

    @Override
    protected void init() {
        this.imageWidth = 256;
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

        renderProgressArrow(guiGraphics, x, y);
    }

    private void renderProgressArrow(GuiGraphics guiGraphics, int x, int y) {
        if (menu.isCrafting() && tab == BioengineeringWorkstationTab.ASSEMBLER) {
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

        clearWidgets();
        addRecompileButton();
        addTabButtons();

    }

    private void addTabButtons() {
        this.addWidget(Button.builder(Component.empty(), b -> switchTab(BioengineeringWorkstationTab.ASSEMBLER))
                .pos(this.leftPos + 15, this.topPos + 87)
                .size(26, 25)
                .tooltip(Tooltip.create(Component.translatable("gui.wildaside.bioengineering_workstation.bio_assembler")))
                .build());

        this.addWidget(Button.builder(Component.empty(), b -> switchTab(BioengineeringWorkstationTab.DNA_EDITOR))
                .pos(this.leftPos + 15, this.topPos + 114)
                .size(26, 25)
                .tooltip(Tooltip.create(Component.translatable("gui.wildaside.bioengineering_workstation.dna_editor")))
                .build());
    }

    public void addRecompileButton() {
        if (tab == BioengineeringWorkstationTab.DNA_EDITOR) {
            this.addRenderableWidget(Button.builder(Component.literal("="), btn -> {
                        NetworkHandler.sendBioengineeringWorkstationRecompileGenesPacket(menu.blockEntity.getBlockPos());
                    }).pos(leftPos + 14, topPos + 55).size(20, 20)
                    .tooltip(Tooltip.create(Component.translatable("gui.wildaside.bioengineering_workstation.recompile_dna"))).build());
        }
    }
}
