package net.farkas.wildaside.item.custom;

import net.farkas.wildaside.dna.trait.Trait;
import net.farkas.wildaside.dna.trait.Traits;
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
        String traitName = tag.getString("trait");
        float value = tag.getFloat("value");

        Trait trait = Traits.getByName(traitName);
        if (trait != null) {
            tooltip.add(Component.translatable("trait.wildaside." + traitName)
                    .append(Component.literal(": " + String.format("%.2f", value)))
                    .withStyle(trait.getTraitType().getHeaderColour()));
        }
    }

    @Override
    public void onInventoryTick(ItemStack stack, Level level, Player player, int slotIndex, int selectedIndex) {
        player.getInventory().removeItem(stack);
    }
}
