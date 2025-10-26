package net.farkas.wildaside.dna.speed;

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
import java.util.*;

public class MobSpeedResultStorage {
    private static final Path FILE_PATH = FMLPaths.CONFIGDIR.get().resolve("wildaside_mob_speeds.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Map<ResourceLocation, Map<String, Double>> results = new HashMap<>();

    public static void load() {
        results.clear();

        if (!Files.exists(FILE_PATH)) {
            WildAside.LOGGER.warn("Mob speed file not found: {}", FILE_PATH);
            return;
        }

        try (Reader reader = Files.newBufferedReader(FILE_PATH)) {
            Type type = new TypeToken<Map<String, Map<String, Double>>>(){}.getType();
            Map<String, Map<String, Double>> data = GSON.fromJson(reader, type);

            if (data != null) {
                data.forEach((id, envMap) -> {
                    try {
                        results.put(new ResourceLocation(id), new HashMap<>(envMap));
                    } catch (Exception e) {
                        WildAside.LOGGER.warn("Skipping invalid mob ID in speed file: {}", id);
                    }
                });
            }

            WildAside.LOGGER.info("Loaded mob speed data for {} entities.", results.size());

        } catch (IOException e) {
            WildAside.LOGGER.error("Failed to load mob speed data", e);
        }
    }

    public static void save() {
        try (Writer writer = Files.newBufferedWriter(FILE_PATH)) {
            Map<String, Map<String, Double>> serializable = new TreeMap<>();
            results.forEach((id, map) -> serializable.put(id.toString(), map));
            GSON.toJson(serializable, writer);
        } catch (IOException e) {
            WildAside.LOGGER.error("Failed to save mob speed data", e);
        }
    }

    public static void record(EntityType<?> type, String environment, double blocksPerSecond) {
        ResourceLocation id = ForgeRegistries.ENTITY_TYPES.getKey(type);
        if (id == null) return;

        results.computeIfAbsent(id, k -> new HashMap<>()).put(environment, blocksPerSecond);
    }

    public static double getSpeed(EntityType<?> type, String environment) {
        ResourceLocation id = ForgeRegistries.ENTITY_TYPES.getKey(type);
        if (id == null) return 0.0;

        Map<String, Double> map = results.get(id);
        if (map == null) return 0.0;

        return map.getOrDefault(environment, 0.0);
    }

    public static Set<ResourceLocation> getAllEntities() {
        return Collections.unmodifiableSet(results.keySet());
    }

    public static boolean hasData(EntityType<?> type) {
        ResourceLocation id = ForgeRegistries.ENTITY_TYPES.getKey(type);
        return id != null && results.containsKey(id);
    }
}