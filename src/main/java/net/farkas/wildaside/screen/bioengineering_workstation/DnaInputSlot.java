package net.farkas.wildaside.screen.bioengineering_workstation;

import net.farkas.wildaside.item.ModItems;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class DnaInputSlot extends SlotItemHandler {
    private final BioengineeringWorkstationMenu menu;
    private final int index;

    public DnaInputSlot(BioengineeringWorkstationMenu menu, IItemHandler itemHandler, int index, int xPosition, int yPosition) {
       super(itemHandler, index, xPosition, yPosition);
       this.menu = menu;
       this.index = index;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return stack.is(ModItems.DNA_HOLDER.get());
    }

    @Override
    public void setChanged() {
        if (menu == null) return;
        menu.clearGenes();
        int otherIndex = index == 7 ? 8 : 7;
        menu.loadGenes(index);
        menu.loadGenes(otherIndex);
        super.setChanged();
    }
}
