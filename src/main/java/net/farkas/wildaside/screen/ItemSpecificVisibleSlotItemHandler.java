package net.farkas.wildaside.screen;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ItemSpecificVisibleSlotItemHandler extends VisibleSlotItemHandler {
    private final List<ItemLike> allowedItems;

    public ItemSpecificVisibleSlotItemHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition, List<ItemLike> allowedItems) {
        super(itemHandler, index, xPosition, yPosition);
        this.allowedItems = allowedItems;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return super.mayPlace(stack) && allowedItems.contains(stack.getItem());
    }
}
