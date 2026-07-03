package com.example.tiertagger;

import com.example.tiertagger.commands.TierCommands;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class TierTaggerMod implements ModInitializer {
    public static final String MODID = "kb-tiers";
    private static ConfigManager configManager;
    private static TierManager tierManager;

    @Override
    public void onInitialize() {
        configManager = new ConfigManager();
        tierManager = new TierManager();
        tierManager.load();

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            TierCommands.register(dispatcher, tierManager, configManager);
        });
    }

    public static ConfigManager getConfigManager() { return configManager; }
    public static TierManager getTierManager() { return tierManager; }
}
