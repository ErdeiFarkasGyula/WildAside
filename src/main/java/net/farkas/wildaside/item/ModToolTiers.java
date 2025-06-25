package net.farkas.wildaside.item;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.util.ModTags;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeTier;
import net.minecraftforge.common.TierSortingRegistry;

import java.util.List;

public class ModToolTiers {
    public static final Tier ENTORIUM = TierSortingRegistry.registerTier(
            new ForgeTier(2, 1250, 200f, 1f, 15, ModTags.Blocks.NEEDS_ENTORIUM_TOOL, () -> Ingredient.of(ModItems.ENTORIUM.get())),
            new ResourceLocation(WildAside.MOD_ID, "entorium"), List.of(Tiers.IRON), List.of(Tiers.DIAMOND));
}