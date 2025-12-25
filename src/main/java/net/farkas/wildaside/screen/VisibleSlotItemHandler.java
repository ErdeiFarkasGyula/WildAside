package net.farkas.wildaside.screen;

import net.farkas.wildaside.item.ModItems;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class VisibleSlotItemHandler extends SlotItemHandler {
    public boolean active = true;

    public VisibleSlotItemHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return active && super.mayPlace(stack) && !stack.is(ModItems.GENE.get());
    }

    @Override
    public boolean mayPickup(Player playerIn) {
        return active && super.mayPickup(playerIn);
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public boolean isHighlightable() {
        return active;
    }

    public void setVisible(boolean active) {
        this.active = active;
    }
}
