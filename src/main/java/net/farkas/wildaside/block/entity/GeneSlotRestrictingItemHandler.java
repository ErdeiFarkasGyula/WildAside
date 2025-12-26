package net.farkas.wildaside.block.entity;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

public class GeneSlotRestrictingItemHandler extends SidedItemHandler {
    private final int topStart;
    private final int bottomStart;

    public GeneSlotRestrictingItemHandler(ItemStackHandler delegate, Direction side, Map<Direction, Set<Integer>> insertSlotsBySide, Set<Integer> outputSlots, int topStart, int bottomStart) {
        super(delegate, side, insertSlotsBySide, outputSlots);
        this.topStart = topStart;
        this.bottomStart = bottomStart;
    }

    private boolean isGeneSlot(int slot) {
        int topEnd = topStart + 12;
        int botEnd = bottomStart + 12;
        return (slot >= topStart && slot <= topEnd) || (slot >= bottomStart && slot <= botEnd);
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (isGeneSlot(slot)) {
            return stack;
        }

        return super.insertItem(slot, stack, simulate);
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (isGeneSlot(slot)) {
            return ItemStack.EMPTY;
        }

        return super.extractItem(slot, amount, simulate);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        if (isGeneSlot(slot)) {
            return false;
        }

        return super.isItemValid(slot, stack);
    }
}
