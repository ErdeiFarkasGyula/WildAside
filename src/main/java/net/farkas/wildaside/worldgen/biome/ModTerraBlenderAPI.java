package net.farkas.wildaside.worldgen.biome;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.worldgen.biome.region.ModGlowingHickoryForestRegion;
import net.farkas.wildaside.worldgen.biome.region.ModVibrionHiveRegion;
import net.farkas.wildaside.worldgen.biome.region.ModHickoryForestRegion;
import net.minecraft.resources.ResourceLocation;
import terrablender.api.Regions;

public class ModTerraBlenderAPI {
    public static void registerRegions() {
        Regions.register(new ModHickoryForestRegion(ResourceLocation.fromNamespaceAndPath(WildAside.MOD_ID, "hickory_forest"), 3));
        Regions.register(new ModGlowingHickoryForestRegion(ResourceLocation.fromNamespaceAndPath(WildAside.MOD_ID, "glowing_hickory_forest"), 1));
        Regions.register(new ModVibrionHiveRegion(ResourceLocation.fromNamespaceAndPath(WildAside.MOD_ID, "vibrion_hive"), 4));
    }
}