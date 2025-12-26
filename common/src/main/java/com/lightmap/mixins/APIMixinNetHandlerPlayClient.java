package com.lightmap.mixins;

import com.lightmap.LightMapConstants;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientPacketListener;
// import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
// import org.spongepowered.asm.mixin.injection.At;
// import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// TODO: 1.20.1 Port - Mixin disabled due to refmap generation incompatibility with official mappings
// @Mixin(ClientPacketListener.class)
public abstract class APIMixinNetHandlerPlayClient {
    // TODO: 1.20.1 Port - Explicit descriptor required for official mappings
    // @Inject(method = "sendCommand(Ljava/lang/String;)V", at = @At("HEAD"), cancellable = true, require = 0)
    public void onSendCommand(String string, CallbackInfo cir) {
        if (lightmap$parseCommand(string)) {
            cir.cancel();
        }
    }

    // sendUnattendedCommand doesn't exist in 1.20.1 - commenting out
    // @Inject(method = "sendUnattendedCommand", at = @At("HEAD"), cancellable = true)
    // public void onUnsignedCommand(String string, Screen screen, CallbackInfo ci) {
    //     if (lightmap$parseCommand(string)) {
    //         ci.cancel();
    //     }
    // }


    @Unique
    private boolean lightmap$parseCommand(String command) {
        LightMapConstants.getLogger().info("Command: " + command);
        return !LightMapConstants.onSendChatMessage(command);
    }
}
