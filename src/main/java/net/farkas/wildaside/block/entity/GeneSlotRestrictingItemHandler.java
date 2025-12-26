package net.farkas.wildaside.block.entity;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

import static net.farkas.wildaside.screen.bioengineering_workstation.BioengineeringWorkstationSlots.EDITOR_BOTTOM_GENE_START_INDEX;
import static net.farkas.wildaside.screen.bioengineering_workstation.BioengineeringWorkstationSlots.EDITOR_TOP_GENE_START_INDEX;

public class GeneSlotRestrictingItemHandler extends SidedItemHandler {
    public GeneSlotRestrictingItemHandler(ItemStackHandler delegate, Direction side, Set<Integer> upSlots, Set<Integer> downSlots, Set<Integer> sideSlots, Set<Integer> outputSlots) {
        super(delegate, side, upSlots, downSlots, sideSlots, outputSlots);
    }

    private static boolean isGeneSlot(int slot) {
        int topStart = EDITOR_TOP_GENE_START_INDEX;
        int topEnd = EDITOR_TOP_GENE_START_INDEX + 12;
        int botStart = EDITOR_BOTTOM_GENE_START_INDEX;
        int botEnd = EDITOR_BOTTOM_GENE_START_INDEX + 12;
        return (slot >= topStart && slot <= topEnd) || (slot >= botStart && slot <= botEnd);
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
