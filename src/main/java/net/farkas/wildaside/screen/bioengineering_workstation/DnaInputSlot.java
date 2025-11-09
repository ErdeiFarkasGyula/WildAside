package net.farkas.wildaside.screen.bioengineering_workstation;

import net.farkas.wildaside.item.ModItems;
import net.farkas.wildaside.network.NetworkHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class DnaInputSlot extends SlotItemHandler {
    public DnaInputSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return stack.is(ModItems.DNA_HOLDER.get());
    }
}
