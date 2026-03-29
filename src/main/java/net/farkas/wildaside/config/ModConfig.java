package net.farkas.wildaside.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.worldgen.biome.ModTerraBlenderAPI;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

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
                .define("config_version", "unknown");
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
                .comment("Enable/disable the accurate movement speed calculations of mobs for DNAs. (def: true)")
                .define("accurate_dna_movement_speeds", true);

        ACCURATE_DNA_WATER_MOVEMENT_SPEEDS = SERVER_BUILDER.comment("[EXPERIMENTAL!] Enable/disable the accurate movement speed calculations of mobs for DNAs, only in water. (def: false)")
                .define("accurate_dna_water_movement_speeds", false);

        WILD_MODE = SERVER_BUILDER.comment("Enable/disable Wild Mode. (def: false)")
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
            if (CONFIG_VERSION.get().equals("unknown")) {
                attemptMigration();
            }
            refreshServerFields();
            validateConfig();
            ModTerraBlenderAPI.registerRegions();
        }

        if (event.getConfig().getSpec() == CLIENT_SPEC) {
            showUpdateNotification = SHOW_UPDATE_NOTIFICATION.get();
        }
    }

    private static void attemptMigration() {
        Path oldConfigPath = FMLPaths.CONFIGDIR.get().resolve("wildaside-common.toml");

        if (Files.exists(oldConfigPath)) {
            WildAside.LOGGER.info("Legacy common config found. Migrating to global defaults and world config...");
            try (CommentedFileConfig oldConfig = CommentedFileConfig.builder(oldConfigPath).sync().build()) {
                oldConfig.load();

                copyIfPresent(oldConfig, "Biome weights.hickory_forest_weight", HICKORY_FOREST_WEIGHT);
                copyIfPresent(oldConfig, "Biome weights.glowing_hickory_forest_weight", GLOWING_HICKORY_FOREST_WEIGHT);
                copyIfPresent(oldConfig, "Biome weights.vibrion_hive_weight", VIBRION_HIVE_WEIGHT);
                copyIfPresent(oldConfig, "Hickory colour noise.hickory_colour_noise_seed", HICKORY_COLOUR_NOISE_SEED);
                copyIfPresent(oldConfig, "Hickory colour noise.hickory_colour_noise_scale", HICKORY_COLOUR_NOISE_SCALE);
                copyIfPresent(oldConfig, "Enable/disable features.glowing_hickory_tick", GLOWING_HICKORY_TICK);
                copyIfPresent(oldConfig, "Experimental features.enable_dna", ENABLE_DNA);
                copyIfPresent(oldConfig, "Experimental features.accurate_dna_movement_speeds", ACCURATE_DNA_MOVEMENT_SPEEDS);
                copyIfPresent(oldConfig, "Experimental features.accurate_dna_water_movement_speeds", ACCURATE_DNA_WATER_MOVEMENT_SPEEDS);
                copyIfPresent(oldConfig, "Experimental features.wild_mode", WILD_MODE);
                copyIfPresent(oldConfig, "Experimental features.exclude_players_from_wild_mode", EXCLUDE_PLAYERS_FROM_WILD_MODE);

                CONFIG_VERSION.set(currentVersion);
                SERVER_SPEC.save();

                Path defaultConfigsDir = FMLPaths.GAMEDIR.get().resolve("defaultconfigs");
                if (!Files.exists(defaultConfigsDir)) {
                    Files.createDirectories(defaultConfigsDir);
                }
                
                Path defaultServerConfigPath = defaultConfigsDir.resolve("wildaside-server.toml");

                oldConfig.set("Config version.config_version", currentVersion);

                try (CommentedFileConfig defaultCfg = CommentedFileConfig.builder(defaultServerConfigPath).sync().build()) {
                    oldConfig.entrySet().forEach(entry -> defaultCfg.set(entry.getKey(), entry.getValue()));
                    defaultCfg.save();
                }
                
                WildAside.LOGGER.info("Global default template created in 'defaultconfigs/'");

                Path backupPath = oldConfigPath.resolveSibling("wildaside-common.toml.bak");
                Files.move(oldConfigPath, backupPath, StandardCopyOption.REPLACE_EXISTING);
                WildAside.LOGGER.info("Legacy config retired to .bak");

            } catch (IOException e) {
                WildAside.LOGGER.error("Failed to migrate legacy config", e);
            }
        } else {
            if (CONFIG_VERSION.get().equals("unknown")) {
                CONFIG_VERSION.set(currentVersion);
                SERVER_SPEC.save();
            }
        }
    }

    private static void copyIfPresent(CommentedFileConfig source, String path, ForgeConfigSpec.ConfigValue target) {
        if (source.contains(path)) {
            target.set(source.get(path));
        }
    }

    public static void refreshServerFields() {
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

    private static void validateConfig() {
        String currentVersionStr = ModList.get().getModContainerById("wildaside")
                .map(mod -> mod.getModInfo().getVersion().toString())
                .orElse("unknown");

        if (configVersion == null) {
            configVersion = CONFIG_VERSION.get();
        }

        if (!configVersion.equals(currentVersionStr)) {
            WildAside.LOGGER.warn("Outdated config detected! Resetting to default...");
            CONFIG_VERSION.set(currentVersionStr);
            SERVER_SPEC.save();
        }
    }
}