package net.farkas.wildaside.screen.potion_blaster;

import com.mojang.blaze3d.systems.RenderSystem;
import net.farkas.wildaside.WildAside;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class PotionBlasterScreen extends AbstractContainerScreen<PotionBlasterMenu> {
    private static final ResourceLocation BACKGROUND = new ResourceLocation(WildAside.MOD_ID, "textures/gui/potion_blaster.png");

    public PotionBlasterScreen(PotionBlasterMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();
        this.inventoryLabelY = 9999;
        this.titleLabelY = 9999;
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, BACKGROUND);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        pGuiGraphics.blit(BACKGROUND, x, y, 0, 0, imageWidth, imageHeight);

        renderProgress(pGuiGraphics, x, y);

    }

    private void renderProgress(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.blit(BACKGROUND, x + 106, y + 38, 176, 0, menu.getScaledProgress(), 4);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
