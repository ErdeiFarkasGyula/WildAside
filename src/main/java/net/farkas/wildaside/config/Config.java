package net.farkas.wildaside.config;

import net.farkas.wildaside.WildAside;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = WildAside.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();

    //COMMON
    public static final ForgeConfigSpec.ConfigValue<Integer> HICKORY_FOREST_WEIGHT = COMMON_BUILDER
            .push("Biome weights")
            .comment("Spawn weight of the Hickory Forest biome (def: 3)")
            .define("hickory_forest_weight", 3);
    public static final ForgeConfigSpec.ConfigValue<Integer> GLOWING_HICKORY_FOREST_WEIGHT = COMMON_BUILDER
            .comment("Spawn weight of the Glowing Hickory Forest biome (def: 1)")
            .define("glowing_hickory_forest_weight", 1);
    public static final ForgeConfigSpec.ConfigValue<Integer> VIBRION_HIVE_WEIGHT = COMMON_BUILDER
            .comment("Spawn weight of the Vibrion Hive biome (def: 4)")
            .define("vibrion_hive_weight", 4);

    public static final ForgeConfigSpec.ConfigValue<Integer> HICKORY_COLOUR_NOISE_SEED = COMMON_BUILDER
            .pop()
            .push("Hickory colour noise")
            .comment("Seed of the noise used to generate Hickory colour placements (def: 20080424)")
            .define("hickory_colour_noise_seed", 20080424);
    public static final ForgeConfigSpec.ConfigValue<Double> HICKORY_COLOUR_NOISE_SCALE = COMMON_BUILDER
            .comment("Scale of the noise used to generate Hickory colour placements (higher -> smaller patches) (def: 0.01)")
            .define("hickory_colour_noise_scale", 0.010);

//    public static final ForgeConfigSpec.ConfigValue<Integer> MUCELLITH_SPAWN_WEIGHT = BUILDER
//            .pop()
//            .push("Mucellith mob")
//            .comment("Spawn weight of the Mucellith mob in the Vibrion Hive biome")
//            .define("mucellith_spawn_weight", 15);
//    public static final ForgeConfigSpec.ConfigValue<Integer> MUCELLITH_SPAWN_MIN = BUILDER
//            .comment("Minimum number of Mucellith mobs in a group when spawning in the Vibrion Hive biome")
//            .define("mucellith_spawn_min", 2);
//    public static final ForgeConfigSpec.ConfigValue<Integer> MUCELLITH_SPAWN_MAX = BUILDER
//            .comment("Maximum number of Mucellith mobs in a group when spawning in the Vibrion Hive biome")
//            .define("mucellith_spawn_max", 3);

    public static final ForgeConfigSpec COMMON_SPEC = COMMON_BUILDER.build();
}
