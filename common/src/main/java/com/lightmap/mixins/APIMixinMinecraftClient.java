package com.lightmap.mixins;

import com.lightmap.LightMapConstants;
import net.minecraft.client.Minecraft;
// import org.spongepowered.asm.mixin.Mixin;
// import org.spongepowered.asm.mixin.injection.At;
// import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// TODO: 1.20.1 Port - Mixin disabled due to refmap generation incompatibility with official mappings
// @Mixin(Minecraft.class)
public class APIMixinMinecraftClient {

    // TODO: 1.20.1 Port - Explicit descriptor required for official mappings
    // @Inject(method = "tick()V", at = @At("RETURN"), require = 0)
    private void onTick(CallbackInfo ci) {
        LightMapConstants.clientTick();
    }

}
