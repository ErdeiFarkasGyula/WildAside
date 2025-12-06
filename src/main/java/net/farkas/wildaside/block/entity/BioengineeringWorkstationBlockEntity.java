package net.farkas.wildaside.block.entity;

import net.farkas.wildaside.capability.dna.DnaImplementation;
import net.farkas.wildaside.dna.DnaUtils;
import net.farkas.wildaside.dna.Gene;
import net.farkas.wildaside.dna.trait.Trait;
import net.farkas.wildaside.item.ModItems;
import net.farkas.wildaside.item.custom.DnaHolder;
import net.farkas.wildaside.item.custom.GeneItem;
import net.farkas.wildaside.recipe.BioengineeringWorkstationRecipe;
import net.farkas.wildaside.screen.bioengineering_workstation.BioengineeringWorkstationMenu;
import net.farkas.wildaside.screen.bioengineering_workstation.BioengineeringWorkstationTab;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

import static net.farkas.wildaside.dna.DnaConstants.*;
import static net.farkas.wildaside.screen.bioengineering_workstation.BioengineeringWorkstationSlots.*;

public class BioengineeringWorkstationBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemHandler = new ItemStackHandler(40) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private final IItemHandler automationHandler = new IItemHandler() {
        @Override
        public int getSlots() {
            return itemHandler.getSlots();
        }

        @Override
        public @NotNull ItemStack getStackInSlot(int slot) {
            return itemHandler.getStackInSlot(slot);
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            if (stack.getItem() instanceof GeneItem) return stack.copy();

            return itemHandler.insertItem(slot, stack, simulate);
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            ItemStack existing = itemHandler.getStackInSlot(slot);

            if (existing.getItem() instanceof GeneItem) return ItemStack.EMPTY;

            return itemHandler.extractItem(slot, amount, simulate);
        }

        @Override
        public int getSlotLimit(int slot) {
            return itemHandler.getSlotLimit(slot);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return !(stack.getItem() instanceof GeneItem);
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    public final ContainerData data;

    private int assemblerProgress = 0;
    private int assemblerMaxProgress = 200;

    private int analyserProgress = 0;
    private int analyserMaxProgress = 400;

    private BioengineeringWorkstationTab tab = BioengineeringWorkstationTab.ASSEMBLER;

    public BioengineeringWorkstationBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super (ModBlockEntities.BIOENGINEERING_WORKSTATION.get(), pPos, pBlockState);

        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> BioengineeringWorkstationBlockEntity.this.assemblerProgress;
                    case 1 -> BioengineeringWorkstationBlockEntity.this.assemblerMaxProgress;
                    case 2 -> BioengineeringWorkstationBlockEntity.this.analyserProgress;
                    case 3 -> BioengineeringWorkstationBlockEntity.this.analyserMaxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> BioengineeringWorkstationBlockEntity.this.assemblerProgress = pValue;
                    case 1 -> BioengineeringWorkstationBlockEntity.this.assemblerMaxProgress = pValue;
                    case 2 -> BioengineeringWorkstationBlockEntity.this.analyserProgress = pValue;
                    case 3 -> BioengineeringWorkstationBlockEntity.this.analyserMaxProgress = pValue;
                }
            }

            @Override
            public int getCount() {
                return 4;
            }
        };
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            if (side == null) {
                return LazyOptional.of(() -> itemHandler).cast();
            }

            return LazyOptional.of(() -> automationHandler).cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    public void setTab(BioengineeringWorkstationTab tab) {
        this.tab = tab;
        setChanged();
    }

    public void setTab(int i) {
        this.tab = BioengineeringWorkstationTab.values()[i];
        setChanged();
    }

    public BioengineeringWorkstationTab getTab() {
        return tab;
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    @Override
    public Component getDisplayName() {
        return  Component.translatable("block.wildaside.bioengineering_workstation");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new BioengineeringWorkstationMenu(pContainerId, pPlayerInventory, this, this.data);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);

        pTag.put(INVENTORY, itemHandler.serializeNBT());
        pTag.putInt(ASSEMBLER_PROGRESS, assemblerProgress);
        pTag.putInt(ANALYSER_PROGRESS, analyserProgress);
        pTag.putInt(TAB, tab.ordinal());
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);

        if (pTag.contains(INVENTORY)) {
            itemHandler.deserializeNBT(pTag.getCompound(INVENTORY));
        }
        assemblerProgress = pTag.getInt(ASSEMBLER_PROGRESS);
        analyserProgress = pTag.getInt(ANALYSER_PROGRESS);
        tab = BioengineeringWorkstationTab.values()[pTag.getInt(TAB)];
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        if (hasRecipe()) {
            assemblerProgress++;
            setChanged(pLevel, pPos, pState);

            if (assemblerProgress >= assemblerMaxProgress) {
                craftItem();
                assemblerProgress = 0;
            }
        } else {
            assemblerProgress = 0;
        }

        if (canAnalyse()) {
            analyserProgress++;
            setChanged(pLevel, pPos, pState);

            if (analyserProgress >= analyserMaxProgress) {
                analyseDna();
                analyserProgress = 0;
            }
        } else {
            analyserProgress = 0;
        }
    }

    public static Map<Trait, Gene> orderGenes(DnaImplementation dna) {
        return dna.getGenes().entrySet().stream()
                .sorted(Map.Entry.comparingByKey(Comparator.comparing(Trait::getName)))
                .sorted(Map.Entry.comparingByKey(Comparator.comparing(Trait::getTraitType)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));
    }

    public void analyseDna() {
        ItemStack dnaHolderStack = itemHandler.getStackInSlot(ANA_INPUT_1);

        CompoundTag dnaHolderTag = dnaHolderStack.getOrCreateTag();

        dnaHolderTag.putBoolean(REVEAL_SOURCE, true);
        dnaHolderTag.putBoolean(REVEAL_STABILITY, true);
        dnaHolderTag.putBoolean(REVEAL_TRAITS, true);

        dnaHolderStack.setTag(dnaHolderTag);

        itemHandler.extractItem(ANA_INPUT_1, 1, false);
        itemHandler.extractItem(ANA_INPUT_2, 1, false);
        itemHandler.extractItem(ANA_INPUT_3, 1, false);

        itemHandler.setStackInSlot(ANA_OUTPUT_1, dnaHolderStack);

        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public void recompileDnas() {
        ItemStack inA = itemHandler.getStackInSlot(EDITOR_INPUT_1);
        ItemStack inB = itemHandler.getStackInSlot(EDITOR_INPUT_2);

        if (inA.isEmpty() && inB.isEmpty()) return;

        processDnaSlot(inA, EDITOR_INPUT_1, EDITOR_OUTPUT_1, EDITOR_TOP_GENE_START_INDEX);
        processDnaSlot(inB, EDITOR_INPUT_2, EDITOR_OUTPUT_2, EDITOR_BOTTOM_GENE_START_INDEX);

        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    private void processDnaSlot(ItemStack input, int inputSlot, int outputSlot, int geneStartIndex) {
        if (input.isEmpty() || !(input.getItem() instanceof DnaHolder)) {
            return;
        }

        if (!itemHandler.getStackInSlot(outputSlot).isEmpty()) return;

        ItemStack output = input.copy();
        CompoundTag tag = output.getOrCreateTag();
        CompoundTag dnaTag = tag.getCompound(DNA_DATA);
        DnaImplementation dna = new DnaImplementation();

        if (!dnaTag.isEmpty()) {
            Map<Trait, Gene> genes = new HashMap<>();
            dna.deserializeNBT(dnaTag);

            for (int i = 0; i <= 12; i++) {
                ItemStack geneStack = itemHandler.getStackInSlot(i + geneStartIndex);
                if (geneStack.isEmpty()) continue;
                CompoundTag geneTag = geneStack.getOrCreateTag();

                Gene gene = Gene.deserializeNBT(geneTag);
                Trait trait = gene.getTrait();

                genes.put(trait, new Gene(trait, gene.getAlleleA(), gene.getAlleleB()));
            }

            dna.setGenes(genes);
            tag.remove(DNA_DATA);
            tag.put(DNA_DATA, dna.serializeNBT());
            tag.putBoolean(REVEAL_SOURCE, false);
            tag.putBoolean(REVEAL_STABILITY, false);
            tag.putBoolean(REVEAL_TRAITS, false);
        }

        output.setTag(tag);
        itemHandler.setStackInSlot(outputSlot, output);
        itemHandler.setStackInSlot(inputSlot, ItemStack.EMPTY);
    }

    private void craftItem() {
        Optional<BioengineeringWorkstationRecipe> recipe = getCurrentRecipe();
        ItemStack result = recipe.get().getResultItem(null);

        this.itemHandler.extractItem(ASSE_INPUT_1, 1, false);
        this.itemHandler.extractItem(ASSE_INPUT_2, 1, false);
        this.itemHandler.extractItem(ASSE_INPUT_3, 1, false);
        this.itemHandler.extractItem(ASSE_INPUT_4, 1, false);
        this.itemHandler.extractItem(ASSE_INPUT_5, 1, false);

        this.itemHandler.setStackInSlot(ASSE_OUTPUT_1, new ItemStack(result.getItem(), this.itemHandler.getStackInSlot(ASSE_OUTPUT_1).getCount() + result.getCount()));
    }

    private boolean hasRecipe() {
        Optional<BioengineeringWorkstationRecipe> recipe = getCurrentRecipe();

        if (recipe.isEmpty()) return false;

        ItemStack result = recipe.get().getResultItem(getLevel().registryAccess());

        return canInsertAmountIntoOutputSlot(result.getCount()) && canInsertItemIntoOutputSlot(result.getItem());
    }

    private Optional<BioengineeringWorkstationRecipe> getCurrentRecipe() {
        SimpleContainer inventory = new SimpleContainer(6);

        for (int i = 0; i < 6; i++) {
            inventory.setItem(i, this.itemHandler.getStackInSlot(i));
        }

        return this.level.getRecipeManager().getRecipeFor(BioengineeringWorkstationRecipe.Type.INSTANCE, inventory, level);
    }

    private boolean canInsertItemIntoOutputSlot(Item item) {
        return this.itemHandler.getStackInSlot(ASSE_OUTPUT_1).isEmpty() || this.itemHandler.getStackInSlot(ASSE_OUTPUT_1).is(item);
    }

    private boolean canInsertAmountIntoOutputSlot(int count) {
        return this.itemHandler.getStackInSlot(ASSE_OUTPUT_1).getCount() + count <= this.itemHandler.getStackInSlot(ASSE_OUTPUT_1).getMaxStackSize();
    }

    private boolean canAnalyse() {
        ItemStack dnaHolderStack = itemHandler.getStackInSlot(ANA_INPUT_1);
        ItemStack detergentStack = itemHandler.getStackInSlot(ANA_INPUT_2);
        ItemStack stabiliserStack = itemHandler.getStackInSlot(ANA_INPUT_3);

        if (dnaHolderStack.isEmpty() || detergentStack.isEmpty() || stabiliserStack.isEmpty()) return false;

        if (!detergentStack.is(ModItems.ENTORIUM.get()) || !stabiliserStack.is(ModItems.VIBRION.get())) return false;
        if (!dnaHolderStack.is(ModItems.DNA_HOLDER.get())) return false;

        CompoundTag tag = dnaHolderStack.getOrCreateTag();
        boolean unusable = tag.getBoolean(SAMPLE_UNUSABLE);

        return !unusable;
    }
}