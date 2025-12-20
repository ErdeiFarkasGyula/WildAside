package net.farkas.wildaside.screen;

import net.farkas.wildaside.item.ModItems;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ModVisibleSlot extends Slot {
    public boolean active = true;

    public ModVisibleSlot(Inventory inventory, int index, int xPosition, int yPosition) {
        super(inventory, index, xPosition, yPosition);
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
