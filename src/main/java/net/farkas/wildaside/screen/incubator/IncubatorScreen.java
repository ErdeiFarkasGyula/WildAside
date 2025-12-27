package net.farkas.wildaside.screen.incubator;

import com.mojang.blaze3d.systems.RenderSystem;
import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.network.NetworkHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class IncubatorScreen extends AbstractContainerScreen<IncubatorMenu> {
    private static final ResourceLocation BACKGROUND = new ResourceLocation(WildAside.MOD_ID, "textures/gui/incubator.png");
    private Button minus;
    private Button plus;
    private Button openBtn;

    public IncubatorScreen(IncubatorMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.titleLabelY = 9999;
    }

    @Override
    protected void init() {
        super.init();
        int x = leftPos;
        int y = topPos;

        minus = addRenderableWidget(Button.builder(Component.literal("-"), b -> changeHeat(-1)).bounds(x + 140, y + 20, 12, 12).build());
        plus = addRenderableWidget(Button.builder(Component.literal("+"), b -> changeHeat(+1)).bounds(x + 156, y + 20, 12, 12).build());
        openBtn = addRenderableWidget(Button.builder(Component.literal("Open"), b -> toggleOpen()).bounds(x + 130, y + 50, 38, 14).build());
    }

    private void changeHeat(int delta) {
        int current = menu.getData().get(4);
        int next = Math.max(0, Math.min(4, current + delta));
        if (next != current) NetworkHandler.sendSetIncubatorHeatLevelPacket(menu.getBlockEntity().getBlockPos(), next);
    }

    private void toggleOpen() {
        NetworkHandler.sendToggleIncubatorOpenPacket(menu.getBlockEntity().getBlockPos());
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, BACKGROUND);
        graphics.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        drawHeatMeter(graphics, partialTicks);
    }

    private void drawHeatMeter(GuiGraphics graphics, float partialTicks) {
        int heat = menu.getData().get(4);
        int burnTime = menu.getData().get(0);
        if (heat <= 0 || burnTime <= 0) return;

        int maxH = 52;
        int barHeight = (int) ((heat / 4f) * maxH);

        float wobble = (float) Math.sin((minecraft.level != null ? minecraft.level.getGameTime() : 0) * 0.2f + partialTicks) * 2f;
        barHeight = Math.max(0, Math.min(maxH, barHeight + (int) wobble));
        if (barHeight <= 0) return;

        int x1 = leftPos + 148;
        int y1 = topPos + 36 + (maxH - barHeight);
        int x2 = x1 + 8;
        int y2 = topPos + 36 + maxH;

        int bottom = 0xFFB24A00;
        int top = 0xFFFFCC33;
        graphics.fillGradient(x1, y1, x2, y2, top, bottom);

        graphics.fillGradient(x1 + 1, y1, x2 - 1, y1 + Math.min(6, barHeight), 0xFFFFEE77, 0x00FFFFEE);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, 8, 6, 0x404040, false);
        graphics.drawString(font, Component.literal("Heat"), 140, 10, 0xFFFFFF, false);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);
    }
}