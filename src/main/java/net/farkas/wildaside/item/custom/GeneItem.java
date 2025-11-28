package net.farkas.wildaside.item.custom;

import net.farkas.wildaside.dna.DnaUtils;
import net.farkas.wildaside.dna.Gene;
import net.farkas.wildaside.dna.trait.Trait;
import net.farkas.wildaside.dna.trait.Traits;
import net.farkas.wildaside.item.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static net.farkas.wildaside.dna.DnaConstants.*;

public class GeneItem extends Item {
    public GeneItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltip, isAdvanced);

        CompoundTag tag = stack.getOrCreateTag();
        Gene gene = Gene.deserializeNBT(tag);
        Trait trait = gene.getTrait();

        if (trait != null) {
            tooltip.add(Component.translatable("trait.wildaside." + trait.getName())
                    .append(Component.literal(": " + DnaUtils.getFormattedString(gene.getExpressedValue())))
                    .withStyle(trait.getTraitType().getHeaderColour())
                    .append(Component.translatable("dna.wildaside.allele"))
                    .append(Component.translatable("dna.wildaside.alleleA"))
                    .withStyle(trait.getTraitType().getHeaderColour())
                    .append(DnaUtils.getFormattedString(gene.getAlleleA().getValue()))
                    .append(Component.translatable("dna.wildaside.alleleB"))
                    .withStyle(trait.getTraitType().getHeaderColour())
                    .append(DnaUtils.getFormattedString(gene.getAlleleB().getValue())));
        }
    }

    @Override
    public void onInventoryTick(ItemStack stack, Level level, Player player, int slotIndex, int selectedIndex) {
        player.getInventory().removeItem(stack);
    }

    public static ItemStack createFromTag(Gene gene) {
        ItemStack stack = new ItemStack(ModItems.GENE.get());
        CompoundTag tag = gene.serializeNBT();
        stack.setTag(tag);
        return stack;
    }
}
