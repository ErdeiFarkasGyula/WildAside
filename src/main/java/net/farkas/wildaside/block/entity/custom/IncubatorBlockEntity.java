package net.farkas.wildaside.block.entity.custom;

import net.farkas.wildaside.block.custom.IncubatorBlock;
import net.farkas.wildaside.block.entity.ModBlockEntities;
import net.farkas.wildaside.block.entity.SidedItemHandler;
import net.farkas.wildaside.dna.BacillusBlobPayload;
import net.farkas.wildaside.item.ModItems;
import net.farkas.wildaside.screen.incubator.IncubatorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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

import static net.farkas.wildaside.dna.DnaConstants.*;

public class IncubatorBlockEntity extends BlockEntity implements MenuProvider {
    private static final int SLOT_FUEL = 0;

    private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    public final ContainerData data;

    private boolean hasBlob = false;
    private CompoundTag dnaPayload = new CompoundTag();

    private float maturity = 0f;
    private float maturityRequired = 1f;
    private boolean glassOpen = false;

    private int coldTicks = 0;
    private final int coldTicksThreshold = 20 * 60;

    private int burnTime = 0;
    private int burnTimeTotal = 0;

    private int heatLevel = 2;
    private int mutationRisk = 0;

    public IncubatorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.INCUBATOR.get(), pos, state);

        this.data = new ContainerData() {
            @Override
            public int get(int i) {
                return switch (i) {
                    case 0 -> burnTime;
                    case 1 -> burnTimeTotal;
                    case 2 -> (int) (maturity * 1000f);
                    case 3 -> (int) (maturityRequired * 1000f);
                    case 4 -> heatLevel;
                    case 5 -> mutationRisk;
                    case 6 -> coldTicks;
                    case 7 -> coldTicksThreshold;
                    case 8 -> glassOpen ? 1 : 0;
                    default -> 0;
                };
            }

            @Override
            public void set(int i, int v) {
                switch (i) {
                    case 0 -> burnTime = v;
                    case 1 -> burnTimeTotal = v;
                    case 2 -> maturity = v / 1000f;
                    case 3 -> maturityRequired = v / 1000f;
                    case 4 -> heatLevel = v;
                    case 5 -> mutationRisk = v;
                    case 6 -> coldTicks = v;
                    case 8 -> glassOpen = v != 0;
                }
            }

            @Override
            public int getCount() {
                return 9;
            }
        };
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            if (side == null) {
                return LazyOptional.of(() -> itemHandler).cast();
            }

            Map<Direction, Set<Integer>> insertBySide = Map.of(
                    Direction.UP, Set.of(SLOT_FUEL),
                    Direction.DOWN, Set.of(),
                    Direction.NORTH, Set.of(SLOT_FUEL),
                    Direction.SOUTH, Set.of(SLOT_FUEL),
                    Direction.EAST, Set.of(SLOT_FUEL),
                    Direction.WEST, Set.of(SLOT_FUEL)
            );

            return LazyOptional.of(() -> new SidedItemHandler(itemHandler, side, insertBySide, Set.of())).cast();
        }

        return super.getCapability(cap, side);
    }

    public InteractionResult handleUse(Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);

        if (!glassOpen) return InteractionResult.PASS;

        if (!hasBlob && held.is(ModItems.BACILLUS_BLOB.get())) {
            hasBlob = true;
            dnaPayload = held.hasTag() ? held.getTag().copy() : new CompoundTag();
            held.shrink(1);
            sync();
            return InteractionResult.SUCCESS;
        }

        if (hasBlob && dnaPayload.isEmpty() && held.is(ModItems.DNA_HOLDER.get())) {
            if (BacillusBlobPayload.copyDnaFromHolderInternal(dnaPayload, held)) {
                sync();
                return InteractionResult.SUCCESS;
            }
        }

        if (hasBlob && held.isEmpty() && player.isShiftKeyDown()) {
            applyBlobToPlayer(player);
            clearBlob();
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    private void applyBlobToPlayer(Player player) {

    }

    private void clearBlob() {
        dnaPayload = new CompoundTag();
        hasBlob = false;
        glassOpen = false;
        maturity = 0f;
        coldTicks = 0;
        sync();
    }

    public void tickServer() {
        if (level == null || level.isClientSide) return;
        if (getBlockState().getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) != DoubleBlockHalf.LOWER) return;

        boolean dirty = false;
        float factor = heatFactor();

        if (burnTime > 0) {
            burnTime -= Math.max(1, (int) Math.ceil(Math.max(1f, factor)));
            if (burnTime < 0) burnTime = 0;
            dirty = true;
        }

        if (burnTime == 0 && factor > 0f && !itemHandler.getStackInSlot(SLOT_FUEL).isEmpty()) {
            ItemStack fuel = itemHandler.getStackInSlot(SLOT_FUEL);
            int burnValue = ForgeHooks.getBurnTime(fuel, null);

            if (burnValue > 0) {
                burnTimeTotal = burnTime = burnValue;

                if (fuel.getItem() == Items.LAVA_BUCKET) {
                    itemHandler.setStackInSlot(SLOT_FUEL, new ItemStack(Items.BUCKET));
                } else {
                    itemHandler.extractItem(SLOT_FUEL, 1, false);
                }
                dirty = true;
            }
        }

        if (hasBlob) {
            if (burnTime > 0 && factor > 0f) {
                float inc = (1f / 200f) * factor;
                maturity = Math.min(maturityRequired, maturity + inc);
                coldTicks = 0;
                if (maturity > 0.7f * maturityRequired) {
                    mutationRisk = Math.min(1000, mutationRisk + (int) (2 * factor));
                }
                dirty = true;
            } else {
                coldTicks++;
                if (coldTicks >= coldTicksThreshold) {
                    clearBlob();
                }
                dirty = true;
            }
        }

        if (dirty) sync();
    }

    private void sync() {
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public void tickClient() {
        if (level == null || !level.isClientSide) return;
        if (burnTime <= 0) return;
        if (heatLevel <= 0) return;

        BlockState st = getBlockState();
        Direction facing = st.hasProperty(BlockStateProperties.HORIZONTAL_FACING)
                ? st.getValue(BlockStateProperties.HORIZONTAL_FACING)
                : Direction.NORTH;

        double cx = worldPosition.getX() + 0.5;
        double cy = worldPosition.getY() + 1.0;
        double cz = worldPosition.getZ() + 0.5;

        int count = switch (heatLevel) {
            case 1 -> 1;
            case 2 -> 2;
            case 3 -> 3;
            case 4 -> 5;
            default -> 0;
        };

        for (int i = 0; i < count; i++) {
            if (level.random.nextFloat() > 0.2f + count / 10f) continue;

            double r = 0.32 + level.random.nextDouble() * 0.12;
            double a = level.random.nextDouble() * Math.PI * 2.0;
            double ox = Math.cos(a) * r;
            double oz = Math.sin(a) * r;
            double oy = level.random.nextDouble() * 0.08;

            level.addParticle(ParticleTypes.FLAME, cx + ox, cy + oy, cz + oz, 0, 0.01, 0);
            level.addParticle(ParticleTypes.SMOKE, cx + ox, cy + oy, cz + oz, 0, 0.003, 0);
        }
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

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put(INVENTORY, itemHandler.serializeNBT());
        tag.putBoolean(HAS_BLOB, hasBlob);
        tag.put(DNA_PAYLOAD, dnaPayload);
        tag.putFloat(MATURITY, maturity);
        tag.putFloat(MATURITY, maturityRequired);
        tag.putBoolean(GLASS_OPEN, glassOpen);
        tag.putInt(BURN_TIME, burnTime);
        tag.putInt(BURN_TIME_TOTAL, burnTimeTotal);
        tag.putInt(COLD_TICKS, coldTicks);
        tag.putInt(HEAT_LEVEL, heatLevel);
        tag.putInt(MATURITY_REQUIRED, mutationRisk);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        if (tag.contains(INVENTORY)) {
            itemHandler.deserializeNBT(tag.getCompound(INVENTORY));
        }

        hasBlob = tag.getBoolean(HAS_BLOB);
        dnaPayload = tag.getCompound(DNA_PAYLOAD);
        maturity = tag.getFloat(MATURITY);
        maturityRequired = tag.getFloat(MATURITY_REQUIRED);
        glassOpen = tag.getBoolean(GLASS_OPEN);
        burnTime = tag.getInt(BURN_TIME);
        burnTimeTotal = tag.getInt(BURN_TIME_TOTAL);
        coldTicks = tag.getInt(COLD_TICKS);
        heatLevel = tag.getInt(HEAT_LEVEL);
        mutationRisk = tag.getInt(MUTATION_RISK);
    }

    public float heatFactor() {
        return switch (heatLevel) {
            case 0 -> 0f;
            case 1 -> 1.0f;
            case 2 -> 1.4f;
            case 3 -> 1.8f;
            case 4 -> 2.3f;
            default -> 1.0f;
        };
    }

    public void dropContents() {
        SimpleContainer inv = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inv.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(level, worldPosition, inv);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.wildaside.incubator");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new IncubatorMenu(id, inv, this, data);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        if (pkt.getTag() == null) return;
        this.load(pkt.getTag());
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithFullMetadata();
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        load(tag);
    }

    public void setOpenState(boolean cracked) {
        if (level == null) return;

        BlockState st = level.getBlockState(worldPosition);

        if (st.hasProperty(IncubatorBlock.OPEN)) {
            level.setBlock(worldPosition, st.setValue(IncubatorBlock.OPEN, cracked), 3);
        }

        BlockPos up = worldPosition.above();
        BlockState stUp = level.getBlockState(up);

        if (stUp.getBlock() == st.getBlock() && stUp.hasProperty(IncubatorBlock.OPEN)) {
            level.setBlock(up, stUp.setValue(IncubatorBlock.OPEN, cracked), 3);
        }
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    public boolean hasBlob() {
        return hasBlob;
    }

    public float getMaturity() {
        return maturity;
    }

    public float getMaturityRequired() {
        return maturityRequired;
    }

    public int getBurnTime() {
        return burnTime;
    }

    public int getColdTicks() {
        return coldTicks;
    }

    public int getColdTicksThreshold() {
        return coldTicksThreshold;
    }

    public void setHeatLevel(int level) {
        heatLevel = Mth.clamp(level, 0, 4);
        sync();
    }

    public boolean isOpen() {
        return glassOpen;
    }

    public void setOpen(boolean open) {
        glassOpen = open;
        setOpenState(open);
        sync();
    }

    public int getHeatLevel() {
        return heatLevel;
    }

    public int getMutationRisk() {
        return mutationRisk;
    }

    public boolean blobHasDna() {
        return hasBlob && !dnaPayload.isEmpty();
    }

    public boolean tryInjectDnaFromSyringe(CompoundTag syringeTag) {
        if (!hasBlob || !glassOpen || !dnaPayload.isEmpty()) return false;
        if (!syringeTag.contains(DNA_DATA)) return false;

        dnaPayload = syringeTag.getCompound(DNA_DATA).copy();
        sync();
        return true;
    }
}