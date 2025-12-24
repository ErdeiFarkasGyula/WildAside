package net.farkas.wildaside.block.entity.custom;

import net.farkas.wildaside.block.custom.vibrion.PotionBlasterBlock;
import net.farkas.wildaside.screen.potion_blaster.PotionBlasterMenu;
import net.farkas.wildaside.util.AdvancementHandler;
import net.farkas.wildaside.util.BlasterUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.*;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

public class PotionBlasterBlockEntity extends BlasterBlockEntity implements MenuProvider {
    private final ItemStackHandler itemHandler = new ItemStackHandler(10) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    public static final int OUTPUT_1 = 9;

    public final ContainerData data;

    public int potionColour = 0;
    public int potionTicksLeft = 0;
    public int maxPotionTicks = 200;
    public int lastUsedSlot = -1;
    public ItemStack activePotion = ItemStack.EMPTY;
    public boolean shouldSelectNewPotion = true;

    public PotionBlasterBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.POTION_BLASTER.get(), pPos, pBlockState);

        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> PotionBlasterBlockEntity.this.potionTicksLeft;
                    case 1 -> PotionBlasterBlockEntity.this.maxPotionTicks;
                    case 2 -> PotionBlasterBlockEntity.this.potionColour;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> PotionBlasterBlockEntity.this.potionTicksLeft = pValue;
                    case 1 -> PotionBlasterBlockEntity.this.maxPotionTicks = pValue;
                    case 2 -> PotionBlasterBlockEntity.this.potionColour = pValue;
                }
            }

            @Override
            public int getCount() {
                return 3;
            }
        };
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.wildaside.potion_blaster");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new PotionBlasterMenu(pContainerId, pPlayerInventory, this, this.data);
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    public void shootPotionBeam(Direction direction, ServerLevel level, BlockPos pos) {
        if (activePotion.isEmpty()) return;

        List<MobEffectInstance> effects = PotionUtils.getMobEffects(activePotion);
        if (effects.isEmpty()) return;

        int power = level.getBestNeighborSignal(pos);

        this.potionColour = PotionUtils.getColor(activePotion);
        setChanged();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        float r = ((potionColour >> 16) & 0xFF) / 255.0f;
        float g = ((potionColour >> 8) & 0xFF) / 255.0f;
        float b = (potionColour & 0xFF) / 255.0f;

        DustParticleOptions particle = new DustParticleOptions(new Vector3f(r, g, b), 1f);
        RandomSource random = level.random;

        for (int i = 1; i <= power; i++) {
            if (shouldBreakNext) {
                shouldBreakNext = false;
                break;
            }

            BlockPos target = pos.relative(direction, i);
            if (level.getBlockState(target).isCollisionShapeFullBlock(level, target)) break;

            var nextBlock = level.getBlockState(target);
            var originBlock = level.getBlockState(this.getBlockPos());

            if (!BlasterUtils.canTraverse(direction, nextBlock, originBlock, this)) return;

            for (int k = 0; k < 2; k++) {
                double x = target.getX() + random.nextDouble();
                double y = target.getY() + random.nextDouble();
                double z = target.getZ() + random.nextDouble();

                level.sendParticles(particle,
                        x, y, z, 1, direction.getStepX(), direction.getStepY(), direction.getStepZ(), 0.1
                );
            }

            List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, new AABB(target));
            for (LivingEntity entity : targets) {
                for (MobEffectInstance effect : effects) {
                    entity.addEffect(new MobEffectInstance(
                            effect.getEffect(),
                            effect.getDuration() - (maxPotionTicks - potionTicksLeft),
                            effect.getAmplifier()
                    ));
                    level.sendParticles(particle,
                            entity.getX(), entity.getY(), entity.getZ(), 1, direction.getStepX(), direction.getStepY(), direction.getStepZ(), 0.1
                    );
                    if (entity instanceof ServerPlayer player) {
                        AdvancementHandler.givePlayerAdvancement(player, "brew_barrage");
                    }
                }
            }
        }
    }

    public void consumePotionBottle() {
        activePotion = ItemStack.EMPTY;
        potionTicksLeft = 0;
        maxPotionTicks = 200;
        lastUsedSlot = -1;
        shouldSelectNewPotion = true;

        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public void selectNewPotion() {
        if (!activePotion.isEmpty() || potionTicksLeft > 0) return;

        List<Integer> validSlots = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() instanceof PotionItem) {
                validSlots.add(i);
            }
        }

        if (validSlots.isEmpty()) {
            activePotion = ItemStack.EMPTY;
            lastUsedSlot = -1;
            shouldSelectNewPotion = true;
            return;
        }

        int slot = validSlots.get(level.random.nextInt(validSlots.size()));

        ItemStack extracted = itemHandler.extractItem(slot, 1, false);
        if (extracted.isEmpty()) {
            shouldSelectNewPotion = true;
            lastUsedSlot = -1;
            activePotion = ItemStack.EMPTY;
            return;
        }

        ItemStack remainder = itemHandler.insertItem(OUTPUT_1, new ItemStack(Items.GLASS_BOTTLE), false);
        if (!remainder.isEmpty() && level instanceof ServerLevel serverLevel) {
            serverLevel.addFreshEntity(new ItemEntity(serverLevel,
                    worldPosition.getX() + 0.5,
                    worldPosition.getY() + 1.0,
                    worldPosition.getZ() + 0.5,
                    remainder));
        }

        activePotion = extracted.copy();
        lastUsedSlot = slot;
        shouldSelectNewPotion = false;

        Iterable<MobEffectInstance> effectInstances = PotionUtils.getMobEffects(activePotion);
        List<MobEffectInstance> effects = StreamSupport.stream(effectInstances.spliterator(), false).toList();
        maxPotionTicks = effects.isEmpty() ? 200 : effects.stream().mapToInt(MobEffectInstance::getDuration).max().orElse(200);

        potionTicksLeft = maxPotionTicks;

        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
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

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);

        if (pTag.contains("inventory")) {
            pTag.put("inventory", itemHandler.serializeNBT());
        }
        pTag.putInt("ticks_left", potionTicksLeft);
        pTag.putInt("max_ticks", maxPotionTicks);
        pTag.putInt("colour", potionColour);
        pTag.put("potion", activePotion.save(new CompoundTag()));

    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        itemHandler.deserializeNBT(pTag.getCompound("inventory"));
        potionTicksLeft = pTag.getInt("ticks_left");
        maxPotionTicks = pTag.getInt("max_ticks");
        potionColour = pTag.getInt("colour");
        if (pTag.contains("potion")) {
            activePotion = ItemStack.of(pTag.getCompound("potion"));
        } else {
            activePotion = ItemStack.EMPTY;
        }
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide()) return;

        if (level.getBlockEntity(pos) instanceof PotionBlasterBlockEntity be) {
            if (level.getBestNeighborSignal(pos) > 0) {
                int poweredSides = 0;
                for (Direction dir : Direction.values()) {
                    if (level.getSignal(pos.relative(dir), dir) > 0) {
                        poweredSides++;
                    }
                }

                if (poweredSides >= 2) {
                    clearActivePotion();
                    return;
                }

                if (potionTicksLeft <= 0 || activePotion.isEmpty()) {
                    if (shouldSelectNewPotion) {
                        selectNewPotion();
                    }
                }

                if (!activePotion.isEmpty()) {
                    shootPotionBeam(state.getValue(PotionBlasterBlock.FACING), (ServerLevel)level, pos);
                    potionTicksLeft--;

                    if (potionTicksLeft <= 0) {
                        consumePotionBottle();
                    }
                }
            }
        }
    }

    public void clearActivePotion() {
        if (!activePotion.isEmpty()) {
            activePotion = ItemStack.EMPTY;
            potionTicksLeft = 0;
            lastUsedSlot = -1;
            shouldSelectNewPotion = true;
            setChanged();
            if (level != null) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }
    }
}
