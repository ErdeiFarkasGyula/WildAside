package net.farkas.wildaside.dna.testing;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.farkas.wildaside.WildAside;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class MobSpeedResultStorage {
    private static final Path FILE_PATH = FMLPaths.CONFIGDIR.get().resolve("wildaside_mob_speeds.json");

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final Map<ResourceLocation, Map<String, Double>> results = new HashMap<>();

    public static void record(EntityType<?> type, String environment, double blocksPerSecond) {
        results.computeIfAbsent(ForgeRegistries.ENTITY_TYPES.getKey(type), k -> new HashMap<>())
                .put(environment, blocksPerSecond);
    }

    public static void save() {
        try (Writer writer = Files.newBufferedWriter(FILE_PATH)) {
            GSON.toJson(results, writer);
        } catch (IOException e) {
            WildAside.LOGGER.error("Failed to save mob speed data", e);
        }
    }

    public static void load() {
        if (!Files.exists(FILE_PATH)) return;
        try (Reader reader = Files.newBufferedReader(FILE_PATH)) {
            Type type = new TypeToken<Map<ResourceLocation, Map<String, Double>>>(){}.getType();
            Map<ResourceLocation, Map<String, Double>> data = GSON.fromJson(reader, type);
            if (data != null) results.putAll(data);
        } catch (IOException e) {
            WildAside.LOGGER.error("Failed to load mob speed data", e);
        }
    }

    public static double getSpeed(EntityType<?> type, String environment) {
        return results.getOrDefault(ForgeRegistries.ENTITY_TYPES.getKey(type), Map.of())
                .getOrDefault(environment, 0.0);
    }
}
