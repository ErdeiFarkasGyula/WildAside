package net.farkas.wildaside.item.custom;

import net.farkas.wildaside.capability.dna.DnaImplementation;
import net.farkas.wildaside.dna.DnaUtils;
import net.farkas.wildaside.dna.Gene;
import net.farkas.wildaside.dna.allele.value.AlleleValue;
import net.farkas.wildaside.dna.allele.value.EnumAlleleValue;
import net.farkas.wildaside.dna.allele.value.FloatAlleleValue;
import net.farkas.wildaside.dna.trait.Trait;
import net.farkas.wildaside.dna.trait.TraitType;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

import static net.farkas.wildaside.dna.DnaConstants.*;

public class DnaHolderItem extends Item {
    public static final int MAX_COOLDOWN = 60;

    public static final int DEFAULT_MAX_SAMPLES = 3;

    public DnaHolderItem(Properties pProperties) {
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
            tooltip.add(Component.translatable("dna.wildaside.no_dna_data").withStyle(ChatFormatting.DARK_GRAY));
            return;
        }

        boolean revealSource = tag.getBoolean(REVEAL_SOURCE);
        boolean revealStability = tag.getBoolean(REVEAL_STABILITY);
        boolean revealTraits = tag.getBoolean(REVEAL_TRAITS);

        boolean multipleSources = tag.getBoolean(MULTIPLE_SOURCES);
        boolean clotted = DnaUtils.getFrozenItemEffectiveAge(tag, level) > tag.getLong(BLOOD_CLOTTING_TIME) || tag.getBoolean(SAMPLE_CLOTTED);
        boolean dirty = tag.getBoolean(SAMPLE_DIRTY);

        boolean unusable = DnaUtils.handleContaminatedSampleTooltip(tooltip, multipleSources, clotted, dirty);

        if (unusable) {
            tag.putBoolean(SAMPLE_UNUSABLE, true);
            return;
        }

        if (!(revealSource || revealStability || revealTraits)) {
            tooltip.add(Component.translatable("dna.wildaside.dna_data_hidden").withStyle(ChatFormatting.STRIKETHROUGH, ChatFormatting.DARK_GRAY));
            return;
        }

        DnaImplementation dna = new DnaImplementation();
        dna.deserializeNBT(tag.getCompound(DNA_DATA));

        if (revealSource) {
            Component sourceName = dna.getSource() != null
                    ? dna.getSource().getDescription()
                    : Component.translatable("dna.wildaside.unknown");
            tooltip.add(Component.translatable("dna.wildaside.source")
                    .append(": " + sourceName.getString())
                    .withStyle(ChatFormatting.AQUA));
        }

        if (revealStability) {
            tooltip.add(Component.translatable("dna.wildaside.stability")
                    .append(": " + String.format("%.2f", dna.getStability()))
                    .withStyle(ChatFormatting.GREEN));
        }

        if (revealTraits) {
            displayGenesSection(tooltip, dna, TraitType.CORE, getTranslatable(CORE_TRAITS));
            displayGenesSection(tooltip, dna, TraitType.RESISTANCE, getTranslatable(RESISTANCES));
            displayGenesSection(tooltip, dna, TraitType.ABILITY, getTranslatable(ABILITIES));
            displayGenesSection(tooltip, dna, TraitType.APPEARANCE, getTranslatable(APPEARANCE));
        }
    }

    private void displayGenesSection(List<Component> tooltip, DnaImplementation dna, TraitType type, MutableComponent title) {
        Map<Trait, Gene> filtered = dna.getGenes().entrySet().stream()
                .filter(e -> e.getKey().getTraitType() == type)
                .sorted(Map.Entry.comparingByKey(Comparator.comparing(Trait::getName)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));

        if (filtered.isEmpty()) return;

        tooltip.add(title.withStyle(type.getHeaderColour()));

        filtered.values().forEach(gene -> {
            AlleleValue valueHolder = gene.getExpressedValueHolder();
            String valueStr = valueHolder.format().getString();

            if (type == TraitType.ABILITY && valueHolder instanceof FloatAlleleValue floatAlleleValue) {
                float value = floatAlleleValue.get() / 20;
                valueStr = String.format("%.2f", value) + "s";
            } else if (type == TraitType.RESISTANCE && valueHolder instanceof FloatAlleleValue floatAlleleValue) {
                float value = floatAlleleValue.get() / 20;
                value *= 100;
                valueStr = String.format("%.2f", value) + "%";
            } else if (type == TraitType.APPEARANCE && valueHolder instanceof EnumAlleleValue<?> enumAlleleValue) {
                valueStr = enumAlleleValue.get().toString();
            }

            tooltip.add(Component.literal("- ")
                    .append(Component.translatable("trait.wildaside." + gene.getTrait().getName()))
                    .append(": " + valueStr)
                    .withStyle(type.getEntryColour()));
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
