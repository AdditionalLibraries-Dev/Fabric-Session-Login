package dev.adlib.mixin;

import dev.adlib.utils.APIUtils;
import dev.adlib.utils.SessionUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class MultiPlayerScreenRenderMixin {

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!(((Object) this) instanceof MultiplayerScreen)) return;

        net.minecraft.client.font.TextRenderer tr = MinecraftClient.getInstance().textRenderer;

        if (SessionUtils.isSessionValid == null && !SessionUtils.hasValidationStarted) {
            SessionUtils.hasValidationStarted = true;
            new Thread(() -> {
                SessionUtils.isSessionValid = APIUtils.validateSession(
                        MinecraftClient.getInstance().getSession().getAccessToken()
                );
            }, "SessionValidationThread").start();
        }

        Text statusText;
        if (SessionUtils.isSessionValid == null) {
            statusText = Text.literal("[... Validating]").styled(s -> s.withColor(0xFF888888));
        } else if (SessionUtils.isSessionValid) {
            statusText = Text.literal("[✔] Valid").styled(s -> s.withColor(0xFF00FF00));
        } else {
            statusText = Text.literal("[✘] Invalid").styled(s -> s.withColor(0xFFFF0000));
        }

        String username = SessionUtils.getUsername();
        Text display = Text.literal("User: ").styled(s -> s.withColor(0xFFFFFFFF))
                .append(Text.literal(username).styled(s -> s.withColor(0xFFFFFFFF)))
                .append(Text.literal(" | ").styled(s -> s.withColor(0xFF555555)))
                .append(statusText);

        context.drawText(tr, display, 5, 10, 0xFFFFFFFF, false);
    }
}