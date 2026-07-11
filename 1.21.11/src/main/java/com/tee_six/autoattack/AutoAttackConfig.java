package com.tee_six.autoattack;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Configuration for Auto Attack mod.
 * Stored as JSON in .minecraft/config/autoattack.json
 */
public class AutoAttackConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("autoattack.json");

    public static AutoAttackConfig INSTANCE = new AutoAttackConfig();

    /**
     * Whether the auto attack mod is enabled.
     */
    public boolean enabled = true;

    /**
     * Tick delay between mace attacks (0 = spam every tick, max 20 = 1 second).
     */
    public int maceTickDelay = 0;

    /**
     * Load config from disk. Creates default config if file doesn't exist.
     */
    public static void load() {
        if (Files.exists(CONFIG_PATH)) {
            try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                INSTANCE = GSON.fromJson(reader, AutoAttackConfig.class);
            } catch (IOException e) {
                AutoAttack.LOGGER.error("Failed to load AutoAttack config", e);
                INSTANCE = new AutoAttackConfig();
            }
        } else {
            save();
        }
    }

    /**
     * Save current config to disk.
     */
    public static void save() {
        try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
            GSON.toJson(INSTANCE, writer);
        } catch (IOException e) {
            AutoAttack.LOGGER.error("Failed to save AutoAttack config", e);
        }
    }
}
