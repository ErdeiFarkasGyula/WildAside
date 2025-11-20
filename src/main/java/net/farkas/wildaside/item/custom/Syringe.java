package net.farkas.wildaside.item.custom;

import net.farkas.wildaside.network.NetworkHandler;
import net.farkas.wildaside.network.packets.SyringeDataPacket;
import net.minecraft.client.Minecraft;
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
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.Nullable;

public class Syringe extends Item {
    public static final int DEFAULT_MAX_LOAD = 3;

    public static final String SYRINGE_PROGRESS = "syringe_progress";
    public static final String ANIMATING = "animating";
    public static final String INWARDS = "inwards";

    public Syringe(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide() && hand == InteractionHand.MAIN_HAND) {
            player.startUsingItem(hand);
        }
        return InteractionResultHolder.success(stack);
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int useRemaining) {
        if (!(entity instanceof ServerPlayer player)) return;

        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains(INWARDS)) {
            tag.putBoolean(INWARDS, true);
        }

        boolean inwards = tag.getBoolean(INWARDS);
        float delta = 0.1f;
        float progress = tag.getFloat(SYRINGE_PROGRESS);

        progress += inwards ? delta : -delta;
        progress = Mth.clamp(progress, 0f, DEFAULT_MAX_LOAD);
        tag.putFloat(SYRINGE_PROGRESS, progress);

        if (progress >= DEFAULT_MAX_LOAD) {
            tag.putBoolean(INWARDS, false);
        } else if (progress <= 0f) {
            tag.putBoolean(INWARDS, true);
        }

        NetworkHandler.sendSyringeDataClientSyncPacket(player, player.getInventory().selected, progress, true, inwards);

        if (progress >= DEFAULT_MAX_LOAD || progress <= 0f) {
            player.stopUsingItem();
            tag.putBoolean(ANIMATING, false);
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (!(entity instanceof ServerPlayer player)) return;
        CompoundTag tag = stack.getOrCreateTag();
        tag.putBoolean(ANIMATING, false);
        NetworkHandler.sendSyringeDataClientSyncPacket(player, player.getInventory().selected, tag.getFloat(SYRINGE_PROGRESS), false, tag.getBoolean(INWARDS));
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack pStack, Player pPlayer, LivingEntity pInteractionTarget, InteractionHand pUsedHand) {
        if (!pPlayer.level().isClientSide() && pUsedHand == InteractionHand.MAIN_HAND) {
            System.out.println("MOB");
        }
        return super.interactLivingEntity(pStack, pPlayer, pInteractionTarget, pUsedHand);
    }

    public static void handleSyringeProgress(SyringeDataPacket pkt) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        Player target = mc.level.getPlayerByUUID(pkt.getPlayerId());
        if (target == null) return;

        int slot = pkt.getSlot();
        if (slot < 0 || slot >= target.getInventory().items.size()) return;

        ItemStack stack = target.getInventory().getItem(slot);
        if (!(stack.getItem() instanceof Syringe)) return;

        CompoundTag tag = stack.getOrCreateTag();
        tag.putFloat(SYRINGE_PROGRESS, pkt.getProgress());
        tag.putBoolean(ANIMATING, pkt.isAnimating());
        tag.putBoolean(INWARDS, pkt.isInwards());
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.NONE;
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        return 72000;
    }
}

