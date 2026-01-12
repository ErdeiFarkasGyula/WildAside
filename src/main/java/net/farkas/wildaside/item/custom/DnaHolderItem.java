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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static net.farkas.wildaside.dna.DnaConstants.*;

public class DnaHolderItem extends AbstractDnaSampleItem {
    public static final int MAX_COOLDOWN = 60;
    public static final int DEFAULT_MAX_SAMPLES = 1;

    public DnaHolderItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().getInt(SAMPLE_PROGRESS) > 0) return 1;
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

        if (appendContaminationTooltipIfNeeded(tooltip, tag, level)) return;

        if (!(revealSource || revealStability || revealTraits)) {
            tooltip.add(Component.translatable("dna.wildaside.dna_data_hidden").withStyle(ChatFormatting.STRIKETHROUGH, ChatFormatting.DARK_GRAY));
            return;
        }

        DnaImplementation dna = new DnaImplementation();
        dna.deserializeNBT(tag.getCompound(DNA_DATA));

        if (revealSource) {
            Component sourceName = dna.getSource() != null ? dna.getSource().getDescription() : Component.translatable("dna.wildaside.unknown");
            tooltip.add(Component.translatable("dna.wildaside.source")
                    .append(": " + sourceName.getString())
                    .withStyle(ChatFormatting.AQUA));
        }

        if (revealStability) {
            tooltip.add(Component.translatable("dna.wildaside.stress")
                    .append(": " + String.format("%.2f", dna.getStress()))
                    .withStyle(ChatFormatting.GREEN));
        }

        if (revealTraits) {
            displayGenesFromGenome(tooltip, dna);
        }
    }

    private void displayGenesFromGenome(List<Component> tooltip, DnaImplementation dna) {
        if (dna.getGenome() == null) {
            Map<Trait, List<GeneLocus>> loci = dna.getGenomeLociView();
            displayGenesSection(tooltip, loci, TraitType.CORE, getTranslatable(CORE_TRAITS));
            displayGenesSection(tooltip, loci, TraitType.RESISTANCE, getTranslatable(RESISTANCES));
            displayGenesSection(tooltip, loci, TraitType.ABILITY, getTranslatable(ABILITIES));
            displayGenesSection(tooltip, loci, TraitType.APPEARANCE, getTranslatable(APPEARANCE));
            return;
        }

        net.farkas.wildaside.dna.expression.ExpressionContext context = new net.farkas.wildaside.dna.expression.ExpressionContext(null);
        
        displayGenomeSection(tooltip, dna, TraitType.CORE, getTranslatable(CORE_TRAITS), context);
        displayGenomeSection(tooltip, dna, TraitType.RESISTANCE, getTranslatable(RESISTANCES), context);
        displayGenomeSection(tooltip, dna, TraitType.ABILITY, getTranslatable(ABILITIES), context);
        displayGenomeSection(tooltip, dna, TraitType.APPEARANCE, getTranslatable(APPEARANCE), context);
    }

    private void displayGenomeSection(List<Component> tooltip, DnaImplementation dna, TraitType type, MutableComponent title, net.farkas.wildaside.dna.expression.ExpressionContext context) {
        var filtered = net.farkas.wildaside.dna.trait.TraitRegistry.getAllTraits().stream()
                .filter(trait -> trait.getTraitType() == type)
                .filter(trait -> {
                    float value = dna.getGenome().getExpressedValue(trait, context);
                    return Math.abs(value) > 0.001f;
                })
                .sorted(Comparator.comparing(Trait::getName))
                .toList();

        if (filtered.isEmpty()) return;

        tooltip.add(title.withStyle(type.getHeaderColour()));

        for (Trait trait : filtered) {
            float value = dna.getGenome().getExpressedValue(trait, context);
            String valueStr = String.format("%.2f", value);

            if (type == TraitType.ABILITY) {
                valueStr = String.format("%.2f", value / 20f) + "s";
            }
            else if (type == TraitType.RESISTANCE) {
                valueStr = String.format("%.2f", value * 100f) + "%";
            }

            tooltip.add(Component.literal("- ")
                    .append(Component.translatable("trait.wildaside." + trait.getName()))
                    .append(": " + valueStr)
                    .withStyle(type.getEntryColour()));
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

            if (valueHolder instanceof FloatAlleleValue floatAlleleValue && floatAlleleValue.get() == 0f) continue;

            if (type == TraitType.ABILITY && valueHolder instanceof FloatAlleleValue fa) {
                valueStr = String.format("%.2f", fa.get() / 20f) + "s";
            }
            else if (type == TraitType.RESISTANCE && valueHolder instanceof FloatAlleleValue fa) {
                valueStr = String.format("%.2f", fa.get() * 100f) + "%";
            }
            else if (type == TraitType.APPEARANCE) {
                if (valueHolder instanceof EnumAlleleValue<?> enumAlleleValue) {
                    valueStr = enumAlleleValue.get().toString();
                }
                if (valueStr.startsWith("0")) continue;
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

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (hand != InteractionHand.MAIN_HAND) return InteractionResultHolder.pass(stack);
        if (level.isClientSide()) return InteractionResultHolder.success(stack);
        if (!hasDna(stack)) return InteractionResultHolder.pass(stack);

        CompoundTag tag = stack.getOrCreateTag();

        tag.remove(DNA_DATA);
        tag.putInt(SAMPLE_PROGRESS, 0);
        tag.putBoolean(SAMPLE_UNUSABLE, false);
        tag.putBoolean(MULTIPLE_SOURCES, false);
        tag.putBoolean(SAMPLE_CLOTTED, false);
        tag.putBoolean(SAMPLE_DIRTY, false);
        tag.putBoolean(REVEAL_SOURCE, false);
        tag.putBoolean(REVEAL_STABILITY, false);
        tag.putBoolean(REVEAL_TRAITS, false);

        tag.putLong(BLOOD_CLOTTING_TIME, BLOOD_CLOTTING_TIME_DEFAULT);
        tag.remove(BLOOD_FREEZER_TICKS);
        tag.remove(BLOOD_CREATION_TICK);
        tag.putFloat(DIRTINESS, 0f);

        level.playSound(null, player.blockPosition(), SoundEvents.BOTTLE_EMPTY, SoundSource.PLAYERS, 0.5f, 0.4f);

        return InteractionResultHolder.success(stack);
    }
}