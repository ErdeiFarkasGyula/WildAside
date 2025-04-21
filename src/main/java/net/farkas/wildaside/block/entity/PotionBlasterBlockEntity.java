package net.farkas.wildaside.block.entity;

import net.farkas.wildaside.block.custom.vibrion.PotionBlaster;
import net.farkas.wildaside.screen.potion_blaster.PotionBlasterMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.*;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
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

public class PotionBlasterBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemHandler = new ItemStackHandler(10);
    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    private static final int INPUT_1 = 0;
    private static final int INPUT_2 = 1;
    private static final int INPUT_3 = 2;
    private static final int INPUT_4 = 3;
    private static final int INPUT_5 = 4;
    private static final int INPUT_6 = 5;
    private static final int INPUT_7 = 6;
    private static final int INPUT_8 = 7;
    private static final int INPUT_9 = 8;
    private static final int OUTPUT_1 = 9;

    protected final ContainerData data;

    public int potionTicksLeft = 0;
    public int maxPotionTicks = 1200;
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
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> PotionBlasterBlockEntity.this.potionTicksLeft = pValue;
                    case 1 -> PotionBlasterBlockEntity.this.maxPotionTicks = pValue;
                }
            }

            @Override
            public int getCount() {
                return 2;
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

        for (int i = 1; i <= power; i++) {
            BlockPos target = pos.relative(direction, i);
            if (level.getBlockState(target).isCollisionShapeFullBlock(level, target)) break;

            int colour = PotionUtils.getColor(activePotion);
            float r = ((colour >> 16) & 0xFF) / 255.0f;
            float g = ((colour >> 8) & 0xFF) / 255.0f;
            float b = (colour & 0xFF) / 255.0f;

            DustParticleOptions particle = new DustParticleOptions(new Vector3f(r, g, b), 1f);
            RandomSource random = level.random;
            Vec3 base = Vec3.atCenterOf(worldPosition);

            for (int k = 0; k < 3; k++) {
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
                }
            }
        }
    }

    public void consumePotionBottle() {
        if (lastUsedSlot >= 0 && lastUsedSlot < 9) {
            ItemStack stack = itemHandler.getStackInSlot(lastUsedSlot);
            if (!stack.isEmpty()) {
                itemHandler.setStackInSlot(lastUsedSlot, stack);
                itemHandler.insertItem(OUTPUT_1, new ItemStack(Items.GLASS_BOTTLE), false);
            }
        }

        activePotion = ItemStack.EMPTY;
        potionTicksLeft = 0;

        lastUsedSlot = -1;
    }

    public void selectNewPotion() {
        if (!activePotion.isEmpty() || potionTicksLeft > 0) return;

        List<Integer> validSlots = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (i != lastUsedSlot && stack.getItem() instanceof PotionItem && !stack.isEmpty()) {
                validSlots.add(i);
            }
        }

        if (validSlots.isEmpty()) {
            activePotion = ItemStack.EMPTY;
            return;
        }

        int slot = validSlots.get(level.random.nextInt(validSlots.size()));
        ItemStack potionStack = itemHandler.getStackInSlot(slot);

        activePotion = potionStack.copy();
        potionTicksLeft = maxPotionTicks;

        potionStack.shrink(1);
        itemHandler.setStackInSlot(slot, potionStack);
        itemHandler.insertItem(OUTPUT_1, new ItemStack(Items.GLASS_BOTTLE), false);

        lastUsedSlot = slot;
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
        pTag.put("inventory", itemHandler.serializeNBT());
        pTag.putInt("spore_blaster.progress", potionTicksLeft);

        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        itemHandler.deserializeNBT(pTag.getCompound("inventory"));
        potionTicksLeft = pTag.getInt("spore_blaster.progress");
    }
}
