package net.farkas.wildaside.item.custom;

import net.farkas.wildaside.capability.dna.DnaImplementation;
import net.farkas.wildaside.dna.Gene;
import net.farkas.wildaside.dna.traits.Trait;
import net.farkas.wildaside.dna.traits.TraitTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GeneItem extends Item {
    public GeneItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltip, isAdvanced);

        CompoundTag tag = stack.getOrCreateTag();

    }

    private void displayGenesSection(List<Component> tooltip, DnaImplementation dna, TraitTypes type, String title, ChatFormatting headerColor) {
        Map<Trait, Gene> filtered = dna.genes().entrySet().stream()
                .filter(e -> e.getKey().traitType() == type)
                .sorted(Map.Entry.comparingByKey(Comparator.comparing(Trait::name)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));

        if (filtered.isEmpty()) return;

        tooltip.add(Component.literal(title).withStyle(headerColor));
        filtered.values().forEach(gene -> {
            float value = gene.value();
            String valueStr = String.format("%.2f", value);

            if (type == TraitTypes.ABILITY) {
                value /= 20;
                valueStr = String.format("%.2f", value) + " s";
            } else if (type == TraitTypes.RESISTANCE) {
                value *= 100;
                valueStr = String.format("%.2f", value) + " %";
            }

            ChatFormatting color = (type == TraitTypes.ABILITY) ? ChatFormatting.LIGHT_PURPLE : ChatFormatting.WHITE;
            tooltip.add(Component.literal("- " + gene.trait().name() + ": " + valueStr).withStyle(color));
        });
    }
}
