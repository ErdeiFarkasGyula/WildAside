package net.farkas.wildaside.block.entity;

import net.farkas.wildaside.capability.dna.DnaImplementation;
import net.farkas.wildaside.dna.Gene;
import net.farkas.wildaside.dna.traits.Trait;
import net.farkas.wildaside.dna.traits.Traits;
import net.farkas.wildaside.item.custom.DnaHolder;
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
import net.minecraft.world.inventory.Slot;
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

public class  BioengineeringWorkstationBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemHandler = new ItemStackHandler(70);

    private static final int INPUT_1 = 0;
    private static final int INPUT_2 = 1;
    private static final int INPUT_3 = 2;
    private static final int INPUT_4 = 3;
    private static final int INPUT_5 = 4;
    private static final int OUTPUT_1 = 5;

    private static final int DNA_INPUT_1 = 7;
    private static final int DNA_INPUT_2 = 8;
    private static final int DNA_OUTPUT_1 = 9;
    private static final int DNA_OUTPUT_2 = 10;

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 200;

    private BioengineeringWorkstationTab tab = BioengineeringWorkstationTab.ASSEMBLER;

    public BioengineeringWorkstationBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super (ModBlockEntities.BIOENGINEERING_WORKSTATION.get(), pPos, pBlockState);

        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> BioengineeringWorkstationBlockEntity.this.progress;
                    case 1 -> BioengineeringWorkstationBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> BioengineeringWorkstationBlockEntity.this.progress = pValue;
                    case 1 -> BioengineeringWorkstationBlockEntity.this.maxProgress = pValue;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
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
        return Component.translatable("block.wildaside.bioengineering_workstation");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new BioengineeringWorkstationMenu(pContainerId, pPlayerInventory, this, this.data);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.put("inventory", itemHandler.serializeNBT());
        pTag.putInt("bioengineering_workstation.progress", progress);
        pTag.putInt("bioengineering_workstation.tab", tab.ordinal());

        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        itemHandler.deserializeNBT(pTag.getCompound("inventory"));
        progress = pTag.getInt("bioengineering_workstation.progress");
        tab = BioengineeringWorkstationTab.values()[pTag.getInt("bioengineering_workstation.tab")];
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        if (hasRecipe()) {
            increaseCraftingProgress();
            setChanged(pLevel, pPos, pState);

            if (hasProgressFinished()) {
                craftItem();
                resetProgress();
            }
        } else {
            resetProgress();
        }
    }

    public static Map<Trait, Gene> orderGenes(DnaImplementation dna) {
        return dna.genes().entrySet().stream()
                .sorted(Map.Entry.comparingByKey(Comparator.comparing(Trait::name)))
                .sorted(Map.Entry.comparingByKey(Comparator.comparing(Trait::traitType)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));
    }

    public void recompileDnas() {
        ItemStack inA = itemHandler.getStackInSlot(DNA_INPUT_1);
        ItemStack inB = itemHandler.getStackInSlot(DNA_INPUT_2);

        if (inA.isEmpty() && inB.isEmpty()) return;

        processDnaSlot(inA, DNA_INPUT_1, DNA_OUTPUT_1, BioengineeringWorkstationMenu.TOP_GENE_INDEX_START);
        processDnaSlot(inB, DNA_INPUT_2, DNA_OUTPUT_2, BioengineeringWorkstationMenu.BOT_GENE_INDEX_START);

        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    private void processDnaSlot(ItemStack input, int inputSlot, int outputSlot, int geneStartIndex) {
        if (input.isEmpty() || !(input.getItem() instanceof DnaHolder)) {
            return;
        }

        ItemStack output = input.copy();
        CompoundTag tag = output.getOrCreateTag();
        CompoundTag dnaTag = tag.getCompound("dna_data");
        DnaImplementation dna = new DnaImplementation();

        if (!dnaTag.isEmpty()) {
            Map<Trait, Gene> genes = new HashMap<>();
            dna.deserializeNBT(dnaTag);

            for (int i = 0; i <= 12; i++) {
                ItemStack geneStack = itemHandler.getStackInSlot(i + geneStartIndex);
                CompoundTag geneTag = geneStack.getOrCreateTag();

                Trait trait = Traits.getByName(geneTag.getString("trait"));
                float value = geneTag.getFloat("value");

                if (trait == null) {
                    trait = Traits.FIRE_RESISTANCE;
                }

                genes.put(trait, new Gene(trait, value, trait.baseInstability()));
            }

            dna.setGenes(genes);
            tag.remove("dna_data");
            tag.put("dna_data", dna.serializeNBT());
        }

        output.setTag(tag);
        itemHandler.setStackInSlot(outputSlot, output);
        itemHandler.setStackInSlot(inputSlot, ItemStack.EMPTY);
    }

    private void resetProgress() {
        progress = 0;
    }

    private void craftItem() {
        Optional<BioengineeringWorkstationRecipe> recipe = getCurrentRecipe();
        ItemStack result = recipe.get().getResultItem(null);

        this.itemHandler.extractItem(INPUT_1, 1, false);
        this.itemHandler.extractItem(INPUT_2, 1, false);
        this.itemHandler.extractItem(INPUT_3, 1, false);
        this.itemHandler.extractItem(INPUT_4, 1, false);
        this.itemHandler.extractItem(INPUT_5, 1, false);

        this.itemHandler.setStackInSlot(OUTPUT_1, new ItemStack(result.getItem(), this.itemHandler.getStackInSlot(OUTPUT_1).getCount() + result.getCount()));
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
        return this.itemHandler.getStackInSlot(OUTPUT_1).isEmpty() || this.itemHandler.getStackInSlot(OUTPUT_1).is(item);
    }

    private boolean canInsertAmountIntoOutputSlot(int count) {
        return this.itemHandler.getStackInSlot(OUTPUT_1).getCount() + count <= this.itemHandler.getStackInSlot(OUTPUT_1).getMaxStackSize();
    }

    private boolean hasProgressFinished() {
        return progress >= maxProgress;
    }

    private void increaseCraftingProgress() {
        progress++;
    }
}