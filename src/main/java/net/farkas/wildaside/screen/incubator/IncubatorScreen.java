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
    private static final ResourceLocation BG = new ResourceLocation(WildAside.MOD_ID, "textures/gui/incubator.png");
    private Button minus;
    private Button plus;
    private Button openBtn;

    public IncubatorScreen(IncubatorMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
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
    protected void renderBg(GuiGraphics gg, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, BG);
        gg.blit(BG, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        int heat = menu.getData().get(4); // 0-4
        int barHeight = (int) ((heat / 4f) * 52);
        if (barHeight > 0) {
            gg.blit(BG, leftPos + 148, topPos + 36 + (52 - barHeight), 176, 52 - barHeight, 8, barHeight);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics gg, int mouseX, int mouseY) {
        gg.drawString(font, title, 8, 6, 0x404040, false);
        gg.drawString(font, Component.literal("Heat"), 140, 10, 0xFFFFFF, false);
    }

    @Override
    public void render(GuiGraphics gg, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(gg);
        super.render(gg, mouseX, mouseY, partialTicks);
        this.renderTooltip(gg, mouseX, mouseY);
    }
}