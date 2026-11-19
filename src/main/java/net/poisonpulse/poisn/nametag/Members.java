package net.poisonpulse.poisn.nametag;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.poisonpulse.poisn.PoisnClient;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Loads member nametag settings from config/poisn/members.json
 * Fully offline. Editable without recompiling.
 */
public final class Members {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_DIR = FabricLoader.getInstance().getConfigDir().resolve("poisn");
    private static final Path CONFIG_FILE = CONFIG_DIR.resolve("members.json");

    private static final Map<String, MemberInfo> BY_NAME = new HashMap<>();

    public record MemberInfo(
            String rank,
            String underName,
            ChatFormatting rankColor,
            ChatFormatting nameColor,
            ChatFormatting underColor
    ) {}

    private Members() {}

    public static void init() {
        if (Files.isRegularFile(CONFIG_FILE)) {
            loadFromFile();
        } else {
            createDefaultConfig();
            loadFromFile();
        }
        PoisnClient.LOGGER.info("Loaded {} members from config", BY_NAME.size());
    }

    private static void createDefaultConfig() {
        try {
            Files.createDirectories(CONFIG_DIR);

            JsonObject root = new JsonObject();
            JsonArray members = new JsonArray();

            // TxmmyYT_
            members.add(member(
                    "TxmmyYT_",
                    "Member",
                    null,
                    "green",      // rank color
                    "white",      // name color
                    "white"       // under color (unused)
            ));

            // The_downfall
            members.add(member(
                    "The_downfall",
                    "Member",
                    null,
                    "green",
                    "white",
                    "white"
            ));

            // Poison_PulseYT – Leader + Twitch line
            members.add(member(
                    "Poison_PulseYT",
                    "Leader",
                    "Twitch: PosonPulse",
                    "gold",           // rank color
                    "green",          // name color (POISN green)
                    "light_purple"    // under name color (Twitch lila/blau)
            ));

            root.add("members", members);

            // Optional color legend for the user
            JsonObject legend = new JsonObject();
            legend.addProperty("green", "POISN Grün");
            legend.addProperty("gold", "Leader Gold");
            legend.addProperty("aqua", "Hellblau");
            legend.addProperty("blue", "Blau");
            legend.addProperty("light_purple", "Lila / Twitch");
            legend.addProperty("dark_purple", "Dunkel-Lila");
            legend.addProperty("white", "Weiß");
            legend.addProperty("gray", "Grau");
            legend.addProperty("yellow", "Gelb");
            legend.addProperty("red", "Rot");
            root.add("colors", legend);

            Files.writeString(CONFIG_FILE, GSON.toJson(root), StandardCharsets.UTF_8);
            PoisnClient.LOGGER.info("Created default members.json at {}", CONFIG_FILE);
        } catch (Exception e) {
            PoisnClient.LOGGER.error("Failed to create members.json", e);
        }
    }

    private static JsonObject member(String name, String rank, String under,
                                     String rankColor, String nameColor, String underColor) {
        JsonObject o = new JsonObject();
        o.addProperty("name", name);
        o.addProperty("rank", rank);
        if (under != null) o.addProperty("under", under);
        o.addProperty("rankColor", rankColor);
        o.addProperty("nameColor", nameColor);
        o.addProperty("underColor", underColor);
        return o;
    }

    private static void loadFromFile() {
        BY_NAME.clear();
        try {
            String json = Files.readString(CONFIG_FILE, StandardCharsets.UTF_8);
            JsonObject root = GSON.fromJson(json, JsonObject.class);
            if (root == null || !root.has("members")) return;

            JsonArray arr = root.getAsJsonArray("members");
            for (JsonElement el : arr) {
                if (!el.isJsonObject()) continue;
                JsonObject o = el.getAsJsonObject();

                String name = getString(o, "name");
                if (name == null || name.isBlank()) continue;

                String rank = getString(o, "rank");
                if (rank == null) rank = "Member";

                String under = getString(o, "under");

                ChatFormatting rankColor = parseColor(getString(o, "rankColor"), ChatFormatting.GREEN);
                ChatFormatting nameColor = parseColor(getString(o, "nameColor"), ChatFormatting.WHITE);
                ChatFormatting underColor = parseColor(getString(o, "underColor"), ChatFormatting.LIGHT_PURPLE);

                BY_NAME.put(name.toLowerCase(Locale.ROOT),
                        new MemberInfo(rank, under, rankColor, nameColor, underColor));
            }
        } catch (Exception e) {
            PoisnClient.LOGGER.error("Failed to load members.json", e);
        }
    }

    private static String getString(JsonObject o, String key) {
        return o.has(key) && !o.get(key).isJsonNull() ? o.get(key).getAsString() : null;
    }

    private static ChatFormatting parseColor(String name, ChatFormatting fallback) {
        if (name == null || name.isBlank()) return fallback;
        try {
            return ChatFormatting.valueOf(name.trim().toUpperCase(Locale.ROOT));
        } catch (Exception e) {
            // also accept common aliases
            return switch (name.toLowerCase(Locale.ROOT)) {
                case "green", "poisn", "poisn_green" -> ChatFormatting.GREEN;
                case "gold", "leader" -> ChatFormatting.GOLD;
                case "aqua", "cyan" -> ChatFormatting.AQUA;
                case "blue" -> ChatFormatting.BLUE;
                case "light_purple", "lila", "purple", "twitch" -> ChatFormatting.LIGHT_PURPLE;
                case "dark_purple" -> ChatFormatting.DARK_PURPLE;
                case "white" -> ChatFormatting.WHITE;
                case "gray", "grey" -> ChatFormatting.GRAY;
                case "yellow" -> ChatFormatting.YELLOW;
                case "red" -> ChatFormatting.RED;
                case "dark_green" -> ChatFormatting.DARK_GREEN;
                case "dark_aqua" -> ChatFormatting.DARK_AQUA;
                case "dark_blue" -> ChatFormatting.DARK_BLUE;
                case "dark_red" -> ChatFormatting.DARK_RED;
                case "dark_gray", "dark_grey" -> ChatFormatting.DARK_GRAY;
                case "black" -> ChatFormatting.BLACK;
                default -> fallback;
            };
        }
    }

    public static MemberInfo get(String name) {
        if (name == null) return null;
        return BY_NAME.get(name.toLowerCase(Locale.ROOT));
    }

    public static boolean isMember(String name) {
        return get(name) != null;
    }

    public static Component buildNameTag(String originalName) {
        MemberInfo info = get(originalName);
        if (info == null) {
            return Component.literal(originalName);
        }

        MutableComponent prefix = Component.literal("[" + info.rank() + "] ")
                .withStyle(Style.EMPTY.withColor(info.rankColor()));

        MutableComponent namePart = Component.literal(originalName)
                .withStyle(Style.EMPTY.withColor(info.nameColor()));

        MutableComponent result = prefix.append(namePart);

        if (info.underName() != null && !info.underName().isBlank()) {
            result.append(Component.literal("\n")
                    .append(Component.literal(info.underName())
                            .withStyle(Style.EMPTY.withColor(info.underColor()))));
        }

        return result;
    }
}
