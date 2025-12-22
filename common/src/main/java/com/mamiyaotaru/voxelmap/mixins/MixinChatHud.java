package com.mamiyaotaru.voxelmap.mixins;

import com.mamiyaotaru.voxelmap.VoxelMap;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatComponent.class)
public class MixinChatHud {

    // TODO: 1.20.1 Port - addMessage uses 1-parameter signature, let Mixin auto-detect
    @Inject(method = "addMessage", at = @At("HEAD"))
    private void addMessage(Component message, CallbackInfo ci) {
        VoxelMap.checkPermissionMessages(message);
    }
}
