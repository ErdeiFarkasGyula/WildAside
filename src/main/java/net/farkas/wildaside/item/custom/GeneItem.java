package net.farkas.wildaside.item.custom;

import net.farkas.wildaside.capability.bioengineering_skill.BioengineeringSkillsCapability;
import net.farkas.wildaside.dna.Gene;
import net.farkas.wildaside.dna.bioengineering_skill.BioengineeringSkillRegistry;
import net.farkas.wildaside.dna.expression.ExpressionContext;
import net.farkas.wildaside.dna.sequence.GeneSequence;
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
            GeneSequence maternal = gene.getMaternalSequence();
            GeneSequence paternal = gene.getPaternalSequence();

            tooltip.add(Component.translatable("trait.wildaside." + trait.getName()).withStyle(trait.getTraitType().getHeaderColour()));
            
            ExpressionContext context = new ExpressionContext(null);
            float expressedValue = gene.getExpressedValue(context);
            String expressedValueString = String.format("%.2f", expressedValue);

            tooltip.add(Component.literal("- " + expressedValueString).withStyle(ChatFormatting.GREEN));

            Minecraft mc = Minecraft.getInstance();
            LocalPlayer player = mc.player;

            player.getCapability(BioengineeringSkillsCapability.INSTANCE).ifPresent(cap -> {
                if (cap.hasSkill(BioengineeringSkillRegistry.REVEAL_SEQUENCES.getId())) {
                    tooltip.add(Component.empty());

                    tooltip.add(Component.translatable("dna.wildaside.maternal").withStyle(ChatFormatting.AQUA));
                    if (maternal != null) {
                        tooltip.add(Component.literal("- ").append(Component.literal(maternal.getDominance().name())));
                        tooltip.add(Component.literal("- " + String.format("%.2f", maternal.calculateBaseValue())));
                    } else {
                        tooltip.add(Component.literal("- Missing").withStyle(ChatFormatting.GRAY));
                    }

                    tooltip.add(Component.translatable("dna.wildaside.paternal").withStyle(ChatFormatting.AQUA));
                    if (paternal != null) {
                        tooltip.add(Component.literal("- ").append(Component.literal(paternal.getDominance().name())));
                        tooltip.add(Component.literal("- " + String.format("%.2f", paternal.calculateBaseValue())));
                    } else {
                        tooltip.add(Component.literal("- Missing").withStyle(ChatFormatting.GRAY));
                    }
                }
            });

            if (tag.getBoolean("latent")) {
                tooltip.add(Component.empty());
                tooltip.add(Component.translatable("dna.wildaside.latent")
                        .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
                tooltip.add(Component.translatable("dna.wildaside.latent.description.1")
                        .withStyle(ChatFormatting.GRAY));
                tooltip.add(Component.translatable("dna.wildaside.latent.description.2")
                        .withStyle(ChatFormatting.GRAY));
            }
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

    public static boolean isFlipped(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean("flipped");
    }

    public static void setFlipped(ItemStack stack, boolean flipped) {
        stack.getOrCreateTag().putBoolean("flipped", flipped);
    }
}