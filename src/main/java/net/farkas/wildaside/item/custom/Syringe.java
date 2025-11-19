package net.farkas.wildaside.item.custom;

import net.farkas.wildaside.item.ModItems;
import net.minecraft.nbt.CompoundTag;
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

public class Syringe extends Item {
    public static int DEFAULT_MAX_LOAD = 3;

    public static final String SYRINGE_PROGRESS = "syringe_progress";

    public Syringe(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack pStack, Player pPlayer, LivingEntity pInteractionTarget, InteractionHand pUsedHand) {
        if (pUsedHand == InteractionHand.OFF_HAND || pPlayer.level().isClientSide()) return InteractionResult.PASS;
        
        return InteractionResult.sidedSuccess(pPlayer.level().isClientSide());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if (pPlayer.level().isClientSide()) return InteractionResultHolder.pass(pPlayer.getItemInHand(pUsedHand));

        if (pUsedHand == InteractionHand.MAIN_HAND) {
            ItemStack stack = pPlayer.getItemInHand(pUsedHand);
            if (startSyringeAnimation(pPlayer, pUsedHand, stack)) return InteractionResultHolder.pass(stack);

            if (pPlayer.getItemInHand(InteractionHand.OFF_HAND).is(ModItems.DNA_HOLDER.get())) {
                System.out.println("MEOW");

            }
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }

    private boolean startSyringeAnimation(Player pPlayer, InteractionHand pUsedHand, ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        boolean inProgress = tag.getBoolean("in_progress");
        if (inProgress) return true;

        tag.putBoolean("in_progress", true);
        tag.putInt("slot", pPlayer.getInventory().selected);

        boolean inwards = tag.getBoolean("inwards");
        tag.putBoolean("inwards", !inwards);

        stack.setTag(tag);
        pPlayer.setItemInHand(pUsedHand, stack);
        return false;
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        CompoundTag tag = pStack.getOrCreateTag();
        boolean inProgress = tag.getBoolean("in_progress");
        if (!inProgress) return;

        if (pEntity instanceof Player player) {
            int slot = tag.getInt("slot");
            if (slot != player.getInventory().selected) {
                tag.putBoolean("in_progress", false);
                return;
            }

            boolean inwards = tag.getBoolean("inwards");

            float prog = tag.getFloat("syringe_progress");

            if (inwards) {
                prog += 0.1f;
            } else {
                prog -= 0.1f;
            }

            System.out.println(" prog: " + prog);

            if (prog >= Syringe.DEFAULT_MAX_LOAD || prog < 0.0f) {
                tag.putBoolean("in_progress", false);
            }

            tag.putFloat("syringe_progress", prog);
            pStack.setTag(tag);
        }
    }
}
