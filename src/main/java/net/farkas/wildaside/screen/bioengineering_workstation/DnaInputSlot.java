package net.farkas.wildaside.screen.bioengineering_workstation;

import net.farkas.wildaside.block.entity.BioengineeringWorkstationBlockEntity;
import net.farkas.wildaside.item.ModItems;
import net.farkas.wildaside.screen.ModVisibleSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class DnaInputSlot extends ModVisibleSlot {
    private final BioengineeringWorkstationMenu menu;
    private final int index;

    public DnaInputSlot(BioengineeringWorkstationMenu menu, IItemHandler itemHandler, int index, int xPosition, int yPosition) {
       super(itemHandler, index, xPosition, yPosition);
       this.menu = menu;
       this.index = index;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return stack.is(ModItems.DNA_HOLDER.get()) && super.mayPlace(stack);
    }

    @Override
    public void setChanged() {
        if (menu == null) return;
        menu.clearGenes();
        int otherIndex = index == BioengineeringWorkstationBlockEntity.DNA_INPUT_1
                ? BioengineeringWorkstationBlockEntity.DNA_INPUT_2
                : BioengineeringWorkstationBlockEntity.DNA_INPUT_1;
        menu.loadGenes(index);
        menu.loadGenes(otherIndex);
        super.setChanged();
    }
}
