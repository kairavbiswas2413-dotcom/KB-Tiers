package com.example.tiertagger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static class Config {
        public String ownerUuid = null;
        public boolean disableRemoteFetch = false;
        public String verifierUsername = "KB_PlazZ";
        public String verifierCode = "331025";
    }

    private final Path configDir;
    private final Path configFile;
    private Config config;

    public ConfigManager() {
        this.configDir = FabricLoader.getInstance().getConfigDir().resolve("cctl_tiertagger");
        this.configFile = configDir.resolve("config.json");
        load();
    }

    public void load() {
        try {
            if (!Files.exists(configDir)) Files.createDirectories(configDir);
            if (Files.exists(configFile)) {
                try (Reader r = Files.newBufferedReader(configFile)) {
                    config = GSON.fromJson(r, Config.class);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (config == null) config = new Config();
    }

    public void save() {
        try {
            if (!Files.exists(configDir)) Files.createDirectories(configDir);
            try (Writer w = Files.newBufferedWriter(configFile)) {
                GSON.toJson(config, w);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public UUID getOwnerUuid() {
        if (config.ownerUuid == null || config.ownerUuid.isEmpty()) return null;
        try { return UUID.fromString(config.ownerUuid); }
        catch (IllegalArgumentException e) { return null; }
    }

    public void setOwnerUuid(UUID uuid) {
        config.ownerUuid = uuid == null ? null : uuid.toString();
        save();
    }

    public boolean isRemoteFetchDisabled() { return config.disableRemoteFetch; }
    public void setRemoteFetchDisabled(boolean disabled) { config.disableRemoteFetch = disabled; save(); }

    public String getVerifierUsername() { return config.verifierUsername; }
    public String getVerifierCode() { return config.verifierCode; }
    public void setVerifier(String username, String code) {
        config.verifierUsername = username;
        config.verifierCode = code;
        save();
    }
}
