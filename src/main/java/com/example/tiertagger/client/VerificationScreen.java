package com.example.tiertagger.client;

import com.example.tiertagger.ConfigManager;
import com.example.tiertagger.TierTaggerMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class VerificationScreen extends Screen {
    private TextFieldWidget userField;
    private TextFieldWidget codeField;

    protected VerificationScreen() { super(Text.of("Verification")); }

    @Override
    protected void init() {
        int w = this.width;
        int h = this.height;
        userField = new TextFieldWidget(this.textRenderer, w/2 - 100, h/2 - 30, 200, 20, Text.of("Username"));
        codeField = new TextFieldWidget(this.textRenderer, w/2 - 100, h/2, 200, 20, Text.of("Code"));
        addSelectableChild(userField);
        addSelectableChild(codeField);

        addDrawableChild(new ButtonWidget(w/2 - 100, h/2 + 30, 98, 20, Text.of("Verify"), (btn) -> {
            String u = userField.getText().trim();
            String c = codeField.getText().trim();
            ConfigManager cfg = TierTaggerMod.getConfigManager();
            if (u.equals(cfg.getVerifierUsername()) && c.equals(cfg.getVerifierCode())) {
                MinecraftClient.getInstance().setScreen(new PlayerTierEditorScreen());
            } else {
                if (MinecraftClient.getInstance().player != null) {
                    MinecraftClient.getInstance().player.sendMessage(Text.of("Verification failed."), false);
                }
            }
        }));

        addDrawableChild(new ButtonWidget(w/2 + 2, h/2 + 30, 98, 20, Text.of("Cancel"), (btn) -> {
            MinecraftClient.getInstance().setScreen(null);
        }));
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();
        super.render(mouseX, mouseY, delta);
        userField.render(mouseX, mouseY, delta);
        codeField.render(mouseX, mouseY, delta);
    }
}
