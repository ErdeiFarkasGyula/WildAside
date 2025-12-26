package net.farkas.wildaside.block.entity;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

public class SidedItemHandler implements IItemHandler {
    private final ItemStackHandler delegate;
    private final Direction side;
    private final Map<Direction, Set<Integer>> insertSlotsBySide;
    private final Set<Integer> outputSlots;

    public SidedItemHandler(ItemStackHandler delegate, Direction side, Map<Direction, Set<Integer>> insertSlotsBySide, Set<Integer> outputSlots) {
        this.delegate = delegate;
        this.side = side;
        this.insertSlotsBySide = insertSlotsBySide;
        this.outputSlots = outputSlots;
    }

    @Override
    public int getSlots() {
        return delegate.getSlots();
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        return delegate.getStackInSlot(slot);
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (!canInsertForSide(slot) || outputSlots.contains(slot)) {
            return stack;
        }
        return delegate.insertItem(slot, stack, simulate);
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (!outputSlots.contains(slot)) return ItemStack.EMPTY;
        return delegate.extractItem(slot, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return delegate.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return canInsertForSide(slot) && !outputSlots.contains(slot);
    }

    private boolean canInsertForSide(int slot) {
        Set<Integer> allowed = insertSlotsBySide.getOrDefault(side, Set.of());
        return allowed.contains(slot);
    }
}