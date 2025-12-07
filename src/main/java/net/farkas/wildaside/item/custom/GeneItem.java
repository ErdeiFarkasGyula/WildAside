package net.farkas.wildaside.item.custom;

import net.farkas.wildaside.capability.bioengineering.BioengineeringSkillsCapability;
import net.farkas.wildaside.dna.DnaUtils;
import net.farkas.wildaside.dna.Gene;
import net.farkas.wildaside.dna.allele.Allele;
import net.farkas.wildaside.dna.bioengineering_skill.BioengineeringSkillRegistry;
import net.farkas.wildaside.dna.trait.Trait;
import net.farkas.wildaside.item.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
            Allele alleleA = gene.getAlleleA();
            Allele alleleB = gene.getAlleleB();

            tooltip.add(Component.translatable("trait.wildaside." + trait.getName()).withStyle(trait.getTraitType().getHeaderColour()));
            tooltip.add(Component.literal("- " + DnaUtils.getFormattedString(gene.getExpressedValue())).withStyle(ChatFormatting.GREEN));

            Minecraft mc = Minecraft.getInstance();
            LocalPlayer player = mc.player;

            player.getCapability(BioengineeringSkillsCapability.INSTANCE).ifPresent(cap -> {
                if (cap.hasSkill(BioengineeringSkillRegistry.REVEAL_ALLELES.getId())) {
                    tooltip.add(Component.empty());

                    tooltip.add(Component.translatable("dna.wildaside.alleleA").withStyle(ChatFormatting.AQUA));
                    tooltip.add(Component.literal("- ").append(alleleA.getDominance().getComponent()));
                    tooltip.add(Component.literal("- " + DnaUtils.getFormattedString(alleleA.getValue())));

                    tooltip.add(Component.translatable("dna.wildaside.alleleB").withStyle(ChatFormatting.AQUA));
                    tooltip.add(Component.literal("- ").append(alleleB.getDominance().getComponent()));
                    tooltip.add(Component.literal("- " + DnaUtils.getFormattedString(alleleB.getValue())));
                }
            });
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
