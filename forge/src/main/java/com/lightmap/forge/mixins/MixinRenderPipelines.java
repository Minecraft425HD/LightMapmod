package com.lightmap.forge.mixins;

import com.lightmap.LightMapConstants;
import com.lightmap.forge.ForgeModApiBridge;
// TODO: 1.20.1 Port - RenderPipelines doesn't exist in 1.20.1, this is a 1.21.x class
// This entire mixin needs to be disabled or replaced with a 1.20.1 alternative
// For now, commenting out to allow compilation
/*
import net.minecraft.client.renderer.RenderPipelines;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderPipelines.class)
public class MixinRenderPipelines {

    @Inject(method = "<clinit>", at = @At("HEAD"))
    private static void onRegisterPipelines(CallbackInfo ci) {
        LightMapConstants.setModApiBride(new ForgeModApiBridge());
    }
}
*/

// TODO: 1.20.1 Port - Need to find alternative initialization point for ForgeModApiBridge
// The mod API bridge needs to be set somewhere else since RenderPipelines doesn't exist
public class MixinRenderPipelines {
    // Mixin disabled for 1.20.1 compatibility
}
