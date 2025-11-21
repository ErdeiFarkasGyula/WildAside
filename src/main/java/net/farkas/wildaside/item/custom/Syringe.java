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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class Syringe extends Item {

    public static final int DEFAULT_MAX_LOAD = 3;

    public static final String SYRINGE_PROGRESS = "syringe_progress";
    public static final String INWARDS = "inwards";
    public static final String BLOOD_LEVEL = "blood_level";

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

        if (!tag.contains(INWARDS)) tag.putBoolean(INWARDS, true);
        if (!tag.contains(BLOOD_LEVEL)) tag.putFloat(BLOOD_LEVEL, 0);

        boolean inwards = tag.getBoolean(INWARDS);
        float progress = tag.getFloat(SYRINGE_PROGRESS);
        float blood = tag.getFloat(BLOOD_LEVEL);

        float oldProgress = progress;
        progress += inwards ? NEEDLE_DELTA : -NEEDLE_DELTA;
        progress = Mth.clamp(progress, 0f, DEFAULT_MAX_LOAD);

        tag.putFloat(SYRINGE_PROGRESS, progress);

        if (progress >= DEFAULT_MAX_LOAD) tag.putBoolean(INWARDS, false);
        if (progress <= 0f) tag.putBoolean(INWARDS, true);

        if (!inwards) {
            LivingEntity target = raytraceLiving(level, player, RAYCAST_RANGE);
            if (target != null) {
                blood += NEEDLE_DELTA;

            }
        } else {
            blood -= NEEDLE_DELTA;
        }

        blood = Mth.clamp(blood, 0, DEFAULT_MAX_LOAD);
        tag.putFloat(BLOOD_LEVEL, blood);

        NetworkHandler.sendSyringeDataClientSyncPacket(player, player.getInventory().selected, progress, blood, true, tag.getBoolean(INWARDS));

        if (progress >= DEFAULT_MAX_LOAD || progress <= 0f) {
            player.stopUsingItem();
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (!(entity instanceof ServerPlayer player)) return;

        CompoundTag tag = stack.getOrCreateTag();

        NetworkHandler.sendSyringeDataClientSyncPacket(player, player.getInventory().selected, tag.getFloat(SYRINGE_PROGRESS), tag.getFloat(BLOOD_LEVEL), false, tag.getBoolean(INWARDS));
    }

    public static void handleSyringeProgress(SyringeDataPacket packet) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        Player target = mc.level.getPlayerByUUID(packet.getPlayerId());
        if (target == null) return;

        int slot = packet.getSlot();
        if (slot < 0 || slot >= target.getInventory().items.size()) return;

        ItemStack stack = target.getInventory().getItem(slot);
        if (!(stack.getItem() instanceof Syringe)) return;

        CompoundTag tag = stack.getOrCreateTag();
        tag.putFloat(SYRINGE_PROGRESS, packet.getProgress());
        tag.putBoolean(INWARDS, packet.isInwards());
        tag.putFloat(BLOOD_LEVEL, packet.getBlood());
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.NONE;
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        return 72000;
    }

    private LivingEntity raytraceLiving(Level level, Player player, double range) {
        Vec3 start = player.getEyePosition();
        Vec3 look = player.getLookAngle().scale(range);
        Vec3 end = start.add(look);

        AABB box = player.getBoundingBox().expandTowards(look).inflate(1.0);
        List<Entity> list = level.getEntities(player, box, e -> e instanceof LivingEntity && e.isPickable());

        double closest = Double.MAX_VALUE;
        LivingEntity found = null;

        for (Entity e : list) {
            AABB bb = e.getBoundingBox().inflate(0.3);
            Optional<Vec3> hit = bb.clip(start, end);

            if (hit.isPresent()) {
                double dist = hit.get().distanceTo(start);
                if (dist < closest) {
                    closest = dist;
                    found = (LivingEntity) e;
                }
            }
        }

        return found;
    }
}
