package net.farkas.wildaside.block.entity;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class OutputRestrictingItemHandler implements IItemHandler {
    private final ItemStackHandler delegate;

    private final Set<Integer> outputSlots;

    public OutputRestrictingItemHandler(ItemStackHandler delegate, Set<Integer> outputSlots) {
        this.delegate = delegate;
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
        if (outputSlots.contains(slot)) {
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
        return !outputSlots.contains(slot);
    }
}
