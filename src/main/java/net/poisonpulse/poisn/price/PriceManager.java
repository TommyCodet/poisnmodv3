package net.poisonpulse.poisn.price;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;
import net.poisonpulse.poisn.PoisnClient;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Fully offline price loader.
 * Priority:
 * 1. config/poisn/prices.json  (user can replace anytime)
 * 2. assets/poisn/prices.json  (bundled)
 */
public final class PriceManager {
    private static final Gson GSON = new Gson();
    private static final Path CONFIG_DIR = FabricLoader.getInstance().getConfigDir().resolve("poisn");
    private static final Path CONFIG_PRICES = CONFIG_DIR.resolve("prices.json");

    private static volatile Map<String, Double[]> items = Map.of();
    private static volatile Map<String, String> customNames = Map.of();

    private PriceManager() {}

    public static void load() {
        // Prefer user config
        if (Files.isRegularFile(CONFIG_PRICES)) {
            try (var reader = Files.newBufferedReader(CONFIG_PRICES, StandardCharsets.UTF_8)) {
                apply(GSON.fromJson(reader, JsonObject.class));
                PoisnClient.LOGGER.info("Loaded prices from {}", CONFIG_PRICES);
                return;
            } catch (Exception e) {
                PoisnClient.LOGGER.warn("Failed reading config prices.json: {}", e.toString());
            }
        }

        // Bundled fallback
        try (InputStream in = PriceManager.class.getResourceAsStream("/assets/poisn/prices.json")) {
            if (in != null) {
                apply(GSON.fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), JsonObject.class));
                PoisnClient.LOGGER.info("Loaded bundled prices.json ({} items)", items.size());
                // Also copy to config so user can easily edit later
                try {
                    Files.createDirectories(CONFIG_DIR);
                    if (!Files.exists(CONFIG_PRICES)) {
                        try (InputStream copy = PriceManager.class.getResourceAsStream("/assets/poisn/prices.json")) {
                            if (copy != null) Files.copy(copy, CONFIG_PRICES);
                        }
                    }
                } catch (Exception ignored) {}
                return;
            }
        } catch (Exception e) {
            PoisnClient.LOGGER.warn("Failed loading bundled prices: {}", e.toString());
        }

        PoisnClient.LOGGER.warn("No prices.json found – tooltips will show nothing");
    }

    private static void apply(JsonObject root) {
        if (root == null) return;

        Map<String, Double[]> newItems = new HashMap<>();
        Map<String, String> newNames = new HashMap<>();

        JsonElement itemsEl = root.get("items");
        if (itemsEl != null && itemsEl.isJsonObject()) {
            for (var entry : itemsEl.getAsJsonObject().entrySet()) {
                String key = normalize(entry.getKey());
                if (entry.getValue().isJsonArray()) {
                    var arr = entry.getValue().getAsJsonArray();
                    Double ah = (arr.size() > 0 && !arr.get(0).isJsonNull()) ? arr.get(0).getAsDouble() : null;
                    Double orders = (arr.size() > 1 && !arr.get(1).isJsonNull()) ? arr.get(1).getAsDouble() : null;
                    newItems.put(key, new Double[]{ah, orders});
                }
            }
        }

        JsonElement namesEl = root.get("names");
        if (namesEl != null && namesEl.isJsonObject()) {
            for (var entry : namesEl.getAsJsonObject().entrySet()) {
                if (!entry.getValue().isJsonNull()) {
                    newNames.put(entry.getKey(), entry.getValue().getAsString());
                }
            }
        }

        items = Map.copyOf(newItems);
        customNames = Map.copyOf(newNames);
    }

    public static Double[] get(String key) {
        if (key == null) return null;
        return items.get(normalize(key));
    }

    public static String customName(String key) {
        return customNames.get(key);
    }

    private static String normalize(String s) {
        return s == null ? "" : s.toLowerCase(Locale.ROOT).trim();
    }
}
