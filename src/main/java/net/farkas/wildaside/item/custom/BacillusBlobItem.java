package net.farkas.wildaside.item.custom;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.capability.dna.DnaCapability;
import net.farkas.wildaside.capability.dna.DnaImplementation;
import net.farkas.wildaside.dna.bacillus_blob.BacillusBlobConsumption;
import net.farkas.wildaside.dna.DnaUtils;
import net.farkas.wildaside.dna.locus.GeneLocus;
import net.farkas.wildaside.dna.locus.LocusSource;
import net.farkas.wildaside.dna.trait.Trait;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static net.farkas.wildaside.dna.DnaConstants.*;

public class BacillusBlobItem extends Item {
    public static final String BLOB_MATURITY = "blob_maturity";
    public static final String BLOB_MUTATION_RISK = "blob_mutation_risk";
    public static final String BLOB_INCUBATED = "blob_incubated";
    public static final String BLOB_EXTRACTION_TICK = "blob_extraction_tick";
    public static final String BLOB_EXPIRED = "blob_expired";

    public static final long DEFAULT_VIABILITY_TIME = 20 * 60 * 3;

    public static final float MATURITY_MIN_USABLE = 0.25f;
    public static final float MATURITY_POOR = 0.50f;
    public static final float MATURITY_SUBOPTIMAL = 0.75f;
    public static final float MATURITY_OPTIMAL_MIN = 0.95f;
    public static final float MATURITY_OPTIMAL_MAX = 1.05f;
    public static final float MATURITY_OVERHEATED = 1.25f;
    public static final float MATURITY_CRITICAL = 1.50f;

    public static final int MUTATION_RISK_LOW = 100;
    public static final int MUTATION_RISK_MEDIUM = 300;
    public static final int MUTATION_RISK_HIGH = 500;
    public static final int MUTATION_RISK_SEVERE = 750;

    public BacillusBlobItem(Properties props) {
        super(props);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        if (canBeUsed(stack)) {
            return UseAnim.EAT;
        }
        return UseAnim.NONE;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        if (canBeUsed(stack)) {
            float maturity = getMaturity(stack);
            if (maturity < MATURITY_SUBOPTIMAL) {
                return 72;
            }
            else if (maturity < MATURITY_OPTIMAL_MIN) {
                return 56;
            }
            return 48;
        }
        return 0;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (checkAndMarkExpired(stack, level)) {
            if (!level.isClientSide) {
                player.displayClientMessage(
                        Component.translatable("item.wildaside.bacillus_blob.expired")
                                .withStyle(ChatFormatting.DARK_RED),
                        true
                );
            }
            return InteractionResultHolder.fail(stack);
        }

        if (!hasDna(stack)) {
            if (!level.isClientSide) {
                player.displayClientMessage(
                        Component.translatable("item.wildaside.bacillus_blob.no_dna")
                                .withStyle(ChatFormatting.RED),
                        true
                );
            }
            return InteractionResultHolder.fail(stack);
        }

        if (!isIncubated(stack)) {
            if (!level.isClientSide) {
                player.displayClientMessage(
                        Component.translatable("item.wildaside.bacillus_blob.not_incubated")
                                .withStyle(ChatFormatting.YELLOW),
                        true
                );
            }
            return InteractionResultHolder.fail(stack);
        }

        float maturity = getMaturity(stack);
        if (maturity < MATURITY_MIN_USABLE) {
            if (!level.isClientSide) {
                player.displayClientMessage(
                        Component.translatable("item.wildaside.bacillus_blob.too_immature",
                                        String.format("%.0f%%", maturity * 100))
                                .withStyle(ChatFormatting.RED),
                        true
                );
            }
            return InteractionResultHolder.fail(stack);
        }

        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!level.isClientSide()) {
            if (checkAndMarkExpired(stack, level)) {
                if (entity instanceof Player player) {
                    player.displayClientMessage(
                            Component.translatable("item.wildaside.bacillus_blob.expired")
                                    .withStyle(ChatFormatting.DARK_RED),
                            true
                    );
                }
                return stack;
            }

            CompoundTag tag = stack.getOrCreateTag();
            float maturity = getMaturity(stack);
            int mutationRisk = getMutationRisk(stack);

            WildAside.LOGGER.info("Entity {} consuming bacillus blob (maturity: {}%, risk: {}‰)",
                    entity.getName().getString(),
                    String.format("%.1f", maturity * 100),
                    mutationRisk);

            BlobQuality quality = assessQuality(maturity, mutationRisk);
            applyPreConsumptionEffects(entity, quality, maturity, mutationRisk);

            modifyDnaByQuality(tag, quality, maturity, mutationRisk, entity);

            boolean success = BacillusBlobConsumption.consume(entity, tag);

            if (success) {
                level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                        SoundEvents.PLAYER_BURP, SoundSource.PLAYERS, 1.0f, 0.8f);

                applyPostConsumptionEffects(entity, quality, maturity, mutationRisk);

                stack.shrink(1);

                WildAside.LOGGER.info("Bacillus blob consumed successfully by {} (quality: {})",
                        entity.getName().getString(), quality);

                if (entity instanceof Player player) {
                    player.displayClientMessage(
                            getConsumptionMessage(quality),
                            true
                    );
                }
            }
            else {
                WildAside.LOGGER.warn("Bacillus blob consumption failed for {}", entity.getName().getString());
                if (entity instanceof Player player) {
                    player.displayClientMessage(
                            Component.translatable("item.wildaside.bacillus_blob.consume_failed")
                                    .withStyle(ChatFormatting.RED),
                            true
                    );
                }
            }
        }

        return stack;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (player.level().isClientSide()) {
            return InteractionResult.PASS;
        }

        if (target instanceof Player) {
            return InteractionResult.PASS;
        }

        if (checkAndMarkExpired(stack, player.level())) {
            player.displayClientMessage(
                    Component.translatable("item.wildaside.bacillus_blob.expired")
                            .withStyle(ChatFormatting.DARK_RED),
                    true
            );
            return InteractionResult.FAIL;
        }

        if (!canBeUsed(stack)) {
            sendCannotUseMessage(stack, player);
            return InteractionResult.FAIL;
        }

        float maturity = getMaturity(stack);
        if (maturity < MATURITY_MIN_USABLE) {
            player.displayClientMessage(
                    Component.translatable("item.wildaside.bacillus_blob.too_immature",
                                    String.format("%.0f%%", maturity * 100))
                            .withStyle(ChatFormatting.RED),
                    true
            );
            return InteractionResult.FAIL;
        }

        if (!canApplyDnaTo(target)) {
            player.displayClientMessage(
                    Component.translatable("item.wildaside.bacillus_blob.cannot_apply", target.getName())
                            .withStyle(ChatFormatting.RED),
                    true
            );
            return InteractionResult.FAIL;
        }

        CompoundTag tag = stack.getOrCreateTag();
        int mutationRisk = getMutationRisk(stack);

        WildAside.LOGGER.info("Applying bacillus blob to {} (maturity: {}%, risk: {}‰)",
                target.getName().getString(),
                String.format("%.1f", maturity * 100),
                mutationRisk);

        BlobQuality quality = assessQuality(maturity, mutationRisk);
        modifyDnaByQuality(tag, quality, maturity, mutationRisk, target);

        boolean success = BacillusBlobConsumption.consume(target, tag);

        if (success) {
            player.level().playSound(null, target.getX(), target.getY(), target.getZ(),
                    SoundEvents.SLIME_SQUISH, SoundSource.NEUTRAL, 1.0f, 1.0f);

            applyPostConsumptionEffects(target, quality, maturity, mutationRisk);

            stack.shrink(1);

            player.displayClientMessage(
                    Component.translatable("item.wildaside.bacillus_blob.applied", target.getName())
                            .append(Component.literal(" "))
                            .append(getQualityIndicator(quality)),
                    true
            );

            WildAside.LOGGER.info("Bacillus blob applied successfully to {} (quality: {})",
                    target.getName().getString(), quality);
            return InteractionResult.SUCCESS;
        }

        player.displayClientMessage(
                Component.translatable("item.wildaside.bacillus_blob.apply_failed")
                        .withStyle(ChatFormatting.RED),
                true
        );
        return InteractionResult.FAIL;
    }

    private void sendCannotUseMessage(ItemStack stack, Player player) {
        if (!hasDna(stack)) {
            player.displayClientMessage(
                    Component.translatable("item.wildaside.bacillus_blob.no_dna")
                            .withStyle(ChatFormatting.RED),
                    true
            );
        }
        else if (!isIncubated(stack)) {
            player.displayClientMessage(
                    Component.translatable("item.wildaside.bacillus_blob.not_incubated")
                            .withStyle(ChatFormatting.YELLOW),
                    true
            );
        }
    }

    private boolean canApplyDnaTo(LivingEntity entity) {
        return entity.getCapability(DnaCapability.INSTANCE).isPresent();
    }

    public enum BlobQuality {
        TERRIBLE,
        POOR,
        SUBOPTIMAL,
        OPTIMAL,
        OVERHEATED,
        CRITICAL
    }

    public static BlobQuality assessQuality(float maturity, int mutationRisk) {
        if (mutationRisk >= MUTATION_RISK_SEVERE) {
            return BlobQuality.CRITICAL;
        }

        if (maturity < MATURITY_POOR) {
            return BlobQuality.TERRIBLE;
        }
        else if (maturity < MATURITY_SUBOPTIMAL) {
            return BlobQuality.POOR;
        }
        else if (maturity < MATURITY_OPTIMAL_MIN) {
            return BlobQuality.SUBOPTIMAL;
        }
        else if (maturity <= MATURITY_OPTIMAL_MAX) {
            if (mutationRisk >= MUTATION_RISK_HIGH) {
                return BlobQuality.OVERHEATED;
            }
            return BlobQuality.OPTIMAL;
        }
        else if (maturity <= MATURITY_OVERHEATED) {
            return BlobQuality.OVERHEATED;
        }
        else {
            return BlobQuality.CRITICAL;
        }
    }

    private void applyPreConsumptionEffects(LivingEntity entity, BlobQuality quality, float maturity, int mutationRisk) {
        switch (quality) {
            case TERRIBLE -> {
                entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 1, false, true, true));
                entity.addEffect(new MobEffectInstance(MobEffects.HUNGER, 400, 2, false, true, true));
            }
            case POOR -> {
                entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 100, 0, false, true, true));
                entity.addEffect(new MobEffectInstance(MobEffects.HUNGER, 200, 1, false, true, true));
            }
            case SUBOPTIMAL -> {
                entity.addEffect(new MobEffectInstance(MobEffects.HUNGER, 100, 0, false, true, true));
            }
            case OPTIMAL -> {

            }
            case OVERHEATED -> {
                entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 0, false, true, true));
            }
            case CRITICAL -> {
                entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 400, 1, false, true, true));
                entity.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 0, false, true, true));
                entity.hurt(entity.damageSources().magic(), 2f);
            }
        }
    }

    private void applyPostConsumptionEffects(LivingEntity entity, BlobQuality quality, float maturity, int mutationRisk) {
        switch (quality) {
            case TERRIBLE -> {
                entity.getCapability(DnaCapability.INSTANCE).ifPresent(dna -> {
                    dna.setStress(dna.getStress() + 30f);
                });
            }
            case POOR -> {
                entity.getCapability(DnaCapability.INSTANCE).ifPresent(dna -> {
                    dna.setStress(dna.getStress() + 20f);
                });
            }
            case SUBOPTIMAL -> {
                entity.getCapability(DnaCapability.INSTANCE).ifPresent(dna -> {
                    dna.setStress(dna.getStress() + 10f);
                });
            }
            case OPTIMAL -> {
                entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 0, false, true, true));
            }
            case OVERHEATED -> {
                entity.getCapability(DnaCapability.INSTANCE).ifPresent(dna -> {
                    dna.setStress(dna.getStress() + 15f);
                });
            }
            case CRITICAL -> {
                entity.getCapability(DnaCapability.INSTANCE).ifPresent(dna -> {
                    dna.setStress(dna.getStress() + 35f);
                });
                entity.addEffect(new MobEffectInstance(MobEffects.WITHER, 60, 0, false, true, true));
            }
        }
    }

    private void modifyDnaByQuality(CompoundTag tag, BlobQuality quality, float maturity, int mutationRisk, LivingEntity target) {
        if (!tag.contains(DNA_DATA)) return;

        DnaImplementation dna = new DnaImplementation();
        dna.deserializeNBT(tag.getCompound(DNA_DATA));

        long seed = target.getUUID().getLeastSignificantBits() ^ System.currentTimeMillis();
        boolean modified = false;

        for (Map.Entry<Trait, List<GeneLocus>> entry : dna.getLoci().entrySet()) {
            List<GeneLocus> loci = new ArrayList<>(entry.getValue());

            for (int i = 0; i < loci.size(); i++) {
                GeneLocus locus = loci.get(i);
                GeneLocus newLocus = locus;

                switch (quality) {
                    case TERRIBLE -> {
                        if (DnaUtils.hashToFloat(seed, locus.getId() + "_terrible", i) < 0.7f) {
                            newLocus = new GeneLocus(
                                    locus.getId(),
                                    locus.getAlleleA(),
                                    locus.getAlleleB(),
                                    locus.getFlags(),
                                    locus.getStability() * 0.5f,
                                    LocusSource.REJECTED,
                                    0,
                                    0.3f
                            );
                            modified = true;
                        }
                    }
                    case POOR -> {
                        if (DnaUtils.hashToFloat(seed, locus.getId() + "_poor", i) < 0.5f) {
                            newLocus = new GeneLocus(
                                    locus.getId(),
                                    locus.getAlleleA(),
                                    locus.getAlleleB(),
                                    locus.getFlags(),
                                    locus.getStability() * 0.7f,
                                    LocusSource.TRANSIENT,
                                    0,
                                    0.15f
                            );
                            modified = true;
                        }
                    }
                    case SUBOPTIMAL -> {
                        if (DnaUtils.hashToFloat(seed, locus.getId() + "_subopt", i) < 0.25f) {
                            newLocus = new GeneLocus(
                                    locus.getId(),
                                    locus.getAlleleA(),
                                    locus.getAlleleB(),
                                    locus.getFlags(),
                                    locus.getStability() * 0.85f,
                                    LocusSource.TRANSIENT,
                                    0,
                                    0.05f
                            );
                            modified = true;
                        }
                    }
                    case OPTIMAL -> {

                    }
                    case OVERHEATED -> {
                        float mutChance = mutationRisk / 1000f;
                        if (DnaUtils.hashToFloat(seed, locus.getId() + "_heat", i) < mutChance) {
                            newLocus = applyHeatMutation(locus, seed, i);
                            modified = true;
                        }
                    }
                    case CRITICAL -> {
                        float mutChance = Math.min(0.9f, mutationRisk / 750f);
                        if (DnaUtils.hashToFloat(seed, locus.getId() + "_critical", i) < mutChance) {
                            newLocus = applySevereHeatMutation(locus, seed, i);
                            modified = true;
                        }
                    }
                }

                loci.set(i, newLocus);
            }
            entry.setValue(loci);
        }

        if (modified) {
            tag.put(DNA_DATA, dna.serializeNBT());
            WildAside.LOGGER.info("DNA modified due to blob quality: {}", quality);
        }
    }

    private GeneLocus applyHeatMutation(GeneLocus locus, long seed, int index) {
        WildAside.LOGGER.debug("Applying heat mutation to locus [{}]", locus.getId());
        return new GeneLocus(
                locus.getId() + "_heat",
                locus.getAlleleA(),
                locus.getAlleleB(),
                locus.getFlags(),
                locus.getStability() * 0.75f,
                LocusSource.TRANSIENT,
                0,
                0.1f
        );
    }

    private GeneLocus applySevereHeatMutation(GeneLocus locus, long seed, int index) {
        WildAside.LOGGER.debug("Applying SEVERE heat mutation to locus [{}]", locus.getId());
        return new GeneLocus(
                locus.getId() + "_burn",
                locus.getAlleleA(),
                locus.getAlleleB(),
                locus.getFlags(),
                locus.getStability() * 0.5f,
                LocusSource.REJECTED,
                0,
                0.25f
        );
    }

    private Component getConsumptionMessage(BlobQuality quality) {
        return switch (quality) {
            case TERRIBLE -> Component.translatable("item.wildaside.bacillus_blob.consumed_terrible")
                    .withStyle(ChatFormatting.DARK_RED);
            case POOR -> Component.translatable("item.wildaside.bacillus_blob.consumed_poor")
                    .withStyle(ChatFormatting.RED);
            case SUBOPTIMAL -> Component.translatable("item.wildaside.bacillus_blob.consumed_suboptimal")
                    .withStyle(ChatFormatting.YELLOW);
            case OPTIMAL -> Component.translatable("item.wildaside.bacillus_blob.consumed_optimal")
                    .withStyle(ChatFormatting.GREEN);
            case OVERHEATED -> Component.translatable("item.wildaside.bacillus_blob.consumed_overheated")
                    .withStyle(ChatFormatting.GOLD);
            case CRITICAL -> Component.translatable("item.wildaside.bacillus_blob.consumed_critical")
                    .withStyle(ChatFormatting.DARK_RED);
        };
    }

    private Component getQualityIndicator(BlobQuality quality) {
        return switch (quality) {
            case TERRIBLE -> Component.literal("(✗✗)").withStyle(ChatFormatting.DARK_RED);
            case POOR -> Component.literal("(✗)").withStyle(ChatFormatting.RED);
            case SUBOPTIMAL -> Component.literal("(~)").withStyle(ChatFormatting.YELLOW);
            case OPTIMAL -> Component.literal("(✓)").withStyle(ChatFormatting.GREEN);
            case OVERHEATED -> Component.literal("(⚠)").withStyle(ChatFormatting.GOLD);
            case CRITICAL -> Component.literal("(☠)").withStyle(ChatFormatting.DARK_RED);
        };
    }

    public static long getEffectiveAge(ItemStack stack, Level level) {
        if (level == null || !stack.hasTag()) return 0;

        CompoundTag tag = stack.getTag();
        if (!tag.contains(BLOB_EXTRACTION_TICK)) return 0;

        long extractionTick = tag.getLong(BLOB_EXTRACTION_TICK);
        long currentTick = level.getGameTime();

        return Math.max(0, currentTick - extractionTick);
    }

    public static long getViabilityTime(ItemStack stack) {
        return DEFAULT_VIABILITY_TIME;
    }

    public static long getRemainingTime(ItemStack stack, Level level) {
        if (!isExtracted(stack)) return getViabilityTime(stack);

        long age = getEffectiveAge(stack, level);
        long viability = getViabilityTime(stack);
        return Math.max(0, viability - age);
    }

    public static boolean checkAndMarkExpired(ItemStack stack, Level level) {
        if (!stack.hasTag()) return false;

        CompoundTag tag = stack.getTag();

        if (tag.getBoolean(BLOB_EXPIRED)) return true;
        if (!tag.contains(BLOB_EXTRACTION_TICK)) return false;

        if (getEffectiveAge(stack, level) >= getViabilityTime(stack)) {
            tag.putBoolean(BLOB_EXPIRED, true);
            WildAside.LOGGER.info("Bacillus blob expired after {} ticks", getEffectiveAge(stack, level));
            return true;
        }

        return false;
    }

    public static boolean isExtracted(ItemStack stack) {
        return stack.hasTag() && stack.getTag().contains(BLOB_EXTRACTION_TICK);
    }

    public static void setExtractionTick(ItemStack stack, long tick) {
        stack.getOrCreateTag().putLong(BLOB_EXTRACTION_TICK, tick);
        WildAside.LOGGER.info("Blob extracted at tick {}, will expire at tick {}",
                tick, tick + getViabilityTime(stack));
    }

    public static boolean hasDna(ItemStack stack) {
        if (!stack.hasTag()) {
            return false;
        }

        CompoundTag tag = stack.getTag();
        boolean containsKey = tag.contains(DNA_DATA);
        boolean isEmpty = containsKey && tag.getCompound(DNA_DATA).isEmpty();

        return containsKey && !isEmpty;
    }

    public static boolean isIncubated(ItemStack stack) {
        if (!stack.hasTag()) return false;
        return stack.getTag().getBoolean(BLOB_INCUBATED);
    }

    public static float getMaturity(ItemStack stack) {
        if (!stack.hasTag()) return 0f;
        return stack.getTag().getFloat(BLOB_MATURITY);
    }

    public static int getMutationRisk(ItemStack stack) {
        if (!stack.hasTag()) return 0;
        return stack.getTag().getInt(BLOB_MUTATION_RISK);
    }

    public static boolean isMarkedExpired(ItemStack stack) {
        if (!stack.hasTag()) return false;
        return stack.getTag().getBoolean(BLOB_EXPIRED);
    }

    public static boolean canBeUsed(ItemStack stack) {
        return hasDna(stack) && isIncubated(stack) && !isMarkedExpired(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        CompoundTag tag = stack.getOrCreateTag();

        if (level != null && checkAndMarkExpired(stack, level)) {
            tooltip.add(Component.translatable("item.wildaside.bacillus_blob.expired")
                    .withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD));
            tooltip.add(Component.translatable("item.wildaside.bacillus_blob.expired_desc")
                    .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
            return;
        }

        if (hasDna(stack)) {
            DnaImplementation dna = new DnaImplementation();
            dna.deserializeNBT(tag.getCompound(DNA_DATA));

            if (dna.getSource() != null) {
                String sourceName = ForgeRegistries.ENTITY_TYPES.getKey(dna.getSource()).getPath();
                tooltip.add(Component.translatable("item.wildaside.bacillus_blob.source", sourceName)
                        .withStyle(ChatFormatting.GRAY));
            }

            int traitCount = dna.getLoci().size();
            int lociCount = dna.getLoci().values().stream().mapToInt(List::size).sum();
            tooltip.add(Component.translatable("item.wildaside.bacillus_blob.traits", traitCount, lociCount)
                    .withStyle(ChatFormatting.DARK_GRAY));

            tooltip.add(Component.translatable("item.wildaside.bacillus_blob.has_dna")
                    .withStyle(ChatFormatting.GREEN));
        }
        else {
            tooltip.add(Component.translatable("item.wildaside.bacillus_blob.no_dna")
                    .withStyle(ChatFormatting.RED));
        }

        tooltip.add(Component.empty());

        if (isIncubated(stack)) {
            float maturity = getMaturity(stack);
            int mutationRisk = getMutationRisk(stack);
            BlobQuality quality = assessQuality(maturity, mutationRisk);

            ChatFormatting maturityColor = getMaturityColor(maturity);
            String maturityStr = String.format("%.0f%%", maturity * 100);
            tooltip.add(Component.translatable("item.wildaside.bacillus_blob.maturity", maturityStr)
                    .withStyle(maturityColor)
                    .append(Component.literal(" "))
                    .append(getQualityIndicator(quality)));

            tooltip.add(getMaturityDescription(maturity).copy().withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));

            if (mutationRisk > 0) {
                ChatFormatting riskColor = getMutationRiskColor(mutationRisk);
                tooltip.add(Component.translatable("item.wildaside.bacillus_blob.mutation_risk",
                                String.format("%.1f%%", mutationRisk / 10f))
                        .withStyle(riskColor));
            }

            if (isExtracted(stack) && level != null) {
                long remaining = getRemainingTime(stack, level);
                long viability = getViabilityTime(stack);
                float percentRemaining = (float) remaining / viability;

                tooltip.add(Component.empty());

                ChatFormatting timeColor = percentRemaining > 0.5f ? ChatFormatting.GREEN :
                        percentRemaining > 0.25f ? ChatFormatting.YELLOW : ChatFormatting.RED;

                String timeStr = formatTime(remaining);
                tooltip.add(Component.translatable("item.wildaside.bacillus_blob.viability", timeStr)
                        .withStyle(timeColor));

                if (percentRemaining <= 0.25f) {
                    tooltip.add(Component.translatable("item.wildaside.bacillus_blob.expiring_soon")
                            .withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
                }

                tooltip.add(Component.translatable("item.wildaside.bacillus_blob.no_preserve")
                        .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
            }

            if (canBeUsed(stack) && maturity >= MATURITY_MIN_USABLE) {
                tooltip.add(Component.empty());
                tooltip.add(Component.translatable("item.wildaside.bacillus_blob.ready")
                        .withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD));
                tooltip.add(Component.translatable("item.wildaside.bacillus_blob.consume_hint")
                        .withStyle(ChatFormatting.DARK_GREEN, ChatFormatting.ITALIC));
                tooltip.add(Component.translatable("item.wildaside.bacillus_blob.apply_hint")
                        .withStyle(ChatFormatting.DARK_GREEN, ChatFormatting.ITALIC));
            }
        }
        else {
            tooltip.add(Component.translatable("item.wildaside.bacillus_blob.not_incubated")
                    .withStyle(ChatFormatting.YELLOW));
            tooltip.add(Component.translatable("item.wildaside.bacillus_blob.incubate_hint")
                    .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
        }
    }

    private ChatFormatting getMaturityColor(float maturity) {
        if (maturity < MATURITY_POOR) return ChatFormatting.DARK_RED;
        if (maturity < MATURITY_SUBOPTIMAL) return ChatFormatting.RED;
        if (maturity < MATURITY_OPTIMAL_MIN) return ChatFormatting.YELLOW;
        if (maturity <= MATURITY_OPTIMAL_MAX) return ChatFormatting.GREEN;
        if (maturity <= MATURITY_OVERHEATED) return ChatFormatting.GOLD;
        return ChatFormatting.DARK_RED;
    }

    private Component getMaturityDescription(float maturity) {
        if (maturity < MATURITY_MIN_USABLE) {
            return Component.translatable("item.wildaside.bacillus_blob.maturity_unusable");
        }
        else if (maturity < MATURITY_POOR) {
            return Component.translatable("item.wildaside.bacillus_blob.maturity_terrible");
        }
        else if (maturity < MATURITY_SUBOPTIMAL) {
            return Component.translatable("item.wildaside.bacillus_blob.maturity_poor");
        }
        else if (maturity < MATURITY_OPTIMAL_MIN) {
            return Component.translatable("item.wildaside.bacillus_blob.maturity_suboptimal");
        }
        else if (maturity <= MATURITY_OPTIMAL_MAX) {
            return Component.translatable("item.wildaside.bacillus_blob.maturity_optimal");
        }
        else if (maturity <= MATURITY_OVERHEATED) {
            return Component.translatable("item.wildaside.bacillus_blob.maturity_overheated");
        }
        else {
            return Component.translatable("item.wildaside.bacillus_blob.maturity_critical");
        }
    }

    private ChatFormatting getMutationRiskColor(int risk) {
        if (risk < MUTATION_RISK_LOW) return ChatFormatting.GRAY;
        if (risk < MUTATION_RISK_MEDIUM) return ChatFormatting.YELLOW;
        if (risk < MUTATION_RISK_HIGH) return ChatFormatting.GOLD;
        if (risk < MUTATION_RISK_SEVERE) return ChatFormatting.RED;
        return ChatFormatting.DARK_RED;
    }

    private String formatTime(long ticks) {
        long totalSeconds = ticks / 20;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;

        if (minutes > 0) {
            return String.format("%dm %ds", minutes, seconds);
        }
        else {
            return String.format("%ds", seconds);
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        if (!canBeUsed(stack)) return false;
        float maturity = getMaturity(stack);
        int risk = getMutationRisk(stack);
        return assessQuality(maturity, risk) == BlobQuality.OPTIMAL;
    }
}