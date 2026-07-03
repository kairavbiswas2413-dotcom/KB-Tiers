package com.example.tiertagger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TierManager {
    private final Path configDir;
    private final Path dataFile;
    private Map<String, PlayerTiers> entries = new HashMap<>();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public TierManager() {
        this.configDir = FabricLoader.getInstance().getConfigDir().resolve("cctl_tiertagger");
        this.dataFile = configDir.resolve("tiers.json");
    }

    public void load() {
        try {
            if (!Files.exists(configDir)) Files.createDirectories(configDir);
            if (Files.exists(dataFile)) {
                try (Reader r = Files.newBufferedReader(dataFile)) {
                    Type mapType = new TypeToken<Map<String, PlayerTiers>>(){}.getType();
                    Map<String, PlayerTiers> loaded = gson.fromJson(r, mapType);
                    if (loaded != null) entries = loaded;
                }
            } else {
                save();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try (Writer w = Files.newBufferedWriter(dataFile)) {
            gson.toJson(entries, w);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void backup() {
        try {
            if (!Files.exists(dataFile)) return;
            String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            Path backup = configDir.resolve("tiers_backup_" + ts + ".json");
            Files.copy(dataFile, backup);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setPlayerGamemodeTier(String playerName, String gamemode, String tier) {
        String key = playerName.toLowerCase();
        PlayerTiers pt = entries.computeIfAbsent(key, k -> new PlayerTiers(playerName));
        pt.gamemodeToTier.put(gamemode, tier);
        save();
    }

    public Optional<String> getTierFor(String playerName, String gamemode) {
        PlayerTiers pt = entries.get(playerName.toLowerCase());
        if (pt == null) return Optional.empty();
        return Optional.ofNullable(pt.gamemodeToTier.get(gamemode));
    }

    public Map<String, PlayerTiers> getAll() { return entries; }

    public boolean removePlayer(String playerName) {
        if (entries.remove(playerName.toLowerCase()) != null) { save(); return true; }
        return false;
    }

    public void clearAll() { backup(); entries.clear(); save(); }

    public static class PlayerTiers {
        public String playerName;
        public Map<String, String> gamemodeToTier = new HashMap<>();
        public PlayerTiers() {}
        public PlayerTiers(String playerName) { this.playerName = playerName; }
    }
}
