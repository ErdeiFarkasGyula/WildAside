package net.farkas.wildaside.item.custom;

import net.farkas.wildaside.capability.dna.DnaImplementation;
import net.farkas.wildaside.dna.DnaUtils;
import net.farkas.wildaside.dna.Gene;
import net.farkas.wildaside.dna.allele.value.AlleleValue;
import net.farkas.wildaside.dna.allele.value.EnumAlleleValue;
import net.farkas.wildaside.dna.allele.value.FloatAlleleValue;
import net.farkas.wildaside.dna.locus.GeneLocus;
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

    public static final int DEFAULT_MAX_SAMPLES = 1;

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
            tooltip.add(Component.translatable("dna.wildaside.stress")
                    .append(": " + String.format("%.2f", dna.getStress()))
                    .withStyle(ChatFormatting.GREEN));
        }

        Map<Trait, List<GeneLocus>> loci = dna.getLoci();
        if (revealTraits) {
            displayGenesSection(tooltip, loci, TraitType.CORE, getTranslatable(CORE_TRAITS));
            displayGenesSection(tooltip, loci, TraitType.RESISTANCE, getTranslatable(RESISTANCES));
            displayGenesSection(tooltip, loci, TraitType.ABILITY, getTranslatable(ABILITIES));
            displayGenesSection(tooltip, loci, TraitType.APPEARANCE, getTranslatable(APPEARANCE));
        }
    }

    private void displayGenesSection(List<Component> tooltip, Map<Trait, List<GeneLocus>> loci, TraitType type, MutableComponent title) {
        var filtered = loci.entrySet().stream()
                .filter(e -> e.getKey().getTraitType() == type)
                .sorted(Map.Entry.comparingByKey(Comparator.comparing(Trait::getName)))
                .toList();

        if (filtered.isEmpty()) return;

        tooltip.add(title.withStyle(type.getHeaderColour()));

        for (var entry : filtered) {
            Gene gene = DnaUtils.asGene(entry.getKey(), entry.getValue());
            if (gene == null) continue;
            AlleleValue valueHolder = gene.getExpressedValueHolder();
            String valueStr = valueHolder.format().getString();

            if (valueHolder instanceof FloatAlleleValue floatAlleleValue) {
                if (floatAlleleValue.get() == 0f) {
                    continue;
                }
            }

            if (type == TraitType.ABILITY && valueHolder instanceof FloatAlleleValue floatAlleleValue) {
                float value = floatAlleleValue.get() / 20;
                valueStr = String.format("%.2f", value) + "s";
            } else if (type == TraitType.RESISTANCE && valueHolder instanceof FloatAlleleValue floatAlleleValue) {
                float value = floatAlleleValue.get() * 100;
                valueStr = String.format("%.2f", value) + "%";
            } else if (type == TraitType.APPEARANCE) {
                if (valueHolder instanceof EnumAlleleValue<?> enumAlleleValue) {
                    valueStr = enumAlleleValue.get().toString();
                }

                if (valueStr.startsWith("0")) {
                    continue;
                }
            }

            tooltip.add(Component.literal("- ")
                    .append(Component.translatable("trait.wildaside." + gene.getTrait().getName()))
                    .append(": " + valueStr)
                    .withStyle(type.getEntryColour()));
        }
    }
    
    public static MutableComponent getTranslatable(String string) {
        return Component.translatable("dna.wildaside." + string);
    }
}
