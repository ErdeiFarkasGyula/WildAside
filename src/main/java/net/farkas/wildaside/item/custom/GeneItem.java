package net.farkas.wildaside.item.custom;

import net.farkas.wildaside.capability.dna.DnaImplementation;
import net.farkas.wildaside.dna.Gene;
import net.farkas.wildaside.dna.traits.Trait;
import net.farkas.wildaside.dna.traits.TraitTypes;
import net.farkas.wildaside.dna.traits.Traits;
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
        tag.putString("gene_trait", "attack_damage");
        tag.putFloat("gene_value", 0.2f);
        String traitName = tag.getString("gene_trait");
        float value = tag.getFloat("gene_value");

        Trait trait = Traits.getByName(traitName);
        if (trait != null) {
            tooltip.add(Component.literal(traitName + ": " + value).withStyle(trait.traitType().headerColour));
        }
    }
}
