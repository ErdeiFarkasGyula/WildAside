package net.farkas.wildaside.screen.potion_blaster;

import com.mojang.blaze3d.systems.RenderSystem;
import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.block.entity.PotionBlasterBlockEntity;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;

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

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        this.addRenderableWidget(Button.builder(Component.literal("X"), (pButton -> {
            onButtonClick();
        })).bounds(x + 98, y + 52, 10, 10).build());
    }

    private void onButtonClick() {
        System.out.println("MEOW!!!");
        
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

    private void renderProgress(GuiGraphics gui, int x, int y) {
        final int bottomX = x + 140;
        final int bottomY = y + 67;
        final int width   = 4;

        int rgb = this.menu.data.get(2) & 0xFFFFFF;
        int argb = 0xFF000000 | rgb;

        int filled = menu.getScaledProgress();

        gui.fill(bottomX, bottomY - filled,bottomX + width, bottomY, argb);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
