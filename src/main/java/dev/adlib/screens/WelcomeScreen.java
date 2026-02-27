package dev.adlib.screens;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;

public class WelcomeScreen extends Screen {

    private static final String[] WELCOME_MESSAGE = {
            "Made by Additional Libraries",
            "",
            "To use — type \"adlib\" below and click Close.",
    };

    private TextFieldWidget textField;
    private ButtonWidget closeButton;
    private ButtonWidget guideButton;
    private int titleY, subtitleY, instructY;

    public WelcomeScreen() {
        super(Text.literal("Welcome"));
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;

        // Считаем высоту блока контента и центрируем его целиком
        int blockHeight = 12 + 8 + 12 + 8 + 20 + 16 + 20; // title + gap + text lines + gap + field + gap + buttons
        int blockStartY = (this.height - blockHeight) / 2 - 35;

        titleY = blockStartY;
        subtitleY = titleY + 20;       // +20 от заголовка
        instructY = subtitleY + 14;    // +14 от подзаголовка
        int fieldY = instructY + 28;    // +22 от текста
        int buttonsY = fieldY + 28;       // +28 от поля

        textField = new TextFieldWidget(
                this.textRenderer,
                centerX - 100, fieldY,
                200, 20,
                Text.literal("Type here")
        );
        textField.setMaxLength(20);
        textField.setFocused(true);
        this.addSelectableChild(textField);

        int btnW = 148;
        int gap  = 6;
        guideButton = ButtonWidget.builder(Text.literal("Github"), button -> {
            net.minecraft.util.Util.getOperatingSystem().open("https://github.com");
        }).dimensions(centerX - btnW - gap / 2, buttonsY, btnW, 20).build();
        this.addDrawableChild(guideButton);

        closeButton = ButtonWidget.builder(Text.literal("Close"), button -> {
            dev.adlib.SessionIDLoginMod.setWelcomeShown();
            assert this.client != null;
            this.client.setScreen(new TitleScreen());
        }).dimensions(centerX + gap / 2, buttonsY, btnW, 20).build();
        closeButton.active = false;
        this.addDrawableChild(closeButton);
    }

    @Override
    public void tick() {
        if (textField != null) {
            closeButton.active = textField.getText().equalsIgnoreCase("adlib");
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        long time = System.currentTimeMillis();
        float hue = (float) ((time / 10) % 500) / 500f;
        int r = java.awt.Color.HSBtoRGB(hue, 1f, 1f);
        int chromaColor = 0xFF000000 | (r & 0x00FFFFFF);

        String title = "Session ID Login Mod";
        context.drawCenteredTextWithShadow(this.textRenderer, title, this.width / 2, titleY, chromaColor);

        context.drawCenteredTextWithShadow(this.textRenderer,
                "Made by Additional Libraries", this.width / 2, subtitleY, 0xFFFFFFFF);
        context.drawCenteredTextWithShadow(this.textRenderer,
                "To use — type \"adlib\" below and click Close.", this.width / 2, instructY, 0xFFAAAAAA);

        textField.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (textField.keyPressed(input) || textField.isActive()) return true;
        return super.keyPressed(input);
    }

    @Override
    public boolean charTyped(CharInput input) {
        if (textField.charTyped(input)) return true;
        return super.charTyped(input);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}