package net.farkas.wildaside.worldgen.biome;

import net.farkas.wildaside.WildAside;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.registries.ForgeRegistries;

public class ModBiomeModifiers {
//    public static final ResourceKey<BiomeModifier> ADD_ENTORIUM_ORE = registerKey("add_sapphire_ore");
//    public static final ResourceKey<BiomeModifier> ADD_SPOTTED_WINTERGREEN = registerKey("add_spotted_wintergreen");
//    public static final ResourceKey<BiomeModifier> ADD_PINKSTER_FLOWER = registerKey("add_pinkster_flower");

    public static void bootstrap(BootstapContext<BiomeModifier> context) {
//        var placedFeatures = context.lookup(Registries.PLACED_FEATURE);
//        var biomes = context.lookup(Registries.BIOME);

//        context.register(ADD_ENTORIUM_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
//                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
//                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.ENTORIUM_ORE_PLACED_KEY)),
//                GenerationStep.Decoration.UNDERGROUND_ORES));

//        context.register(ADD_SPOTTED_WINTERGREEN, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
//                biomes.getOrThrow(Tags.Biomes.IS_PLAINS),
//                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.SPOTTED_WINTERGREEN_PLACED_KEY)),
//                GenerationStep.Decoration.VEGETAL_DECORATION));

    }

    private static ResourceKey<BiomeModifier> registerKey(String name) {
        return ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, new ResourceLocation(WildAside.MOD_ID, name));
    }
}