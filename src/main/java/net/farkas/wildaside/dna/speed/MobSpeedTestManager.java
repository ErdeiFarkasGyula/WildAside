package net.farkas.wildaside.dna.speed;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.worldgen.dimension.ModDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Objects;

@Mod.EventBusSubscriber(modid = WildAside.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MobSpeedTestManager {
    private static final String DATA_NAME = "mod_version_tracker";

    public static class ModTrackerData extends SavedData {
        private String lastHash = "";

        @Override
        public CompoundTag save(CompoundTag tag) {
            tag.putString("LastHash", lastHash);
            return tag;
        }

        public static ModTrackerData load(CompoundTag tag) {
            ModTrackerData data = new ModTrackerData();
            data.lastHash = tag.getString("LastHash");
            return data;
        }
    }

    @SubscribeEvent
    public static void onWorldLoad(ServerStartedEvent event) {
        if (event.getServer() != null) {
            System.out.println("SERVERRR");
            MinecraftServer server = event.getServer();

            ModTrackerData data = server.overworld()
                    .getDataStorage()
                    .computeIfAbsent(ModTrackerData::load, ModTrackerData::new, DATA_NAME);

            String currentHash = computeModListHash();

            if (!Objects.equals(data.lastHash, currentHash)) {
                System.out.println("WILDASS:CHANGEDLIST");
                onModListChanged(server, data.lastHash, currentHash);

                data.lastHash = currentHash;
                data.setDirty();
            }

            MobSpeedResultStorage.load();
        }
    }

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        loadTestLevelArea(event);
    }

    public static void loadTestLevelArea(ServerStartingEvent event) {
        MinecraftServer server = event.getServer();
        ServerLevel level = server.getLevel(ModDimensions.TEST_LEVEL);

        if (level != null) {
            ensureTestAreaLoaded(level, new BlockPos(0, 16, 0), 150, MobSpeedTesting.getMobsToTest(level).size() * 4 + 2);
        }
    }

    public static void ensureTestAreaLoaded(ServerLevel level, BlockPos origin, int length, int width) {
        int chunkXStart = origin.getX() >> 4;
        int chunkZStart = origin.getZ() >> 4;
        int chunkXEnd = (origin.getX() + length) >> 4;
        int chunkZEnd = (origin.getZ() + width) >> 4;

        for (int cx = chunkXStart; cx <= chunkXEnd; cx++) {
            for (int cz = chunkZStart; cz <= chunkZEnd; cz++) {
                level.setChunkForced(cx, cz, true);
            }
        }
    }

    public static void unloadTestLevelArea(ServerLevel level, BlockPos origin, int length, int width) {
        if (level != null) {
            int chunkXStart = origin.getX() >> 4;
            int chunkZStart = origin.getZ() >> 4;
            int chunkXEnd = (origin.getX() + length) >> 4;
            int chunkZEnd = (origin.getZ() + width) >> 4;

            for (int cx = chunkXStart; cx <= chunkXEnd; cx++) {
                for (int cz = chunkZStart; cz <= chunkZEnd; cz++) {
                    level.setChunkForced(cx, cz, false);
                    level.unload(level.getChunk(chunkXStart, chunkZEnd));
                }
            }
        }
    }

    private static void onModListChanged(MinecraftServer server, String oldHash, String newHash) {
        ServerLevel level = server.getLevel(ModDimensions.TEST_LEVEL);
        MobSpeedTesting.runBatchTest(level, new BlockPos(0, 5, 0));
    }

    private static String computeModListHash() {
        List<String> mods = ModList.get().getMods().stream()
                .map(mod -> mod.getModId() + ":" + mod.getVersion().toString())
                .sorted().toList();

        return Integer.toHexString(mods.toString().hashCode());
    }
}
