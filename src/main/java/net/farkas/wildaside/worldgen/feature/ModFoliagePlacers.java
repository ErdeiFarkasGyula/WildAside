package net.farkas.wildaside.worldgen.feature;

import com.mojang.serialization.MapCodec;
import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.worldgen.feature.tree.hickory.HickoryTreeFoliagePlacer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModFoliagePlacers {
    public static final DeferredRegister<FoliagePlacerType<?>> FOLIAGE_PLACERS =
            DeferredRegister.create(Registries.FOLIAGE_PLACER_TYPE, WildAside.MOD_ID);

    public static final RegistryObject<FoliagePlacerType<HickoryTreeFoliagePlacer>> HICKORY_FOLIAGE_PLACER =
            FOLIAGE_PLACERS.register("hickory_foliage_placer", () -> new FoliagePlacerType<>(MapCodec.assumeMapUnsafe(HickoryTreeFoliagePlacer.CODEC)));

    public static void register(IEventBus eventBus) {
        FOLIAGE_PLACERS.register(eventBus);
    }
}