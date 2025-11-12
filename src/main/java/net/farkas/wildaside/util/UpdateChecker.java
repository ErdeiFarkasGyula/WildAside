package net.farkas.wildaside.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.config.Config;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Mod.EventBusSubscriber(modid = WildAside.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class UpdateChecker {
    private static final String[] VERSION_URLS = {
            "https://raw.githubusercontent.com/ErdeiFarkasGyula/WildAside_Update/refs/heads/main/version_forge.json",
    };

    private static boolean checked = false;

    @SubscribeEvent
    public static void onClientJoin(ClientPlayerNetworkEvent.LoggingIn event) {
        if (checked || !Config.SHOW_UPDATE_NOTIFICATION.get()) return;
        checked = true;

        new Thread(() -> {
            JsonObject json = fetchVersionInfo();
            if (json == null) return;

            String latest = json.get("latestVersion").getAsString();
            JsonObject urls = json.getAsJsonObject("updateURLs");
            String modrinth = urls.get("modrinth").getAsString();
            String curseforge = urls.get("curseforge").getAsString();

            String currentVersion = ModList.get().getModContainerById("wildaside")
                    .map(mod -> mod.getModInfo().getVersion().toString())
                    .orElse("unknown");

            if (isNewerVersion(latest, currentVersion)) {
                Minecraft.getInstance().execute(() -> {
                    if (Minecraft.getInstance().player != null) {
                        var player = Minecraft.getInstance().player;

                        player.sendSystemMessage(Component.translatable("update_checker.wildaside.update_available",
                                        Component.translatable("mod.wildaside"),
                                        Component.literal(latest).withStyle(ChatFormatting.YELLOW),
                                        Component.literal(currentVersion).withStyle(ChatFormatting.GRAY))
                                .withStyle(ChatFormatting.GOLD)
                        );

                        player.sendSystemMessage(
                                Component.literal("[")
                                        .append(Component.translatable("update_checker.wildaside.curseforge_page"))
                                        .append(Component.literal("]"))
                                        .withStyle(style -> style
                                                .withColor(ChatFormatting.LIGHT_PURPLE)
                                                .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, curseforge))
                                                .withUnderlined(true))
                        );

                        player.sendSystemMessage(
                                Component.literal("[")
                                        .append(Component.translatable("update_checker.wildaside.modrinth_page"))
                                        .append(Component.literal("]"))
                                        .withStyle(style -> style
                                                .withColor(ChatFormatting.AQUA)
                                                .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, modrinth))
                                                .withUnderlined(true))
                        );

                        player.sendSystemMessage(
                                Component.literal("[")
                                        .append(Component.translatable("update_checker.wildaside.dont_show_again"))
                                        .append(Component.literal("]"))
                                        .withStyle(style -> style
                                                .withColor(ChatFormatting.GRAY)
                                                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/wildaside update_notification false"))
                                                .withUnderlined(false))
                        );
                    }
                });
            }
        }, "WildAside-UpdateChecker").start();
    }

    private static JsonObject fetchVersionInfo() {
        for (String url : VERSION_URLS) {
            try {
                HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                conn.setRequestProperty("User-Agent", "WildAside Update Checker");
                conn.setConnectTimeout(3000);
                conn.setReadTimeout(3000);
                conn.connect();

                JsonObject json = JsonParser.parseReader(new InputStreamReader(conn.getInputStream())).getAsJsonObject();
                conn.disconnect();
                return json;
            } catch (Exception e) {
                WildAside.LOGGER.warn("[Wild Aside] Failed to fetch version info from {}: {}", url, e.getMessage());
            }
        }
        return null;
    }

    private static boolean isNewerVersion(String latest, String current) {
        if (latest == null || current == null) return false;

        try {
            Version vLatest = Version.parse(latest);
            Version vCurrent = Version.parse(current);
            return vLatest.compareTo(vCurrent) > 0;
        } catch (Exception e) {
            WildAside.LOGGER.warn("[Wild Aside] Version compare failed: {}", e.getMessage());
            return false;
        }
    }

    private static class Version implements Comparable<Version> {
        private final int major;
        private final int minor;
        private final int patch;
        private final String preRelease;

        private Version(int major, int minor, int patch, String preRelease) {
            this.major = major;
            this.minor = minor;
            this.patch = patch;
            this.preRelease = preRelease == null ? "" : preRelease.toLowerCase();
        }

        public static Version parse(String version) {
            version = version.trim();

            String[] mainAndPre = version.split("-", 2);
            String[] numbers = mainAndPre[0].split("\\.");

            int major = numbers.length > 0 ? safeParseInt(numbers[0]) : 0;
            int minor = numbers.length > 1 ? safeParseInt(numbers[1]) : 0;
            int patch = numbers.length > 2 ? safeParseInt(numbers[2]) : 0;
            String pre = mainAndPre.length > 1 ? mainAndPre[1] : "";

            return new Version(major, minor, patch, pre);
        }

        private static int safeParseInt(String s) {
            try {
                return Integer.parseInt(s.replaceAll("[^0-9]", ""));
            } catch (Exception e) {
                return 0;
            }
        }

        @Override
        public int compareTo(Version other) {
            if (this.major != other.major)
                return Integer.compare(this.major, other.major);
            if (this.minor != other.minor)
                return Integer.compare(this.minor, other.minor);
            if (this.patch != other.patch)
                return Integer.compare(this.patch, other.patch);

            if (this.preRelease.isEmpty() && !other.preRelease.isEmpty()) return 1;
            if (!this.preRelease.isEmpty() && other.preRelease.isEmpty()) return -1;

            return this.preRelease.compareTo(other.preRelease);
        }

        @Override
        public String toString() {
            return major + "." + minor + "." + patch + (preRelease.isEmpty() ? "" : "-" + preRelease);
        }
    }
}