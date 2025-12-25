package net.farkas.wildaside.screen.bioengineering_workstation;

import net.farkas.wildaside.item.ModItems;
import net.farkas.wildaside.screen.VisibleSlotItemHandler;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public class GeneSlotItemHandler extends VisibleSlotItemHandler {
    public GeneSlotItemHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        if (stack.isEmpty()) return false;
        return stack.is(ModItems.GENE.get()) && this.getItemHandler().isItemValid(getSlotIndex(), stack);
    }
}
