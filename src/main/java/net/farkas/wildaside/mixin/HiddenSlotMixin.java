package net.farkas.wildaside.mixin;

import net.farkas.wildaside.screen.ModVisibleSlot;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public class HiddenSlotMixin {
    @Inject(method = "renderSlot", at = @At("HEAD"), cancellable = true)
    private void handleHiddenSlot(GuiGraphics pGuiGraphics, Slot pSlot, CallbackInfo ci) {
        if (pSlot instanceof ModVisibleSlot modVisibleSlot && !modVisibleSlot.isActive()) {
            ci.cancel();
        }
    }
}
