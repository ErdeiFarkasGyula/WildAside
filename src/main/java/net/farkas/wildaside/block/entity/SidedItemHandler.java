package net.farkas.wildaside.block.entity;

import net.farkas.wildaside.screen.bioengineering_workstation.BioengineeringWorkstationSlots;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

import static net.farkas.wildaside.screen.bioengineering_workstation.BioengineeringWorkstationSlots.EDITOR_BOTTOM_GENE_START_INDEX;
import static net.farkas.wildaside.screen.bioengineering_workstation.BioengineeringWorkstationSlots.EDITOR_TOP_GENE_START_INDEX;

public class SidedItemHandler implements IItemHandler {
    private final ItemStackHandler delegate;
    private final Direction side;

    private final Set<Integer> upSlots;
    private final Set<Integer> downSlots;
    private final Set<Integer> sideSlots;

    private final Set<Integer> outputSlots;

    private static boolean isGeneSlot(int slot) {
        int topStart = EDITOR_TOP_GENE_START_INDEX;
        int topEnd = EDITOR_TOP_GENE_START_INDEX + 12;
        int botStart = EDITOR_BOTTOM_GENE_START_INDEX;
        int botEnd = EDITOR_BOTTOM_GENE_START_INDEX + 12;
        return (slot >= topStart && slot <= topEnd) || (slot >= botStart && slot <= botEnd);
    }

    public SidedItemHandler(ItemStackHandler delegate, Direction side, Set<Integer> upSlots, Set<Integer> downSlots, Set<Integer> sideSlots, Set<Integer> outputSlots) {
        this.delegate = delegate;
        this.side = side;
        this.upSlots = upSlots;
        this.downSlots = downSlots;
        this.sideSlots = sideSlots;
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
        if (!canInsertForSide(slot) || isGeneSlot(slot) || BioengineeringWorkstationSlots.OUTPUTS.contains(slot)) {
            return stack;

        }
        return delegate.insertItem(slot, stack, simulate);
    }

       @Override
       public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (!outputSlots.contains(slot)) return ItemStack.EMPTY;
            if (isGeneSlot(slot)) return ItemStack.EMPTY;

            return delegate.extractItem(slot, amount, simulate);
        }

    @Override
    public int getSlotLimit(int slot) {
        return delegate.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return canInsertForSide(slot) && !isGeneSlot(slot) && !BioengineeringWorkstationSlots.OUTPUTS.contains(slot);
    }

    private boolean canInsertForSide(int slot) {
        switch (side) {
            case UP -> {
                return upSlots.contains(slot);
            }
            case DOWN -> {
                return downSlots.contains(slot);
            }
            default -> {
                return sideSlots.contains(slot);
            }
        }

    }
}
