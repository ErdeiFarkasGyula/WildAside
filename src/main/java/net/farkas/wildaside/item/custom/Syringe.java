package net.farkas.wildaside.item.custom;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class Syringe extends Item {
    public static int DEFAULT_MAX_LOAD = 4;

    public Syringe(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack pStack, Player pPlayer, LivingEntity pInteractionTarget, InteractionHand pUsedHand) {
        if (pUsedHand == InteractionHand.OFF_HAND || pPlayer.level().isClientSide()) return InteractionResult.PASS;

        CompoundTag tag = pStack.getOrCreateTag();
        int maxProgress = DEFAULT_MAX_LOAD;
        int progress = tag.getInt("unload_progress");

        progress = Math.min(progress + 1, maxProgress);
        tag.putInt("unload_progress", progress);

        pStack.setTag(tag);
        pPlayer.setItemInHand(pUsedHand, pStack);

        return InteractionResult.sidedSuccess(pPlayer.level().isClientSide());
    }
}
