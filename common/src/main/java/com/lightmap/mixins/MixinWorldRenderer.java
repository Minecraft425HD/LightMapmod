package com.lightmap.mixins;

import com.lightmap.LightMapConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import org.joml.Matrix4f;
// import org.spongepowered.asm.mixin.Mixin;
// import org.spongepowered.asm.mixin.injection.At;
// import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// TODO: 1.20.1 Port - Mixin disabled due to refmap generation incompatibility with official mappings
// @Mixin(LevelRenderer.class)
public abstract class MixinWorldRenderer {

    // TODO: 1.20.1 Port - Explicit descriptor required for official mappings
    // @Inject(method = "renderLevel(Lcom/mojang/blaze3d/vertex/PoseStack;FJZLnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/GameRenderer;Lnet/minecraft/client/renderer/LightTexture;Lorg/joml/Matrix4f;)V", at = @At("RETURN"), require = 0)
    private void renderLevel(PoseStack poseStack, float partialTick, long finishNanoTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci) {
        // Waypoint rendering removed
    }

    // TODO: 1.20.1 Port - Targeting private 4-parameter version of setSectionDirty
    // @Inject(method = "setSectionDirty(IIIZ)V", at = @At("RETURN"), require = 0)
    public void postScheduleChunkRender(int x, int y, int z, boolean important, CallbackInfo ci) {
        if (LightMapConstants.getLightMapInstance().getWorldUpdateListener() != null) {
            LightMapConstants.getLightMapInstance().getWorldUpdateListener().notifyObservers(x, z);
        }
    }
}
