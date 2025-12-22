package com.mamiyaotaru.voxelmap.mixins;

import com.mamiyaotaru.voxelmap.VoxelConstants;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class APIMixinNetHandlerPlayClient {
    // 1.20.1: Using sendCommand(String) method
    @Inject(method = "sendCommand(Ljava/lang/String;)V", at = @At("HEAD"), cancellable = true, require = 0)
    public void onSendCommand(String string, CallbackInfo cir) {
        if (voxelmap$parseCommand(string)) {
            cir.cancel();
        }
    }

    // sendUnattendedCommand doesn't exist in 1.20.1 - commenting out
    // @Inject(method = "sendUnattendedCommand", at = @At("HEAD"), cancellable = true)
    // public void onUnsignedCommand(String string, Screen screen, CallbackInfo ci) {
    //     if (voxelmap$parseCommand(string)) {
    //         ci.cancel();
    //     }
    // }


    @Unique
    private boolean voxelmap$parseCommand(String command) {
        VoxelConstants.getLogger().info("Command: " + command);
        return !VoxelConstants.onSendChatMessage(command);
    }
}
