package dev.adlib.mixin;

import dev.adlib.SessionIDLoginMod;
import dev.adlib.screens.WelcomeScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {

    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        if (!SessionIDLoginMod.isWelcomeShown()) {
            MinecraftClient.getInstance().setScreen(new WelcomeScreen());
        }
    }
}