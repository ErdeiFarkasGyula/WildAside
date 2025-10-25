package net.farkas.wildaside.datagen;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.item.ModItems;
import net.farkas.wildaside.worldgen.biome.ModBiomeModifiers;
import net.farkas.wildaside.worldgen.ModConfiguredFeatures;
import net.farkas.wildaside.worldgen.ModPlacedFeatures;
import net.farkas.wildaside.worldgen.biome.ModBiomes;
import net.farkas.wildaside.worldgen.dimension.ModDimensions;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.registries.ForgeRegistries;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ModWorldGenProvider extends DatapackBuiltinEntriesProvider {
    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.DIMENSION_TYPE, ModDimensions::bootstrapType)
            .add(Registries.CONFIGURED_FEATURE, ModConfiguredFeatures::bootstrap)
            .add(Registries.PLACED_FEATURE, ModPlacedFeatures::bootstrap)
            .add(Registries.BIOME, ModBiomes::bootstrap)
            .add(Registries.LEVEL_STEM, ModDimensions::bootstrapStem)
            .add(ForgeRegistries.Keys.BIOME_MODIFIERS, ModBiomeModifiers::bootstrap);

    public ModWorldGenProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(WildAside.MOD_ID));
    }
}