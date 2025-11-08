package net.farkas.wildaside.screen.bioengineering_workstation;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ModTabButton extends Button {
    private static final ResourceLocation TABS_TEXTURE = new ResourceLocation("minecraft", "textures/gui/container/creative_inventory/tabs.png");

    private final ResourceLocation icon;
    private final boolean leftSide;
    private boolean activeTab;

    public ModTabButton(int x, int y, boolean leftSide, ResourceLocation icon, OnPress onPress) {
        super(Button.builder(Component.empty(), onPress)
                .pos(x, y)
                .size(28, 32));

        this.icon = icon;
        this.leftSide = leftSide;
    }

    public void setActive(boolean active) {
        this.activeTab = active;
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.setShaderTexture(0, TABS_TEXTURE);

        // Choose UVs from creative tabs texture
        int u = leftSide ? 0 : 28;
        int v = activeTab ? 32 : 0; // active tab uses lower row (pushed up look)

        graphics.blit(TABS_TEXTURE, getX(), getY(), u, v, this.width, this.height);

        // Draw icon in center (16x16)
        RenderSystem.setShaderTexture(0, icon);
        int iconX = this.getX() + (this.width - 16) / 2;
        int iconY = this.getY() + 8;
        graphics.blit(icon, iconX, iconY, 0, 0, 16, 16, 16, 16);
    }
}