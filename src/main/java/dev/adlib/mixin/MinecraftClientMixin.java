package dev.adlib.mixin;

import com.mojang.authlib.minecraft.UserApiService;
import dev.adlib.SessionIDLoginMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.session.ProfileKeys;
import net.minecraft.client.session.Session;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.nio.file.Path;
import java.util.UUID;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @Shadow
    @Final
    private UserApiService userApiService;

    @Shadow
    @Final
    public File runDirectory;

    @Unique
    private UUID lastProfileKeysUuid = null;

    @Unique
    private String lastProfileKeysToken = null;

    @Unique
    private ProfileKeys cachedProfileKeys = null;

    @Inject(method = "getSession", at = @At("HEAD"), cancellable = true)
    private void onGetSession(CallbackInfoReturnable<Session> cir) {
        if (!SessionIDLoginMod.overrideSession) {
            return;
        }

        cir.setReturnValue(SessionIDLoginMod.currentSession);
    }

    @Inject(method = "getProfileKeys", at = @At("HEAD"), cancellable = true)
    private void onGetProfileKeys(CallbackInfoReturnable<ProfileKeys> cir) {
        if (!SessionIDLoginMod.overrideSession) {
            return;
        }

        Session currentSession = SessionIDLoginMod.currentSession;
        UUID currentUuid = currentSession.getUuidOrNull();
        String currentToken = currentSession.getAccessToken();

        if (lastProfileKeysUuid == null ||
                !lastProfileKeysUuid.equals(currentUuid) ||
                lastProfileKeysToken == null ||
                !lastProfileKeysToken.equals(currentToken)) {

            lastProfileKeysUuid = currentUuid;
            lastProfileKeysToken = currentToken;

            SessionIDLoginMod.LOGGER.info("Session changed, creating new ProfileKeys for: {}", currentSession.getUsername());

            try {
                UserApiService service = this.userApiService;

                Path profileKeysPath = runDirectory.toPath().resolve("profilekeys");

                cachedProfileKeys = ProfileKeys.create(service, currentSession, profileKeysPath);

                SessionIDLoginMod.LOGGER.info("Successfully created new ProfileKeys for: {}", currentSession.getUsername());
            } catch (Exception e) {
                SessionIDLoginMod.LOGGER.error("Failed to create ProfileKeys: {}", e.getMessage());
                cachedProfileKeys = null;
            }
        }

        if (cachedProfileKeys != null) {
            cir.setReturnValue(cachedProfileKeys);
        }
    }
}
