package net.farkas.wildaside.screen.bioengineering_workstation;

import net.farkas.wildaside.item.ModItems;
import net.farkas.wildaside.screen.ModVisibleSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class GeneSlot extends ModVisibleSlot {
    public GeneSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        if (stack.isEmpty()) return false;
        return stack.is(ModItems.GENE.get()) && this.getItemHandler().isItemValid(getSlotIndex(), stack);
    }
}
