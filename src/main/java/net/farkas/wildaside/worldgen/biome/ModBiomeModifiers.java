package net.farkas.wildaside.worldgen.biome;

import net.farkas.wildaside.WildAside;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.registries.ForgeRegistries;

public class ModBiomeModifiers {
    public static final ResourceKey<BiomeModifier> ADD_MUCELLITH = registerKey("add_mucellith");

    public static void bootstrap(BootstrapContext<BiomeModifier> context) {
        var biomes = context.lookup(Registries.BIOME);

//        context.register(ADD_MUCELLITH, new ForgeBiomeModifiers.AddSpawnsBiomeModifier(
//                biomes.getOrThrow(Tags.Biomes.IS_PLAINS),
//                List.of(new MobSpawnSettings.SpawnerData(ModEntities.MUCELLITH.get(), 100, 1, 3))
//        ));

    }

    private static ResourceKey<BiomeModifier> registerKey(String name) {
        return ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, ResourceLocation.fromNamespaceAndPath(WildAside.MOD_ID, name));
    }
}