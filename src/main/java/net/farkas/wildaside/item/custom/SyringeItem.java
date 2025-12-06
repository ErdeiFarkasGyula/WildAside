package net.farkas.wildaside.item.custom;

import net.farkas.wildaside.capability.dna.DnaCapability;
import net.farkas.wildaside.capability.dna.DnaImplementation;
import net.farkas.wildaside.dna.DnaUtils;
import net.farkas.wildaside.network.NetworkHandler;
import net.farkas.wildaside.network.packets.SyringeDataPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static net.farkas.wildaside.dna.DnaConstants.*;

public class SyringeItem extends Item {
    public static final int DEFAULT_MAX_LOAD = 3;

    private static final float NEEDLE_DELTA = 0.1f;
    private static final int RAYCAST_RANGE = 3;

    public SyringeItem(Properties properties) {
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
        ServerLevel serverLevel = player.serverLevel();

        CompoundTag tag = stack.getOrCreateTag();
        initTagDefaults(tag);

        boolean inwards = tag.getBoolean(INWARDS);
        float progress = tag.getFloat(SYRINGE_PROGRESS);
        float fluid = tag.getFloat(FLUID_LEVEL);
        String fluidType = tag.getString(FLUID_TYPE);

        progress = updateProgress(tag, progress, inwards);

        if (inwards) {
            if (BLOOD.equals(fluidType) || NONE.equals(fluidType)) {
                fluid = sampleEntity(serverLevel, player, tag, fluid);
            }
            if (NONE.equals(fluidType) || WATER.equals(fluidType)) {
                int waterColor = raytraceForWater(serverLevel, player, RAYCAST_RANGE);
                if (waterColor != -1) {
                    fluid = Mth.clamp(fluid + NEEDLE_DELTA, 0f, DEFAULT_MAX_LOAD);
                    tag.putString(FLUID_TYPE, WATER);
                    tag.putInt(FLUID_COLOUR, waterColor);

                    serverLevel.playSound(player, player.blockPosition(), SoundEvents.BOTTLE_FILL, SoundSource.PLAYERS, 1f, 1.1f);
                }
            }
        }
        else {
            fluid = Mth.clamp(fluid - NEEDLE_DELTA, 0f, DEFAULT_MAX_LOAD);
            if (BLOOD.equals(fluidType)) {
                handleDnaHolderInteraction(player, tag, fluid, progress);
            }
            if (fluid <= 0.01f) {
                if (WATER.equals(fluidType)) {
                    tag.putFloat(DIRTINESS, 0f);
                }

                tag.putString(FLUID_TYPE, NONE);
                DnaUtils.resetBloodSamplingTick(tag);
                DnaUtils.resetBloodFreezerTicks(tag);
                tag.putBoolean(MULTIPLE_SOURCES, false);
                tag.remove(PREVIOUS_TARGET);

                serverLevel.playSound(player, player.blockPosition(), SoundEvents.BOTTLE_EMPTY, SoundSource.PLAYERS, 1f, 1.1f);
            }
        }

        tag.putFloat(FLUID_LEVEL, fluid);

        NetworkHandler.sendSyringeDataClientSyncPacket(
                player,
                player.getInventory().selected,
                progress,
                fluid,
                true,
                inwards,
                tag.getString(FLUID_TYPE),
                tag.getInt(FLUID_COLOUR),
                tag.getFloat(DIRTINESS),
                tag.getInt(BLOOD_CREATION_TICK),
                tag.getInt(BLOOD_FREEZER_TICKS),
                tag.getBoolean(MULTIPLE_SOURCES)
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
                tag.getFloat(FLUID_LEVEL),
                false,
                tag.getBoolean(INWARDS),
                tag.getString(FLUID_TYPE),
                tag.getInt(FLUID_COLOUR),
                tag.getFloat(DIRTINESS),
                tag.getInt(BLOOD_CREATION_TICK),
                tag.getInt(BLOOD_FREEZER_TICKS),
                tag.getBoolean(MULTIPLE_SOURCES)
        );
    }

    private float handleDnaHolderInteraction(ServerPlayer player, CompoundTag syringeTag, float fluid, float progress) {
        ServerLevel serverLevel = player.serverLevel();

        ItemStack offHandStack = player.getItemInHand(InteractionHand.OFF_HAND);
        if (!(offHandStack.getItem() instanceof DnaHolderItem dnaHolder)) return fluid;

        if (progress >= DEFAULT_MAX_LOAD - 0.25f) {
            boolean multipleSources = syringeTag.getBoolean(MULTIPLE_SOURCES);
            boolean clotted = DnaUtils.getFrozenItemEffectiveAge(syringeTag, serverLevel) > BLOOD_CLOTTING_TIME_DEFAULT;
            boolean dirty = syringeTag.getFloat(DIRTINESS) >= 2.75;

            DnaImplementation dna = new DnaImplementation();
            dna.deserializeNBT(syringeTag.getCompound(DNA_DATA));

            if (dna.getSource() == null) return fluid;

            CompoundTag holderTag = offHandStack.getOrCreateTag();
            holderTag.put(DNA_DATA, dna.serializeNBT());

            holderTag.putBoolean(MULTIPLE_SOURCES, multipleSources);
            holderTag.putBoolean(SAMPLE_CLOTTED, clotted);
            holderTag.putBoolean(SAMPLE_DIRTY, dirty);

            syringeTag.remove(DNA_DATA);

            int newProgress = Mth.clamp(holderTag.getInt(SAMPLE_PROGRESS) + 1, 0, DnaHolderItem.DEFAULT_MAX_SAMPLES);

            holderTag.putInt(SAMPLE_PROGRESS, newProgress);

            DnaUtils.resetBloodFreezerTicks(holderTag);
            holderTag.putLong(BLOOD_CREATION_TICK, DnaUtils.getBloodSamplingTick(syringeTag));

            long clottingTime = holderTag.getLong(BLOOD_FREEZER_TICKS);
            if (clottingTime == 0) {
                holderTag.putLong(BLOOD_CLOTTING_TIME, BLOOD_CLOTTING_TIME_DEFAULT);
            }

            int count = offHandStack.getCount();

            if (count > 1) {
                offHandStack.setCount(1);
                ItemStack newOffHandStack = new ItemStack(offHandStack.getItem(), count - 1);
                if (!player.addItem(newOffHandStack)) {
                    ItemEntity itemEntity = new ItemEntity(player.serverLevel(), player.getX(), player.getY(), player.getZ(), newOffHandStack);
                    player.serverLevel().addFreshEntity(itemEntity);
                }
            }

            offHandStack.setTag(holderTag);
            player.setItemInHand(InteractionHand.OFF_HAND, offHandStack);
        }

        return fluid;
    }

    public static void handleSyringeProgress(SyringeDataPacket packet) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        Player player = mc.level.getPlayerByUUID(packet.getPlayerId());
        if (player == null) return;

        int slot = packet.getSlot();
        if (slot < 0 || slot >= player.getInventory().items.size()) return;

        ItemStack stack = player.getInventory().getItem(slot);
        if (!(stack.getItem() instanceof SyringeItem)) return;

        CompoundTag tag = stack.getOrCreateTag();
        tag.putFloat(SYRINGE_PROGRESS, packet.getProgress());
        tag.putBoolean(INWARDS, packet.isInwards());
        tag.putFloat(FLUID_LEVEL, packet.getBlood());
        tag.putString(FLUID_TYPE, packet.getFluidType());
        tag.putInt(FLUID_COLOUR, packet.getFluidColor());
        tag.putFloat(DIRTINESS, packet.getDirtiness());
        tag.putLong(BLOOD_CREATION_TICK, packet.getCreationTick());
        tag.putLong(BLOOD_FREEZER_TICKS, packet.getFreezerTicks());
        tag.putBoolean(MULTIPLE_SOURCES, packet.isMultipleSources());
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
        if (!tag.contains(INWARDS)) tag.putBoolean(INWARDS, true);
        if (!tag.contains(SYRINGE_PROGRESS)) tag.putFloat(SYRINGE_PROGRESS, 0f);
        if (!tag.contains(FLUID_LEVEL)) tag.putFloat(FLUID_LEVEL, 0f);
        if (!tag.contains(FLUID_TYPE)) tag.putString(FLUID_TYPE, NONE);
        if (!tag.contains(FLUID_COLOUR)) tag.putInt(FLUID_COLOUR, 0);
        if (!tag.contains(DIRTINESS)) tag.putFloat(DIRTINESS, 0);
        if (!tag.contains(BLOOD_CREATION_TICK)) tag.putLong(BLOOD_CREATION_TICK, 0);
        if (!tag.contains(BLOOD_FREEZER_TICKS)) tag.putLong(BLOOD_FREEZER_TICKS, 0);
    }

    private float updateProgress(CompoundTag tag, float progress, boolean inwards) {
        progress += inwards ? -NEEDLE_DELTA : NEEDLE_DELTA;
        progress = Mth.clamp(progress, 0f, DEFAULT_MAX_LOAD);
        tag.putFloat(SYRINGE_PROGRESS, progress);

        if (progress >= DEFAULT_MAX_LOAD) tag.putBoolean(INWARDS, true);
        if (progress <= 0f) tag.putBoolean(INWARDS, false);

        return progress;
    }

    private float sampleEntity(ServerLevel serverLevel, ServerPlayer player, CompoundTag tag, float fluid) {
        LivingEntity target = raytraceLiving(serverLevel, player, RAYCAST_RANGE);
        if (target == null) return fluid;

        UUID previous = tag.contains(PREVIOUS_TARGET) ? tag.getUUID(PREVIOUS_TARGET) : null;
        if (previous != null && !target.getUUID().equals(previous)) {
            tag.putBoolean(MULTIPLE_SOURCES, true);
        }

        fluid = Mth.clamp(fluid + NEEDLE_DELTA, 0f, DEFAULT_MAX_LOAD);

        if (NONE.equals(tag.getString(FLUID_TYPE))) {
            target.hurt(player.damageSources().playerAttack(player), 1);
        }

        tag.putString(FLUID_TYPE, BLOOD);
        tag.putInt(FLUID_COLOUR, DEFAULT_BLOOD_COLOR);

        if (fluid > DEFAULT_MAX_LOAD - 0.25f) {
            DnaUtils.saveBloodSamplingTick(tag, serverLevel);

            var cap = target.getCapability(DnaCapability.INSTANCE).orElse(null);

            if (cap.getGenes().isEmpty()) {
                cap.setSource(target.getType());
                cap.setGenes(DnaUtils.generateBaseGenes(target, true));
                cap.setStability(100);
            }

            tag.put(DNA_DATA, cap.serializeNBT());

            serverLevel.playSound(player, player.blockPosition(), SoundEvents.BOTTLE_FILL, SoundSource.PLAYERS, 1f, 0.8f);

            float dirt = tag.getFloat(DIRTINESS);
            dirt = Mth.clamp(dirt + 0.25f, 0f, 3f);
            tag.putFloat(DIRTINESS, dirt);
        }

        tag.putUUID(PREVIOUS_TARGET, target.getUUID());
        return fluid;
    }

    private LivingEntity raytraceLiving(ServerLevel level, ServerPlayer player, double range) {
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

    private int raytraceForWater(ServerLevel level, ServerPlayer player, double range) {
        ClipContext ctx = new ClipContext(
                player.getEyePosition(),
                player.getEyePosition().add(player.getViewVector(1f).scale(range)),
                ClipContext.Block.OUTLINE,
                ClipContext.Fluid.ANY,
                player
        );

        BlockHitResult res = level.clip(ctx);
        if (res == null) return -1;

        BlockPos pos = res.getBlockPos();
        FluidState fs = level.getFluidState(pos);
        boolean isFluidWater = fs.is(FluidTags.WATER);

        boolean isCauldron = level.getBlockState(pos).getBlock() instanceof LayeredCauldronBlock;
        if (!isFluidWater && !isCauldron) return -1;

        if (isCauldron) {
            int levelValue = level.getBlockState(pos).getValue(LayeredCauldronBlock.LEVEL);
            if (levelValue <= 0) return -1;
        }

        return level.getBiome(pos).value().getWaterColor();
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> tooltip, TooltipFlag pIsAdvanced) {
        CompoundTag tag = pStack.getOrCreateTag();
        boolean multipleSources = tag.getBoolean(MULTIPLE_SOURCES);
        boolean clotted = DnaUtils.getFrozenItemEffectiveAge(tag, pLevel) > BLOOD_CLOTTING_TIME_DEFAULT;
        boolean dirty = tag.getFloat(DIRTINESS) >= 2.75;

        DnaUtils.handleContaminatedSampleTooltip(tooltip, multipleSources, clotted, dirty);
    }
}
