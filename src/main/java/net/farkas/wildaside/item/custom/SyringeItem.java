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
import net.minecraft.world.level.block.entity.BlockEntity;
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
            CompoundTag tag = stack.getOrCreateTag();
            initTagDefaults(tag);
            String fluidType = tag.getString(FLUID_TYPE);
            boolean hasFluid = tag.getFloat(FLUID_LEVEL) > 0.01f && !NONE.equals(fluidType);
            tag.putBoolean(INWARDS, !hasFluid);
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
            boolean canFill = fluid < DEFAULT_MAX_LOAD - NEEDLE_DELTA * 0.5f;
            if (canFill) {
                fluid = pullFromDnaHolder(player, tag, fluid);
                fluidType = tag.getString(FLUID_TYPE);
            }

            if (canFill && !tag.contains(DNA_DATA) && (BLOOD.equals(fluidType) || NONE.equals(fluidType))) {
                fluid = sampleEntity(serverLevel, player, tag, fluid);
            }

            if (canFill && (NONE.equals(fluidType) || WATER.equals(fluidType))) {
                int waterColor = raytraceForWater(serverLevel, player, RAYCAST_RANGE);
                if (waterColor != -1) {
                    fluid = Mth.clamp(fluid + NEEDLE_DELTA, 0f, DEFAULT_MAX_LOAD);
                    tag.putString(FLUID_TYPE, WATER);
                    tag.putInt(FLUID_COLOUR, waterColor);
                }
            }
        }
        else {
            fluid = Mth.clamp(fluid - NEEDLE_DELTA, 0f, DEFAULT_MAX_LOAD);
            if (BLOOD.equals(fluidType)) {
                handleDnaHolderInteraction(player, tag, progress);
                injectIntoIncubator(player, tag, serverLevel);
            }
            if (fluid <= 0.01f) {
                if (WATER.equals(fluidType)) {
                    tag.putFloat(DIRTINESS, 0f);
                }

                tag.remove(DNA_DATA);
                tag.putString(FLUID_TYPE, NONE);
                tag.putBoolean(MULTIPLE_SOURCES, false);
                tag.putBoolean(SAMPLE_CLOTTED, false);
                tag.putBoolean(SAMPLE_DIRTY, false);

                tag.remove(PREVIOUS_TARGET);

                DnaUtils.resetBloodFreezerTicks(tag);
                DnaUtils.resetBloodSamplingTick(tag);
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
                tag.getBoolean(MULTIPLE_SOURCES),
                tag.getBoolean(REVEAL_SOURCE),
                tag.getBoolean(REVEAL_STABILITY),
                tag.getBoolean(REVEAL_TRAITS)
        );
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
                tag.getBoolean(MULTIPLE_SOURCES),
                tag.getBoolean(REVEAL_TRAITS),
                tag.getBoolean(REVEAL_STABILITY),
                tag.getBoolean(REVEAL_SOURCE)
        );
    }

    private void handleDnaHolderInteraction(ServerPlayer player, CompoundTag syringeTag, float progress) {
        ServerLevel serverLevel = player.serverLevel();

        ItemStack offHandStack = player.getItemInHand(InteractionHand.OFF_HAND);

        if (!(offHandStack.getItem() instanceof DnaHolderItem)) return;

        if (syringeTag.getInt(SAMPLE_PROGRESS) >= DnaHolderItem.DEFAULT_MAX_SAMPLES) {
            syringeTag.putBoolean(SAMPLE_DIRTY, true);
            return;
        }

        if (progress >= DEFAULT_MAX_LOAD - 0.5f) {
            boolean multipleSources = syringeTag.getBoolean(MULTIPLE_SOURCES);
            boolean clotted = DnaUtils.getFrozenItemEffectiveAge(syringeTag, serverLevel) > BLOOD_CLOTTING_TIME_DEFAULT;
            boolean dirty = syringeTag.getFloat(DIRTINESS) >= 2.75;

            DnaImplementation dna = new DnaImplementation();
            dna.deserializeNBT(syringeTag.getCompound(DNA_DATA));

            if (dna.getSource() == null) return;

            CompoundTag holderTag = offHandStack.getOrCreateTag();

            holderTag.putBoolean(REVEAL_SOURCE, syringeTag.getBoolean(REVEAL_SOURCE));
            holderTag.putBoolean(REVEAL_STABILITY, syringeTag.getBoolean(REVEAL_STABILITY));
            holderTag.putBoolean(REVEAL_TRAITS, syringeTag.getBoolean(REVEAL_TRAITS));
            holderTag.put(DNA_DATA, dna.serializeNBT());
            holderTag.putInt(FLUID_COLOUR, syringeTag.getInt(FLUID_COLOUR));

            syringeTag.putFloat(FLUID_LEVEL, 0f);
            syringeTag.putString(FLUID_TYPE, NONE);

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

            if (cap.getLoci().isEmpty()) {
                cap.setSource(target.getType());
                cap.setLoci(DnaUtils.generateBaseLoci(target));
                cap.setStress(0f);
            }

            tag.put(DNA_DATA, cap.serializeNBT());

            serverLevel.playSound(null, player.blockPosition(), SoundEvents.BOTTLE_FILL, SoundSource.PLAYERS, 0.5f, 0.3f);

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
            AABB bb = e.getBoundingBox().inflate(0.3f);
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

    private float pullFromDnaHolder(ServerPlayer player, CompoundTag syringeTag, float fluid) {
        ItemStack offHandStack = player.getItemInHand(InteractionHand.OFF_HAND);
        if (!(offHandStack.getItem() instanceof DnaHolderItem)) return fluid;

        CompoundTag holderTag = offHandStack.getOrCreateTag();
        if (!holderTag.contains(DNA_DATA) || holderTag.getInt(SAMPLE_PROGRESS) <= 0) return fluid;

        String fluidType = syringeTag.getString(FLUID_TYPE);
        if (!(NONE.equals(fluidType) || BLOOD.equals(fluidType))) return fluid;

        if (syringeTag.contains(DNA_DATA)) return fluid;

        float newFluid = Mth.clamp(fluid + NEEDLE_DELTA, 0f, DEFAULT_MAX_LOAD);
        syringeTag.putString(FLUID_TYPE, BLOOD);

        int holderColor = holderTag.contains(FLUID_COLOUR) ? holderTag.getInt(FLUID_COLOUR) : DEFAULT_BLOOD_COLOR;

        if (holderTag.getBoolean(REVEAL_SOURCE) && holderTag.contains(DNA_DATA)) {
            int eggColor = resolveSourceEggColor(holderTag.getCompound(DNA_DATA));
            holderColor = eggColor != -1 ? eggColor : holderColor;
        }

        syringeTag.putInt(FLUID_COLOUR, holderColor);
        syringeTag.putInt(FLUID_COLOUR, holderColor);

        syringeTag.putBoolean(REVEAL_SOURCE, holderTag.getBoolean(REVEAL_SOURCE));
        syringeTag.putBoolean(REVEAL_STABILITY, holderTag.getBoolean(REVEAL_STABILITY));
        syringeTag.putBoolean(REVEAL_TRAITS, holderTag.getBoolean(REVEAL_TRAITS));

        syringeTag.putLong(BLOOD_CREATION_TICK, holderTag.getLong(BLOOD_CREATION_TICK));
        syringeTag.putLong(BLOOD_FREEZER_TICKS, holderTag.getLong(BLOOD_FREEZER_TICKS));

        syringeTag.putBoolean(MULTIPLE_SOURCES, holderTag.getBoolean(MULTIPLE_SOURCES));
        syringeTag.putBoolean(SAMPLE_CLOTTED, holderTag.getBoolean(SAMPLE_CLOTTED));
        syringeTag.putBoolean(SAMPLE_DIRTY, holderTag.getBoolean(SAMPLE_DIRTY));
        syringeTag.putFloat(DIRTINESS, holderTag.getFloat(DIRTINESS));

        boolean ready = newFluid >= DEFAULT_MAX_LOAD - 0.25f;

        if (ready) {
            CompoundTag dnaCopy = holderTag.getCompound(DNA_DATA).copy();
            syringeTag.put(DNA_DATA, dnaCopy);

            newFluid = DEFAULT_MAX_LOAD;

            holderTag.remove(DNA_DATA);
            holderTag.putInt(SAMPLE_PROGRESS, 0);
            holderTag.putBoolean(SAMPLE_UNUSABLE, false);
            holderTag.putBoolean(MULTIPLE_SOURCES, false);
            holderTag.putBoolean(SAMPLE_CLOTTED, false);
            holderTag.putBoolean(SAMPLE_DIRTY, false);
            holderTag.putBoolean(REVEAL_SOURCE, false);
            holderTag.putBoolean(REVEAL_STABILITY, false);
            holderTag.putBoolean(REVEAL_TRAITS, false);
            holderTag.putLong(BLOOD_CLOTTING_TIME, BLOOD_CLOTTING_TIME_DEFAULT);
            holderTag.remove(BLOOD_FREEZER_TICKS);
            holderTag.remove(BLOOD_CREATION_TICK);
            holderTag.putFloat(DIRTINESS, 0f);
            offHandStack.setTag(holderTag);
            player.setItemInHand(InteractionHand.OFF_HAND, offHandStack);

            player.level().playSound(null, player.blockPosition(), SoundEvents.BOTTLE_FILL, SoundSource.PLAYERS, 0.5f, 0.4f);

        }
        return newFluid;
    }

    private void injectIntoIncubator(ServerPlayer player, CompoundTag syringeTag, ServerLevel level) {
        if (!syringeTag.contains(DNA_DATA)) return;
        if (!BLOOD.equals(syringeTag.getString(FLUID_TYPE))) return;

        BlockHitResult hit = level.clip(new ClipContext(
                player.getEyePosition(),
                player.getEyePosition().add(player.getViewVector(1f).scale(RAYCAST_RANGE)),
                ClipContext.Block.OUTLINE,
                ClipContext.Fluid.ANY,
                player
        ));
        if (hit.getType() != BlockHitResult.Type.BLOCK) return;

        BlockPos pos = hit.getBlockPos();
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof net.farkas.wildaside.block.entity.custom.IncubatorBlockEntity incubator)) return;

        if (!incubator.tryInjectDnaFromSyringe(syringeTag)) return;


        syringeTag.remove(DNA_DATA);
        syringeTag.putFloat(FLUID_LEVEL, 0f);
        syringeTag.putString(FLUID_TYPE, NONE);
        syringeTag.putBoolean(MULTIPLE_SOURCES, false);
        syringeTag.putBoolean(SAMPLE_CLOTTED, false);
        syringeTag.putBoolean(SAMPLE_DIRTY, false);
        syringeTag.putFloat(DIRTINESS, 0f);
        syringeTag.putLong(BLOOD_CREATION_TICK, 0);
        syringeTag.putLong(BLOOD_FREEZER_TICKS, 0);

        level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.PLAYERS, 0.5f, 0.4f);
    }

    private int resolveSourceEggColor(CompoundTag dnaTag) {
        DnaImplementation dna = new DnaImplementation();
        dna.deserializeNBT(dnaTag);
        if (dna.getSource() == null) return -1;
        var egg = net.minecraftforge.common.ForgeSpawnEggItem.fromEntityType(dna.getSource());
        return egg != null ? egg.getColor(0) : -1;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> tooltip, TooltipFlag pIsAdvanced) {
        CompoundTag tag = pStack.getOrCreateTag();
        boolean analyzed = tag.getBoolean(REVEAL_SOURCE) || tag.getBoolean(REVEAL_STABILITY) || tag.getBoolean(REVEAL_TRAITS);
        if (!analyzed) {
            boolean multipleSources = tag.getBoolean(MULTIPLE_SOURCES);
            boolean clotted = DnaUtils.getFrozenItemEffectiveAge(tag, pLevel) > BLOOD_CLOTTING_TIME_DEFAULT;
            boolean dirty = tag.getFloat(DIRTINESS) >= 2.75;
            DnaUtils.handleContaminatedSampleTooltip(tooltip, multipleSources, clotted, dirty);
        }
        else {
            tag.putBoolean(SAMPLE_UNUSABLE, false);
        }
    }
}
