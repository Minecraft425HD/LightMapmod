package com.mamiyaotaru.voxelmap.mixins;

import com.mamiyaotaru.voxelmap.VoxelConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class MixinWorldRenderer {

    @Unique private final PoseStack voxelmap_poseStack = new PoseStack();

    // 1.20.1: renderLevel method has different signature
    @Inject(method = "renderLevel", at = @At("RETURN"), require = 0)
    private void renderLevel(PoseStack poseStack, float partialTick, long finishNanoTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci) {
        voxelmap_poseStack.pushPose();
        voxelmap_poseStack.last().pose().set(projectionMatrix);
        BufferSource bufferSource = VoxelConstants.getMinecraft().renderBuffers().bufferSource();
        VoxelConstants.onRenderWaypoints(partialTick, voxelmap_poseStack, bufferSource, camera);

        voxelmap_poseStack.popPose();
    }

    // 1.20.1: setSectionDirty method
    @Inject(method = "setSectionDirty(IIIZ)V", at = @At("RETURN"), require = 0)
    public void postScheduleChunkRender(int x, int y, int z, boolean important, CallbackInfo ci) {
        if (VoxelConstants.getVoxelMapInstance().getWorldUpdateListener() != null) {
            VoxelConstants.getVoxelMapInstance().getWorldUpdateListener().notifyObservers(x, z);
        }
    }
}
