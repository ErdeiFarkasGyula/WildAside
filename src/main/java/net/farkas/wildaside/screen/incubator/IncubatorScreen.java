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
    private boolean cachedOpen;

    public IncubatorScreen(IncubatorMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    @Override
    protected void init() {
        super.init();
        int x = leftPos;
        int y = topPos;

        this.imageWidth = 176;
        this.imageHeight = 166;

        this.inventoryLabelY = 9999;
        this.titleLabelY = 9999;

        minus = addRenderableWidget(Button.builder(Component.literal("-"), b -> changeHeat(-1)).bounds(x + 140, y + 20, 12, 12).build());
        plus = addRenderableWidget(Button.builder(Component.literal("+"), b -> changeHeat(+1)).bounds(x + 156, y + 20, 12, 12).build());

        cachedOpen = isOpenFlag();
        openBtn = addRenderableWidget(Button.builder(labelForOpen(cachedOpen), b -> toggleOpen()).bounds(x + 130, y + 50, 38, 14).build());
    }

    private void changeHeat(int delta) {
        int current = menu.getData().get(4);
        int next = Math.max(0, Math.min(4, current + delta));
        if (next != current) NetworkHandler.sendSetIncubatorHeatLevelPacket(menu.getBlockEntity().getBlockPos(), next);
    }


    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, BACKGROUND);
        graphics.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        drawHeatMeter(graphics, partialTicks);
        drawFuelMeter(graphics);
    }

    private void drawHeatMeter(GuiGraphics graphics, float partialTicks) {
        int heat = menu.getData().get(4);
        int burnTime = menu.getData().get(0);
        if (burnTime <= 0) return;

        int maxW = 52;
        int maxLevels = 4;
        int filled = Math.max(0, Math.min(maxW, (int) ((heat / (float) maxLevels) * maxW)));

        int barHeight = 6;
        int x1 = leftPos + 118;
        int y1 = topPos + 24;
        int x2 = x1 + filled;
        int y2 = y1 + barHeight;

        int leftColor = 0xFFFFCC33;
        int rightColor = 0xFFB24A00;
        graphics.fillGradient(x1, y1, x2, y2, leftColor, rightColor);
    }

    private void drawFuelMeter(GuiGraphics graphics) {
        int burn = menu.getData().get(0);
        int burnTotal = menu.getData().get(1);
        if (burn <= 0 || burnTotal <= 0) return;

        int maxH = 52;
        int barHeight = Math.max(0, Math.min(maxH, (int) ((burn / (float) burnTotal) * maxH)));
        if (barHeight == 0) return;

        int x1 = leftPos + 24;
        int y1 = topPos + 36 + (maxH - barHeight);
        int x2 = x1 + 8;
        int y2 = topPos + 36 + maxH;

        int bottom = 0xFF774000;
        int top = 0xFFFF8800;
        graphics.fillGradient(x1, y1, x2, y2, top, bottom);
        graphics.fillGradient(x1 + 1, y1, x2 - 1, y1 + Math.min(6, barHeight), 0xFFFFCC55, 0x00FFFFCC);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, Component.translatable("gui.wildaside.incubator.heat"), 118, 12, 0xFFFFFF, false);
        graphics.drawString(font, Component.translatable("gui.wildaside.incubator.fuel"), 24, 10, 0xFFFFFF, false);

        boolean hasDna = menu.getBlockEntity().blobHasDna();
        int color = hasDna ? 0x7CFC00 : 0xFF5555;
        graphics.drawString(font, Component.translatable("gui.wildaside.incubator.blob_dna"), 118, 40, color, false);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    private void toggleOpen() {
        cachedOpen = !cachedOpen;
        openBtn.setMessage(labelForOpen(cachedOpen));
        NetworkHandler.sendToggleIncubatorOpenPacket(menu.getBlockEntity().getBlockPos());
    }

    private boolean isOpenFlag() {
        return menu.getData().get(8) != 0;
    }

    private Component labelForOpen(boolean open) {
        return open ? Component.translatable("general.wildaside.close") : Component.translatable("general.wildaside.open");
    }
}