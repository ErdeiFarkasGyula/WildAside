package net.farkas.wildaside.dna.testing;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.worldgen.dimension.ModDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
                onModListChanged(server, data.lastHash, currentHash);

                data.lastHash = currentHash;
                data.setDirty();
            }

            MobSpeedResultStorage.load();
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