package net.farkas.wildaside.mixin;

import net.farkas.wildaside.item.custom.SyringeItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Inject(method = "isSameItemSameTags", at = @At("HEAD"), cancellable = true)
    private static void preventSyringeEquipAnimation(ItemStack stack1, ItemStack stack2, CallbackInfoReturnable<Boolean> cir) {
        if (stack1.getItem() instanceof SyringeItem && stack2.getItem() instanceof SyringeItem) {
            cir.setReturnValue(true);
        }
    }
}