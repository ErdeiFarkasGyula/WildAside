package net.farkas.wildaside.item.custom;

import net.farkas.wildaside.capability.dna.DnaCapability;
import net.farkas.wildaside.capability.dna.DnaImplementation;
import net.farkas.wildaside.config.ModConfig;
import net.farkas.wildaside.dna.DnaUtils;
import net.farkas.wildaside.dna.Gene;
import net.farkas.wildaside.dna.speed.MobSpeedResultStorage;
import net.farkas.wildaside.dna.traits.Trait;
import net.farkas.wildaside.dna.traits.TraitTypes;
import net.farkas.wildaside.dna.traits.Traits;
import net.farkas.wildaside.sound.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class DnaHolder extends Item {
    public static final int MAX_COOLDOWN = 60;

    public static final String DNA_DATA = "dna_data";
    public static final String NO_DNA_DATA = "no_dna_data";
    public static final String DNA_DATA_HIDDEN = "dna_data_hidden";
    public static final String DNA_MUTATED = "dna_mutated";
    public static final String DNA_MISMATCH = "dna_mismatch";

    public static final String SAMPLE_PROGRESS = "sample_progress";
    public static final String MAX_SAMPLES = "max_samples";

    public static final String REVEAL_SOURCE = "reveal_source";
    public static final String REVEAL_STABILITY = "reveal_stability";
    public static final String REVEAL_TRAITS = "reveal_traits";

    public static final String STABILITY = "stability";
    public static final String SOURCE = "source";
    public static final String UNKNOWN = "unknown";

    public static final String CORE_TRAITS = "core_traits";
    public static final String RESISTANCES = "resistances";
    public static final String ABILITIES = "abilities";

    public static final int DEFAULT_MAX_SAMPLES = 3;

    public DnaHolder(Properties pProperties) {
        super(pProperties);
    }

    public boolean hasDna(ItemStack stack) {
        return stack.getOrCreateTag().getInt(SAMPLE_PROGRESS) > 0;
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().getInt(SAMPLE_PROGRESS) > 0) {
            return 1;
        }
        return super.getMaxStackSize(stack);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (hand == InteractionHand.OFF_HAND || player.level().isClientSide()) return InteractionResult.PASS;

        CompoundTag tag = stack.getOrCreateTag();
        CompoundTag dnaTag = tag.getCompound(DNA_DATA);
        DnaImplementation dna = new DnaImplementation();
        if (dnaTag != null && !dnaTag.isEmpty()) {
            dna.deserializeNBT(dnaTag);

            if (dna.source() != null && dna.source() != target.getType()) {
                player.displayClientMessage(getTranslatable(DNA_MISMATCH).withStyle(ChatFormatting.RED), true);
                return InteractionResult.sidedSuccess(player.level().isClientSide());
            }
        }

        if (stack.getCount() > 1) {
            ItemStack single = stack.split(1);
            applyDna(player, target, single);

            if (!player.getInventory().add(single)) {
                player.drop(single, false);
            }
        } else {
            applyDna(player, target, stack);
            player.setItemInHand(hand, stack);
        }

        return InteractionResult.sidedSuccess(player.level().isClientSide());
    }

    private ItemStack applyDna(Player player, LivingEntity target, ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        int maxProgress = DEFAULT_MAX_SAMPLES;
        int progress = tag.getInt(SAMPLE_PROGRESS);

        if (progress >= maxProgress) return stack;

        DnaImplementation existingDna;
        if (tag.contains(DNA_DATA)) {
            existingDna = new DnaImplementation();
            existingDna.deserializeNBT(tag.getCompound(DNA_DATA));

            if (!Objects.equals(existingDna.source(), target.getType())) {
                return stack;
            }
        } else {
            existingDna = null;
        }

        progress = Math.min(progress + 1, maxProgress);
        tag.putInt(SAMPLE_PROGRESS, progress);

        AtomicReference<Map<Trait, Gene>> baseGenes = new AtomicReference<>(DnaUtils.generateBaseGenes(target, false));
        DnaImplementation newDna = new DnaImplementation();

        target.getCapability(DnaCapability.INSTANCE).ifPresent(dna -> {
            boolean hasMutated = target.getPersistentData().getBoolean(DNA_MUTATED);
            if (!hasMutated) {
                baseGenes.set(DnaUtils.mutateGenes(dna.genes(), target));
                if (ModConfig.ACCURATE_DNA_MOVEMENT_SPEEDS.get()) {
                    Trait trait = Traits.MOVEMENT_SPEED;
                    Gene gene = DnaUtils.mutateGene(new Gene(trait, (float) MobSpeedResultStorage.getSpeed(target.getType(), "ground"), trait.baseInstability()), target);
                    baseGenes.get().replace(trait, gene);
                }
            } else {
                baseGenes.set(dna.genes());
            }

            float currentStability;
            if (existingDna != null) {
                currentStability = existingDna.stability();
            } else {
                currentStability = 0;
            }

            float targetStability = dna.stability();
            float progressMultiplier = 1f / maxProgress;
            float remainingPercent = player.getCooldowns().getCooldownPercent(stack.getItem(), 0f);
            float cooldownMultiplier = 1.0f - remainingPercent;
            float finalStability = currentStability + (targetStability * progressMultiplier) * (cooldownMultiplier);

            newDna.setStability(Math.max(0f, Math.min(100f, finalStability)));

            player.level().playSound(null, player.blockPosition(), ModSounds.MUCELLITH_DEATH.get(), SoundSource.PLAYERS, remainingPercent, 0.2f);
        });

        newDna.setSource(target.getType());
        newDna.setGenes(baseGenes.get());

        if (existingDna != null) {
            Map<Trait, Gene> averaged = new HashMap<>();
            for (Trait trait : Traits.TRAITS) {
                Gene newGene = baseGenes.get().get(trait);
                if (newGene == null) continue;

                Gene oldGene = existingDna.genes().get(trait);
                float oldValue = oldGene != null ? oldGene.value() : 0f;
                float avg = ((oldValue * (progress - 1)) + newGene.value()) / progress;
                float stabilityCost = newGene.stabilityCost();

                averaged.put(trait, new Gene(trait, avg, stabilityCost));
            }
            newDna.setGenes(averaged);
        }

        tag.put(DNA_DATA, newDna.serializeNBT());

        tag.putBoolean(REVEAL_SOURCE, false);
        tag.putBoolean(REVEAL_STABILITY, false);
        tag.putBoolean(REVEAL_TRAITS, false);

        stack.setTag(tag);

        player.getCooldowns().addCooldown(this, MAX_COOLDOWN);
        player.level().playSound(null, target.blockPosition(), SoundEvents.BOTTLE_FILL, SoundSource.PLAYERS, 1, 1);

        return stack;
    }

    @Override
    public boolean onDroppedByPlayer(ItemStack item, Player player) {
        CompoundTag tag = item.getOrCreateTag();
        tag.putBoolean(REVEAL_SOURCE, true);
        tag.putBoolean(REVEAL_STABILITY, true);
        tag.putBoolean(REVEAL_TRAITS, true);
        item.setTag(tag);
        return super.onDroppedByPlayer(item, player);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltip, isAdvanced);

        CompoundTag tag = stack.getOrCreateTag();
        int progress = getSampleProgress(stack);
        int maxSamples = getMaxSamples(stack);

        Component sampleComponent = Component.translatable("dna.wildaside.sample_progress")
                .append(Component.literal(": " + progress + "/" + maxSamples))
                .withStyle(progress >= maxSamples ? ChatFormatting.GREEN : ChatFormatting.GRAY);
        tooltip.add(sampleComponent);

        if (!tag.contains(DNA_DATA)) {
            tooltip.add(Component.translatable("dna.wildaside.no_dna_data")
                    .withStyle(ChatFormatting.DARK_GRAY));
            return;
        }

        DnaImplementation dna = new DnaImplementation();
        dna.deserializeNBT(tag.getCompound(DNA_DATA));

        boolean revealSource = tag.getBoolean(REVEAL_SOURCE);
        boolean revealStability = tag.getBoolean(REVEAL_STABILITY);
        boolean revealTraits = tag.getBoolean(REVEAL_TRAITS);

        if (!(revealSource || revealStability || revealTraits)) {
            tooltip.add(Component.translatable("dna.wildaside.dna_data_hidden")
                    .withStyle(ChatFormatting.STRIKETHROUGH, ChatFormatting.DARK_GRAY));
            return;
        }

        if (revealSource) {
            Component sourceName = dna.source() != null
                    ? dna.source().getDescription()
                    : Component.translatable("dna.wildaside.unknown");
            tooltip.add(Component.translatable("dna.wildaside.source")
                    .append(": " + sourceName.getString())
                    .withStyle(ChatFormatting.AQUA));
        }

        if (revealStability) {
            tooltip.add(Component.translatable("dna.wildaside.stability")
                    .append(": " + String.format("%.2f", dna.stability()))
                    .withStyle(ChatFormatting.GREEN));
        }

        if (revealTraits) {
            displayGenesSection(tooltip, dna, TraitTypes.CORE, getTranslatable(CORE_TRAITS));
            displayGenesSection(tooltip, dna, TraitTypes.RESISTANCE, getTranslatable(RESISTANCES));
            displayGenesSection(tooltip, dna, TraitTypes.ABILITY, getTranslatable(ABILITIES));
        }
    }

    private void displayGenesSection(List<Component> tooltip, DnaImplementation dna, TraitTypes type, MutableComponent title) {
        Map<Trait, Gene> filtered = dna.genes().entrySet().stream()
                .filter(e -> e.getKey().traitType() == type)
                .sorted(Map.Entry.comparingByKey(Comparator.comparing(Trait::name)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));

        if (filtered.isEmpty()) return;

        tooltip.add(title.withStyle(type.headerColour));

        filtered.values().forEach(gene -> {
            float value = gene.value();
            String valueStr = String.format("%.2f", value);

            if (type == TraitTypes.ABILITY) {
                value /= 20;
                valueStr = String.format("%.2f", value) + "s";
            } else if (type == TraitTypes.RESISTANCE) {
                value *= 100;
                valueStr = String.format("%.2f", value) + "%";
            }

            tooltip.add(Component.literal("- ")
                    .append(Component.translatable("trait.wildaside." + gene.trait().name()))
                    .append(": " + valueStr)
                    .withStyle(type.entryColour));
        });
    }

    private int getSampleProgress(ItemStack stack) {
        return stack.getOrCreateTag().getInt(SAMPLE_PROGRESS);
    }

    private void setSampleProgress(ItemStack stack, int progress) {
        stack.getOrCreateTag().putInt(MAX_SAMPLES, progress);
    }

    private int getMaxSamples(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains(MAX_SAMPLES))
            tag.putInt(MAX_SAMPLES, DEFAULT_MAX_SAMPLES);
        return tag.getInt(MAX_SAMPLES);
    }

    public static MutableComponent getTranslatable(String string) {
        return Component.translatable("dna.wildaside." + string);
    }
}
