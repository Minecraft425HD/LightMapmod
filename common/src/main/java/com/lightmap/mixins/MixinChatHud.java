package com.lightmap.mixins;

import com.lightmap.LightMap;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
// import org.spongepowered.asm.mixin.Mixin;
// import org.spongepowered.asm.mixin.injection.At;
// import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// TODO: 1.20.1 Port - Mixin disabled due to refmap generation incompatibility with official mappings
// @Mixin(ChatComponent.class)
public class MixinChatHud {

    // TODO: 1.20.1 Port - addMessage is overloaded, must specify 1-parameter descriptor
    // @Inject(method = "addMessage(Lnet/minecraft/network/chat/Component;)V", at = @At("HEAD"))
    private void addMessage(Component message, CallbackInfo ci) {
        LightMap.checkPermissionMessages(message);
    }
}
