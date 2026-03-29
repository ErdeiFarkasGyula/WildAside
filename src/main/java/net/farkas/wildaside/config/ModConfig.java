package net.farkas.wildaside.config;

import net.farkas.wildaside.WildAside;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = WildAside.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModConfig {
    private static final ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();

    private static final String currentVersion = ModList.get().getModContainerById("wildaside")
            .map(mod -> mod.getModInfo().getVersion().toString())
            .orElse("unknown");

    public static final ForgeConfigSpec.ConfigValue<String> CONFIG_VERSION;
    public static final ForgeConfigSpec.ConfigValue<Integer> HICKORY_FOREST_WEIGHT;
    public static final ForgeConfigSpec.ConfigValue<Integer> GLOWING_HICKORY_FOREST_WEIGHT;
    public static final ForgeConfigSpec.ConfigValue<Integer> VIBRION_HIVE_WEIGHT;
    public static final ForgeConfigSpec.ConfigValue<Integer> HICKORY_COLOUR_NOISE_SEED;
    public static final ForgeConfigSpec.ConfigValue<Double> HICKORY_COLOUR_NOISE_SCALE;
    public static final ForgeConfigSpec.BooleanValue GLOWING_HICKORY_TICK;
    public static final ForgeConfigSpec.BooleanValue ENABLE_DNA;
    public static final ForgeConfigSpec.BooleanValue ACCURATE_DNA_MOVEMENT_SPEEDS;
    public static final ForgeConfigSpec.BooleanValue ACCURATE_DNA_WATER_MOVEMENT_SPEEDS;
    public static final ForgeConfigSpec.BooleanValue WILD_MODE;
    public static final ForgeConfigSpec.BooleanValue EXCLUDE_PLAYERS_FROM_WILD_MODE;

    static {
        SERVER_BUILDER.push("Config version");
        CONFIG_VERSION = SERVER_BUILDER.comment("Must match internal mod version for compatibility.")
                .define("config_version", currentVersion);
        SERVER_BUILDER.pop();

        SERVER_BUILDER.push("Biome weights");
        HICKORY_FOREST_WEIGHT = SERVER_BUILDER.comment("Spawn weight of the Hickory Forest biome. (def: 3)")
                .define("hickory_forest_weight", 3);
        GLOWING_HICKORY_FOREST_WEIGHT = SERVER_BUILDER.comment("Spawn weight of the Glowing Hickory Forest biome. (def: 1)")
                .define("glowing_hickory_forest_weight", 1);
        VIBRION_HIVE_WEIGHT = SERVER_BUILDER.comment("Spawn weight of the Vibrion Hive biome. (def: 2)")
                .define("vibrion_hive_weight", 2);
        SERVER_BUILDER.pop();

        SERVER_BUILDER.push("Hickory colour noise");
        HICKORY_COLOUR_NOISE_SEED = SERVER_BUILDER.comment("Seed of the noise used to generate Hickory colour placements. (def: 20080424)")
                .define("hickory_colour_noise_seed", 20080424);
        HICKORY_COLOUR_NOISE_SCALE = SERVER_BUILDER.comment("Scale of the noise used to generate Hickory colour placements. (higher -> smaller patches) (def: 0.01)")
                .define("hickory_colour_noise_scale", 0.010);
        SERVER_BUILDER.pop();

        SERVER_BUILDER.push("Enable/disable features");
        GLOWING_HICKORY_TICK = SERVER_BUILDER.comment("Enable/disable the glowing tick of glowing blocks in the Glowing Hickory Forest, making them not change their light levels anymore. (def: true)")
                .define("glowing_hickory_tick", true);
        SERVER_BUILDER.pop();

        SERVER_BUILDER.push("Experimental features");
        ENABLE_DNA = SERVER_BUILDER.comment("Whether to enable dna related features.")
                .define("enable_dna", false);
        ACCURATE_DNA_MOVEMENT_SPEEDS = SERVER_BUILDER
                .comment("Enable/disable the accurate movement speed calculations of mobs for DNAs. " +
                        "If false, both ground and water movement speeds can be inaccurate with DNAs, " +
                        "but performance after loading a world might be better for the first few seconds " +
                        "when loading it up for the first time, or with a different mod list. (def: true)")
                .define("accurate_dna_movement_speeds", false);

        ACCURATE_DNA_WATER_MOVEMENT_SPEEDS = SERVER_BUILDER.comment("[EXPERIMENTAL!] Enable/disable the accurate movement speed calculations of mobs for DNAs, only in water. " +
                        "If false, water movement speeds can be inaccurate with DNAs," +
                        "but performance after loading a world might be better for the first few seconds " +
                        "when loading it up for the first time, or with a different mod list. (def: false)")
                .define("accurate_dna_water_movement_speeds", false);

        WILD_MODE = SERVER_BUILDER.comment("Enable/disable Wild Mode. This means all mobs will spawn with their varied, but deterministic dna generated and applied. (def: false)")
                .define("wild_mode", false);
        EXCLUDE_PLAYERS_FROM_WILD_MODE = SERVER_BUILDER.comment("Exclude/include player from/in Wild Mode. (def: true)")
                .define("exclude_players_from_wild_mode", true);
        SERVER_BUILDER.pop();
    }

    public static final ForgeConfigSpec.BooleanValue SHOW_UPDATE_NOTIFICATION;

    static {
        CLIENT_BUILDER.push("Enable/disable features");

        SHOW_UPDATE_NOTIFICATION = CLIENT_BUILDER.comment("Enable/disable the showing of a chat notification when a new version is available. (def: true)")
                .define("show_update_notification", true);

        CLIENT_BUILDER.pop();
    }

    public static final ForgeConfigSpec SERVER_SPEC = SERVER_BUILDER.build();
    public static final ForgeConfigSpec CLIENT_SPEC = CLIENT_BUILDER.build();

    public static void setShowUpdates(boolean value) {
        SHOW_UPDATE_NOTIFICATION.set(value);
        SHOW_UPDATE_NOTIFICATION.save();
    }

    public static String configVersion;
    public static int hickoryForestWeight;
    public static int glowingHickoryForestWeight;
    public static int vibrionHiveWeight;
    public static int hickoryColourNoiseSeed;
    public static double hickoryColourNoiseScale;
    public static boolean glowingHickoryTick;
    public static boolean enableDna;
    public static boolean accurateDnaMovementSpeeds;
    public static boolean accurateDnaWaterMovementSpeeds;
    public static boolean wildMode;
    public static boolean excludePlayersFromWildMode;
    public static boolean showUpdateNotification;

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent event) {
        if (event.getConfig().getSpec() == SERVER_SPEC) {
            configVersion = CONFIG_VERSION.get();
            hickoryForestWeight = HICKORY_FOREST_WEIGHT.get();
            glowingHickoryForestWeight = GLOWING_HICKORY_FOREST_WEIGHT.get();
            vibrionHiveWeight = VIBRION_HIVE_WEIGHT.get();
            hickoryColourNoiseSeed = HICKORY_COLOUR_NOISE_SEED.get();
            hickoryColourNoiseScale = HICKORY_COLOUR_NOISE_SCALE.get();
            glowingHickoryTick = GLOWING_HICKORY_TICK.get();
            enableDna = ENABLE_DNA.get();
            accurateDnaMovementSpeeds = ACCURATE_DNA_MOVEMENT_SPEEDS.get();
            accurateDnaWaterMovementSpeeds = ACCURATE_DNA_WATER_MOVEMENT_SPEEDS.get();
            wildMode = WILD_MODE.get();
            excludePlayersFromWildMode = EXCLUDE_PLAYERS_FROM_WILD_MODE.get();
        }

        if (event.getConfig().getSpec() == CLIENT_SPEC) {
            showUpdateNotification = SHOW_UPDATE_NOTIFICATION.get();
        }
    }
}