package com.example.tiertagger.client;

import com.example.tiertagger.TierTaggerMod;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

public class ClientInit implements ClientModInitializer {
    public static KeyBinding OPEN_GUI_KEY;

    @Override
    public void onInitializeClient() {
        OPEN_GUI_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.kb_tiers.open",
            GLFW.GLFW_KEY_UNKNOWN,
            "category.kb_tiers"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;
            if (OPEN_GUI_KEY.wasPressed()) {
                client.setScreen(new VerificationScreen());
            }
        });
    }
}
