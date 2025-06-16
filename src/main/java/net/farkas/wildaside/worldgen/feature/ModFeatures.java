package net.farkas.wildaside.worldgen.feature;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.worldgen.feature.custom.*;
import net.minecraft.core.Direction;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.HugeMushroomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ReplaceBlockConfiguration;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, WildAside.MOD_ID);

    public static final RegistryObject<UpdateReplaceSingleBlockFeature> TICK_REPLACE_SINGLE_BLOCK = FEATURES.register("tick_replace_single_block",
            () -> new UpdateReplaceSingleBlockFeature(ReplaceBlockConfiguration.CODEC));
    public static final RegistryObject<NaturalSporeBlasterFeature> NATURAL_SPORE_BLASTER_FEATURE = FEATURES.register("natural_spore_blaster_feature",
            () -> new NaturalSporeBlasterFeature(ReplaceBlockConfiguration.CODEC));

    public static final RegistryObject<RedlikeSubstiliumMushroomFeature> REDLIKE_SUBSTILIUM_MUSHROOM = FEATURES.register("redlike_substilium_mushroom",
            () -> new RedlikeSubstiliumMushroomFeature(HugeMushroomFeatureConfiguration.CODEC));
    public static final RegistryObject<BrownlikeSubstiliumMushroomFeature> BROWNLIKE_SUBSTILIUM_MUSHROOM = FEATURES.register("brownlike_substilium_mushroom",
            () -> new BrownlikeSubstiliumMushroomFeature(HugeMushroomFeatureConfiguration.CODEC));

    public static final RegistryObject<HickoryBushFeature> HICKORY_BUSH = FEATURES.register("hickory_bush",
            () -> new HickoryBushFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<GlowingHickoryBushFeature> GLOWING_HICKORY_BUSH = FEATURES.register("glowing_hickory_bush",
            () -> new GlowingHickoryBushFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<FallenHickoryTreeFeature> FALLEN_HICKORY_TREE = FEATURES.register("fallen_hickory_tree",
            () -> new FallenHickoryTreeFeature(NoneFeatureConfiguration.CODEC));

    public static void register(IEventBus eventBus) {
        FEATURES.register(eventBus);
    }
}