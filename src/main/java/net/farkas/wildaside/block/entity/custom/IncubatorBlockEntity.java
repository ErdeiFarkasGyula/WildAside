package net.farkas.wildaside.block.entity.custom;

import net.farkas.wildaside.block.entity.ModBlockEntities;
import net.farkas.wildaside.block.entity.SidedItemHandler;
import net.farkas.wildaside.dna.BacillusBlobPayload;
import net.farkas.wildaside.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

public class IncubatorBlockEntity extends BlockEntity implements MenuProvider {
    private static final int SLOT_FUEL = 0;

    private final ItemStackHandler items = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private LazyOptional<IItemHandler> sided = LazyOptional.empty();

    private boolean hasBlob = false;
    private CompoundTag dnaPayload = new CompoundTag();
    private float maturity = 0f;
    private float maturityRequired = 1f;
    private boolean glassBroken = false;

    private int burnTime = 0;
    private int burnTimeTotal = 0;

    public IncubatorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.INCUBATOR.get(), pos, state);
    }

    public net.minecraft.world.InteractionResult onUse(Player player, net.minecraft.world.InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);

        if (!hasBlob && held.is(ModItems.BACILLUS_BLOB.get())) {
            hasBlob = true;
            dnaPayload = held.hasTag() ? held.getTag().copy() : new CompoundTag();
            held.shrink(1);
            setChanged();
            return net.minecraft.world.InteractionResult.SUCCESS;
        }

        if (hasBlob && dnaPayload.isEmpty() && held.is(ModItems.DNA_HOLDER.get())) {
            if (BacillusBlobPayload.copyDnaFromHolderInternal(dnaPayload, held)) {
                setChanged();
                return net.minecraft.world.InteractionResult.SUCCESS;
            }
        }

        int burn = ForgeHooks.getBurnTime(held, null);
        if (burn > 0 && items.getStackInSlot(SLOT_FUEL).isEmpty()) {
            ItemStack insert = held.split(1);
            items.setStackInSlot(SLOT_FUEL, insert);
            setChanged();
            return net.minecraft.world.InteractionResult.SUCCESS;
        }

        if (hasBlob && player.isShiftKeyDown() && held.isEmpty()) {
            glassBroken = !glassBroken;
            setChanged();
            return net.minecraft.world.InteractionResult.SUCCESS;
        }

        if (hasBlob && glassBroken && held.isEmpty()) {
            applyBlobToPlayer(player);
            clearBlob();
            return net.minecraft.world.InteractionResult.SUCCESS;
        }

        return net.minecraft.world.InteractionResult.PASS;
    }

    private void applyBlobToPlayer(Player player) {

    }

    private void clearBlob() {
        hasBlob = false;
        dnaPayload = new CompoundTag();
        maturity = 0f;
        glassBroken = false;
        setChanged();
    }

    public void tickServer() {
        if (level == null || level.isClientSide) return;
        if (getBlockState().getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) != DoubleBlockHalf.LOWER) return;

        boolean dirty = false;

        if (burnTime > 0) burnTime--;

        if (burnTime == 0 && hasBlob && !items.getStackInSlot(SLOT_FUEL).isEmpty()) {
            ItemStack fuel = items.extractItem(SLOT_FUEL, 1, false);
            burnTimeTotal = burnTime = ForgeHooks.getBurnTime(fuel, null);
            dirty = true;
        }

        if (hasBlob && burnTime > 0 && maturity < maturityRequired) {
            float increment = 1f / 200f;
            maturity = Math.min(maturityRequired, maturity + increment);
            dirty = true;
        }

        if (dirty) setChanged();
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            if (!sided.isPresent()) {
                Map<Direction, Set<Integer>> insertBySide = Map.of(
                        Direction.UP, Set.of(SLOT_FUEL),
                        Direction.DOWN, Set.of(),
                        Direction.NORTH, Set.of(SLOT_FUEL),
                        Direction.SOUTH, Set.of(SLOT_FUEL),
                        Direction.EAST, Set.of(SLOT_FUEL),
                        Direction.WEST, Set.of(SLOT_FUEL)
                );

                Set<Integer> outputs = Set.of();
                sided = LazyOptional.of(() -> new SidedItemHandler(items, side, insertBySide, outputs));
            }
            return sided.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        sided.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("items", items.serializeNBT());
        tag.putBoolean("hasBlob", hasBlob);
        tag.put("dnaPayload", dnaPayload);
        tag.putFloat("maturity", maturity);
        tag.putFloat("maturityRequired", maturityRequired);
        tag.putBoolean("glassBroken", glassBroken);
        tag.putInt("burnTime", burnTime);
        tag.putInt("burnTimeTotal", burnTimeTotal);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        items.deserializeNBT(tag.getCompound("items"));
        hasBlob = tag.getBoolean("hasBlob");
        dnaPayload = tag.getCompound("dnaPayload");
        maturity = tag.getFloat("maturity");
        maturityRequired = tag.getFloat("maturityRequired");
        glassBroken = tag.getBoolean("glassBroken");
        burnTime = tag.getInt("burnTime");
        burnTimeTotal = tag.getInt("burnTimeTotal");
    }

    public void dropContents() {
        SimpleContainer inv = new SimpleContainer(items.getSlots());
        for (int i = 0; i < items.getSlots(); i++) {
            inv.setItem(i, items.getStackInSlot(i));
        }
        Containers.dropContents(level, worldPosition, inv);
    }

    @Override
    public net.minecraft.network.chat.Component getDisplayName() {
        return net.minecraft.network.chat.Component.translatable("block.wildaside.incubator");
    }

    @Nullable
    @Override
    public net.minecraft.world.inventory.AbstractContainerMenu createMenu(int id, net.minecraft.world.entity.player.Inventory inv, Player player) {
        return null;
    }
}