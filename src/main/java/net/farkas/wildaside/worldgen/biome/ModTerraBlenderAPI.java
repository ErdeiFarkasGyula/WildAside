package net.farkas.wildaside.worldgen.biome;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.config.ModConfig;
import net.farkas.wildaside.worldgen.biome.region.HickoryForestRegion;
import net.farkas.wildaside.worldgen.biome.region.GlowingHickoryForestRegion;
import net.farkas.wildaside.worldgen.biome.region.VibrionHiveRegion;
import net.minecraft.resources.ResourceLocation;
import terrablender.api.Regions;

public class ModTerraBlenderAPI {
    public static void registerRegions() {
        Regions.register(new HickoryForestRegion(new ResourceLocation(WildAside.MOD_ID, "hickory_forest"), ModConfig.hickoryForestWeight));
        Regions.register(new GlowingHickoryForestRegion(new ResourceLocation(WildAside.MOD_ID, "glowing_hickory_forest"), ModConfig.glowingHickoryForestWeight));
        Regions.register(new VibrionHiveRegion(new ResourceLocation(WildAside.MOD_ID, "vibrion_hive"), ModConfig.vibrionHiveWeight));
    }
}