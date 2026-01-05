package net.farkas.wildaside.config;

import net.farkas.wildaside.WildAside;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = WildAside.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModConfig {
    private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();

    private static final String currentVersion = ModList.get().getModContainerById("wildaside")
            .map(mod -> mod.getModInfo().getVersion().toString())
            .orElse("unknown");

    //COMMON
    public static final ForgeConfigSpec.ConfigValue<String> CONFIG_VERSION = COMMON_BUILDER
            .push("Config version")
            .comment("Must match internal mod version for compatibility.")
            .define("config_version", currentVersion);

    public static final ForgeConfigSpec.ConfigValue<Integer> HICKORY_FOREST_WEIGHT = COMMON_BUILDER
            .pop()
            .push("Biome weights")
            .comment("Spawn weight of the Hickory Forest biome. (def: 3)")
            .define("hickory_forest_weight", 3);
    public static final ForgeConfigSpec.ConfigValue<Integer> GLOWING_HICKORY_FOREST_WEIGHT = COMMON_BUILDER
            .comment("Spawn weight of the Glowing Hickory Forest biome. (def: 1)")
            .define("glowing_hickory_forest_weight", 1);
    public static final ForgeConfigSpec.ConfigValue<Integer> VIBRION_HIVE_WEIGHT = COMMON_BUILDER
            .comment("Spawn weight of the Vibrion Hive biome. (def: 2)")
            .define("vibrion_hive_weight", 2);

    public static final ForgeConfigSpec.ConfigValue<Integer> HICKORY_COLOUR_NOISE_SEED = COMMON_BUILDER
            .pop()
            .push("Hickory colour noise")
            .comment("Seed of the noise used to generate Hickory colour placements. (def: 20080424)")
            .define("hickory_colour_noise_seed", 20080424);
    public static final ForgeConfigSpec.ConfigValue<Double> HICKORY_COLOUR_NOISE_SCALE = COMMON_BUILDER
            .comment("Scale of the noise used to generate Hickory colour placements. (higher -> smaller patches) (def: 0.01)")
            .define("hickory_colour_noise_scale", 0.010);

    public static final ForgeConfigSpec.BooleanValue GLOWING_HICKORY_TICK = COMMON_BUILDER
            .pop()
            .push("Enable/disable features")
            .comment("Enable/disable the glowing tick of glowing blocks in the Glowing Hickory Forest, making them not change their light levels anymore.  (def: true)")
            .define("glowing_hickory_tick", true);

    public static final ForgeConfigSpec.BooleanValue ACCURATE_DNA_MOVEMENT_SPEEDS = COMMON_BUILDER
            .comment("Enable/disable the accurate movement speed calculations of mobs for DNAs. " +
                    "If false, both ground and water movement speeds can be inaccurate with DNAs, " +
                    "but performance after loading a world might be better for the first few seconds " +
                    "when loading it up for the first time, or with a different mod list. (def: true)")
            .define("accurate_dna_movement_speeds", true);

    public static final ForgeConfigSpec.BooleanValue ACCURATE_DNA_WATER_MOVEMENT_SPEEDS = COMMON_BUILDER
            .comment("[EXPERIMENTAL!] Enable/disable the accurate movement speed calculations of mobs for DNAs, only in water. " +
                    "If false, water movement speeds can be inaccurate with DNAs," +
                    "but performance after loading a world might be better for the first few seconds " +
                    "when loading it up for the first time, or with a different mod list. (def: false)")
            .define("accurate_dna_water_movement_speeds", false);

    public static final ForgeConfigSpec.BooleanValue WILD_MODE = COMMON_BUILDER
            .comment("Enable/disable Wild Mode. This means all mobs will spawn with their varied, but deterministic dna generated and applied. (def: false)")
            .define("wild_mode", false);

    public static final ForgeConfigSpec.BooleanValue EXCLUDE_PLAYERS_FROM_WILD_MODE = COMMON_BUILDER
            .comment("Exclude/include player from/in Wild Mode. (def: true)")
            .define("exclude_players_from_wild_mode", true);


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

    //CLIENT
    private static final ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.BooleanValue SHOW_UPDATE_NOTIFICATION = CLIENT_BUILDER
            .push("Enable/disable features")
            .comment("Enable/disable the showing of a chat notification when a new version is available. (def: true)")
            .define("show_update_notification", true);

    public static void setShowUpdates(boolean value) {
        SHOW_UPDATE_NOTIFICATION.set(value);
        SHOW_UPDATE_NOTIFICATION.save();
    }

    public static final ForgeConfigSpec COMMON_SPEC = COMMON_BUILDER.build();
    public static final ForgeConfigSpec CLIENT_SPEC = CLIENT_BUILDER.build();
}
