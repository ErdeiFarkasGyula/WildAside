package net.farkas.wildaside.item.custom;

import net.farkas.wildaside.capability.syringe.SyringeDataCapability;
import net.farkas.wildaside.capability.syringe.SyringeDataProvider;
import net.farkas.wildaside.item.ModItems;
import net.farkas.wildaside.network.NetworkHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.Nullable;

public class Syringe extends Item {
    public static final int DEFAULT_MAX_LOAD = 3;
    private static final int SYNC_INTERVAL = 4;
    public static final int MAX_BLOOD_LEVEL = 3;
    public static final int BLOOD_DECAY_INTERVAL = 20 * 2;

    public static final String SYRINGE_PROGRESS = "syringe_progress";
    public static final String BLOOD_LEVEL = "blood_level";
    public static final String BLOOD_DECAY_TICKS = "blood_decay_ticks";

    public Syringe(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if (pLevel.isClientSide() || pUsedHand != InteractionHand.MAIN_HAND) return InteractionResultHolder.pass(pPlayer.getItemInHand(pUsedHand));
        startSyringeAnimation((ServerPlayer) pPlayer, pUsedHand, pPlayer.getMainHandItem());
        return super.use(pLevel, pPlayer, pUsedHand);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, level, entity, slot, selected);

        if (!(entity instanceof ServerPlayer serverPlayer)) {
            return;
        }

        stack.getCapability(SyringeDataCapability.INSTANCE).ifPresent(cap -> {
            if (!cap.isAnimating()) return;

            if (cap.getSlot() != serverPlayer.getInventory().selected) {
                cap.setAnimating(false);
                NetworkHandler.sendSyringeDataClientSyncPacket(serverPlayer, cap.getSlot(), cap.getProgress(), false, cap.isInwards());
                return;
            }

            float delta = 0.1f;
            float old = cap.getProgress();
            float next = old + (cap.isInwards() ? delta : -delta);
            cap.setProgress(next);

            boolean finished = next >= DEFAULT_MAX_LOAD || next <= 0f;
            if (finished) {
                cap.setAnimating(false);
                cap.setProgress(Mth.clamp(next, 0f, DEFAULT_MAX_LOAD));
            }

            if (Math.abs(next - old) > 1e-4f || serverPlayer.tickCount % SYNC_INTERVAL == 0 || finished) {
                NetworkHandler.sendSyringeDataClientSyncPacket(serverPlayer, cap.getSlot(), cap.getProgress(), cap.isAnimating(), cap.isInwards());
            }

            if (finished) {

            }
        });
    }

    public static boolean startSyringeAnimation(ServerPlayer player, InteractionHand hand, ItemStack stack) {
        if (player == null) return false;
        stack.getCapability(SyringeDataCapability.INSTANCE).ifPresent(cap -> {
            if (cap.isAnimating()) return;
            cap.setAnimating(true);
            cap.setSlot(player.getInventory().selected);
            cap.setInwards(!cap.isInwards());
            cap.setProgress(cap.getProgress());

            NetworkHandler.sendSyringeDataClientSyncPacket(player, cap.getSlot(), cap.getProgress(), cap.isAnimating(), cap.isInwards());
        });
        return true;
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new SyringeDataProvider();
    }
}
