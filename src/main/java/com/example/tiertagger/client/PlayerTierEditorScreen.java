package com.example.tiertagger.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class PlayerTierEditorScreen extends Screen {
    private TextFieldWidget playerField;
    private int gamemodeIndex = 0;
    private int tierIndex = 0;
    private final String[] GAMEMODES = {"Nethpot","Sword","SMP","Vanilla","DiamodPot","Mace"};
    private final String[] TIERS = {"HT1","HT2","HT3","HT4","HT5","LT1","LT2","LT3","LT4","LT5"};

    protected PlayerTierEditorScreen() { super(Text.of("Player Tier Editor")); }

    @Override
    protected void init() {
        int w = this.width;
        int h = this.height;
        playerField = new TextFieldWidget(this.textRenderer, w/2 - 100, h/2 - 50, 200, 20, Text.of("Target Player (max 30)"));
        addSelectableChild(playerField);

        ButtonWidget gmBtn = new ButtonWidget(w/2 - 100, h/2 - 20, 98, 20, Text.of("Gamemode: " + GAMEMODES[gamemodeIndex]), (btn) -> {
            gamemodeIndex = (gamemodeIndex + 1) % GAMEMODES.length;
            btn.setMessage(Text.of("Gamemode: " + GAMEMODES[gamemodeIndex]));
        });
        addDrawableChild(gmBtn);

        ButtonWidget tierBtn = new ButtonWidget(w/2 + 2, h/2 - 20, 98, 20, Text.of("Tier: " + TIERS[tierIndex]), (btn) -> {
            tierIndex = (tierIndex + 1) % TIERS.length;
            btn.setMessage(Text.of("Tier: " + TIERS[tierIndex]));
        });
        addDrawableChild(tierBtn);

        addDrawableChild(new ButtonWidget(w/2 - 100, h/2 + 20, 98, 20, Text.of("Done"), (btn) -> {
            String player = playerField.getText().trim();
            if (player.length() == 0 || player.length() > 30) {
                if (MinecraftClient.getInstance().player != null) MinecraftClient.getInstance().player.sendMessage(Text.of("Invalid player name length (1-30)."), false);
                return;
            }
            String gm = GAMEMODES[gamemodeIndex];
            String tier = TIERS[tierIndex];
            // send a server command that will perform the change (server verifies) — works in singleplayer or if server has mod
            MinecraftClient.getInstance().player.networkHandler.getConnection().send(new net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket("/tiertagger addmode " + player + " " + gm + " " + tier));
            MinecraftClient.getInstance().setScreen(null);
        }));

        addDrawableChild(new ButtonWidget(w/2 + 2, h/2 + 20, 98, 20, Text.of("Cancel"), (btn) -> {
            MinecraftClient.getInstance().setScreen(null);
        }));
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();
        super.render(mouseX, mouseY, delta);
        playerField.render(mouseX, mouseY, delta);
    }
}
