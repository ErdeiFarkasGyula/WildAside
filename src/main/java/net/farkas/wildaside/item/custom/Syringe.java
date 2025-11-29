package net.farkas.wildaside.item.custom;

import net.farkas.wildaside.capability.dna.DnaCapability;
import net.farkas.wildaside.capability.dna.DnaImplementation;
import net.farkas.wildaside.dna.DnaConstants;
import net.farkas.wildaside.network.NetworkHandler;
import net.farkas.wildaside.network.packets.SyringeDataPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static net.farkas.wildaside.dna.DnaConstants.*;

public class Syringe extends Item {
    public static final int DEFAULT_BLOOD_COLOR = 0xba260f;
    public static final int DEFAULT_MAX_LOAD = 3;

    private static final float NEEDLE_DELTA = 0.1f;
    private static final int RAYCAST_RANGE = 3;

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
        initTagDefaults(tag);

        boolean inwards = tag.getBoolean(INWARDS);
        float progress = tag.getFloat(SYRINGE_PROGRESS);
        float blood = tag.getFloat(BLOOD_LEVEL);

        progress = updateProgress(tag, progress, inwards);

        if (inwards) {
            blood = handleSamplingPhase(player, tag, blood, progress);
        } else {
            blood = handleInjectionPhase(level, player, tag, blood);
        }

        blood = Mth.clamp(blood, 0, DEFAULT_MAX_LOAD);
        tag.putFloat(BLOOD_LEVEL, blood);

        NetworkHandler.sendSyringeDataClientSyncPacket(
                player,
                player.getInventory().selected,
                progress,
                blood,
                true,
                tag.getBoolean(INWARDS)
        );

        if (progress <= 0f || progress >= DEFAULT_MAX_LOAD) {
            player.stopUsingItem();
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (!(entity instanceof ServerPlayer player)) return;
        CompoundTag tag = stack.getOrCreateTag();

        NetworkHandler.sendSyringeDataClientSyncPacket(
                player,
                player.getInventory().selected,
                tag.getFloat(SYRINGE_PROGRESS),
                tag.getFloat(BLOOD_LEVEL),
                false,
                tag.getBoolean(INWARDS)
        );
    }

    public static void handleSyringeProgress(SyringeDataPacket packet) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        Player player = mc.level.getPlayerByUUID(packet.getPlayerId());
        if (player == null) return;

        int slot = packet.getSlot();
        if (slot < 0 || slot >= player.getInventory().items.size()) return;

        ItemStack stack = player.getInventory().getItem(slot);
        if (!(stack.getItem() instanceof Syringe)) return;

        CompoundTag tag = stack.getOrCreateTag();
        tag.putFloat(SYRINGE_PROGRESS, packet.getProgress());
        tag.putBoolean(INWARDS, packet.isInwards());
        tag.putFloat(BLOOD_LEVEL, packet.getBlood());
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }


    private void initTagDefaults(CompoundTag tag) {
        tag.putBoolean(INWARDS, tag.getBoolean(INWARDS));
        tag.putFloat(BLOOD_LEVEL, tag.getFloat(BLOOD_LEVEL));
    }

    private float updateProgress(CompoundTag tag, float progress, boolean inwards) {
        progress += inwards ? NEEDLE_DELTA : -NEEDLE_DELTA;
        progress = Mth.clamp(progress, 0f, DEFAULT_MAX_LOAD);
        tag.putFloat(SYRINGE_PROGRESS, progress);

        if (progress >= DEFAULT_MAX_LOAD) tag.putBoolean(INWARDS, false);
        if (progress <= 0f) tag.putBoolean(INWARDS, true);

        return progress;
    }

    private float handleSamplingPhase(ServerPlayer player, CompoundTag syringeTag, float blood, float progress) {
        blood -= NEEDLE_DELTA;

        ItemStack offHand = player.getItemInHand(InteractionHand.OFF_HAND);
        if (!(offHand.getItem() instanceof DnaHolder dnaHolder)) return blood;

        if (progress == DEFAULT_MAX_LOAD) {
            DnaImplementation dna = new DnaImplementation();
            dna.deserializeNBT(syringeTag.getCompound(DNA_DATA));

            if (dna.getSource() == null) return blood;

            CompoundTag holderTag = offHand.getOrCreateTag();
            holderTag.put(DNA_DATA, dna.serializeNBT());

            syringeTag.remove(DNA_DATA);

            int newProgress = Mth.clamp(holderTag.getInt(SAMPLE_PROGRESS) + 1, 0, DnaHolder.DEFAULT_MAX_SAMPLES);

            holderTag.putInt(SAMPLE_PROGRESS, newProgress);
            offHand.setTag(holderTag);
        }

        return blood;
    }

    private float handleInjectionPhase(Level level, ServerPlayer player, CompoundTag tag, float blood) {
        LivingEntity target = raytraceLiving(level, player, RAYCAST_RANGE);
        if (target == null) return blood;

        UUID previous = tag.contains(PREVIOUS_TARGET) ? tag.getUUID(PREVIOUS_TARGET) : null;
        if (previous != null && !target.getUUID().equals(previous)) {
            tag.putBoolean(UNUSABLE, true);
        }

        if (blood > 2.5f) {
            target.getCapability(DnaCapability.INSTANCE).ifPresent(dna ->
                    tag.put(DNA_DATA, dna.serializeNBT())
            );
        }

        tag.putUUID(PREVIOUS_TARGET, target.getUUID());
        return blood + NEEDLE_DELTA;
    }

    private LivingEntity raytraceLiving(Level level, Player player, double range) {
        Vec3 start = player.getEyePosition();
        Vec3 end = start.add(player.getLookAngle().scale(range));

        AABB box = player.getBoundingBox().expandTowards(end.subtract(start)).inflate(1.0);
        List<Entity> entities = level.getEntities(player, box, e -> e instanceof LivingEntity && e.isPickable());

        double closest = Double.MAX_VALUE;
        LivingEntity hitResult = null;

        for (Entity e : entities) {
            AABB bb = e.getBoundingBox().inflate(0.3);
            Optional<Vec3> hit = bb.clip(start, end);

            if (hit.isPresent()) {
                double dist = hit.get().distanceTo(start);
                if (dist < closest) {
                    closest = dist;
                    hitResult = (LivingEntity) e;
                }
            }
        }

        return hitResult;
    }

    private int raytraceForWater(Level level, Player player, double range) {
        ClipContext ctx = new ClipContext(
                player.getEyePosition(),
                player.getEyePosition().add(player.getViewVector(1f).scale(range)),
                ClipContext.Block.OUTLINE,
                ClipContext.Fluid.WATER,
                player
        );

        BlockPos pos = level.clip(ctx).getBlockPos();
        return level.getFluidState(pos).is(FluidTags.WATER) || level.getBlockState(pos).is(Blocks.WATER)
                ? level.getBiome(pos).get().getWaterColor()
                : 0;
    }
}
