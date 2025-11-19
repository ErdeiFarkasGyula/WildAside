package net.farkas.wildaside.item.custom.syringe;

import net.farkas.wildaside.capability.syringe.SyringeAnimCapability;
import net.farkas.wildaside.item.custom.DnaHolder;
import net.farkas.wildaside.network.NetworkHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

public class SyringeItem extends Item {
    public static final int MAX_PROGRESS = 3;
    public static final int MAX_CONTAM = 3;
    public static final int CLOT_TICKS = 2400;

    public static final String NEEDLE_PROGRESS = "needle_progress";
    public static final String TAG_CONTAM = "contamination";
    public static final String TAG_SOURCE = "blood_source";
    public static final String TAG_BLOOD_AGE = "blood_age";
    public static final String TAG_WATER = "water";
    public static final String TAG_DNA = "dna_data";

    public SyringeItem(Properties props) { super(props); }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) return InteractionResultHolder.pass(player.getItemInHand(hand));

        ItemStack stack = player.getItemInHand(hand);

        HitResult hit = player.pick(5.0D, 0.0F, false);
        boolean hitWater = false;
        if (hit.getType() == HitResult.Type.BLOCK) {
            BlockPos pos = BlockPos.containing(hit.getLocation());
            var fluidState = level.getFluidState(pos);
            hitWater = !fluidState.isEmpty() && fluidState.isSource();
        }

        boolean shouldPull = SyringeLogic.shouldPull(stack);

        player.getCapability(SyringeAnimCapability.INSTANCE).ifPresent(anim -> {
            System.out.println("USING");
            anim.start(shouldPull, player.getInventory().selected);
            NetworkHandler.sendSyringeAnimPacket(shouldPull, player.getInventory().selected);
        });

        if (shouldPull) {
            if (hitWater) {
                SyringeActions.queue(player, SyringeAction.pullWater(hit));
            } else {
                SyringeActions.queue(player, SyringeAction.pullAir());
            }
        } else {
            ItemStack off = player.getItemInHand(InteractionHand.OFF_HAND);
            boolean hasDnaHolder = off.getItem() instanceof DnaHolder;
            if (hasDnaHolder) {
                SyringeActions.queue(player, SyringeAction.transferToHolder());
            } else {
                SyringeActions.queue(player, SyringeAction.push());
            }
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (hand == InteractionHand.OFF_HAND || player.level().isClientSide()) return InteractionResult.PASS;

        player.getCapability(SyringeAnimCapability.INSTANCE).ifPresent(anim -> {
            anim.start(true, player.getInventory().selected);
            NetworkHandler.sendSyringeAnimPacket(true, player.getInventory().selected);
        });

        SyringeActions.queue(player, SyringeAction.pullEntity(target.getId()));
        return InteractionResult.sidedSuccess(player.level().isClientSide());
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (level.isClientSide()) return;
        SyringeLogic.tickClotting(stack);
    }
}