package net.farkas.wildaside.block.entity;

import net.farkas.wildaside.recipe.BioengineeringWorkstationRecipe;
import net.farkas.wildaside.recipe.BioengineeringWorkstationRecipeInput;
import net.farkas.wildaside.recipe.ModRecipes;
import net.farkas.wildaside.screen.bioengineering_workstation.BioengineeringWorkstationMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
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
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
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

import java.util.List;
import java.util.Optional;

public class  BioengineeringWorkstationBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemHandler = new ItemStackHandler(6);

    private static final int INPUT_1 = 0;
    private static final int INPUT_2 = 1;
    private static final int INPUT_3 = 2;
    private static final int INPUT_4 = 3;
    private static final int INPUT_5 = 4;
    private static final int OUTPUT_1 = 5;

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 200;

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
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);
        pTag.put("inventory", itemHandler.serializeNBT(pRegistries));
        pTag.putInt("bioengineering_workstation.progress", progress);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        itemHandler.deserializeNBT(pRegistries, pTag.getCompound("inventory"));
        progress = pTag.getInt("bioengineering_workstation.progress");
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

    private void resetProgress() {
        progress = 0;
    }

    private void craftItem() {
        Optional<RecipeHolder<BioengineeringWorkstationRecipe>> recipe = getCurrentRecipe();
        ItemStack result = recipe.get().value().output();

        this.itemHandler.extractItem(INPUT_1, 1, false);
        this.itemHandler.extractItem(INPUT_2, 1, false);
        this.itemHandler.extractItem(INPUT_3, 1, false);
        this.itemHandler.extractItem(INPUT_4, 1, false);
        this.itemHandler.extractItem(INPUT_5, 1, false);

        this.itemHandler.setStackInSlot(OUTPUT_1, new ItemStack(result.getItem(), this.itemHandler.getStackInSlot(OUTPUT_1).getCount() + result.getCount()));
    }

    private boolean hasRecipe() {
        Optional<RecipeHolder<BioengineeringWorkstationRecipe>> recipe = getCurrentRecipe();

        if (recipe.isEmpty()) return false;

        ItemStack result = recipe.get().value().output();
        return canInsertAmountIntoOutputSlot(result.getCount()) && canInsertItemIntoOutputSlot(result.getItem());
    }

    private Optional<RecipeHolder<BioengineeringWorkstationRecipe>> getCurrentRecipe() {
        var list = List.of(itemHandler.getStackInSlot(INPUT_1),
                itemHandler.getStackInSlot(INPUT_2),
                itemHandler.getStackInSlot(INPUT_3),
                itemHandler.getStackInSlot(INPUT_4),
                itemHandler.getStackInSlot(INPUT_5));
        return this.level.getRecipeManager().getRecipeFor(ModRecipes.BIOENGINEERING_TYPE.get(), new BioengineeringWorkstationRecipeInput(list), level);
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