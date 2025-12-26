package com.lightmap.util;

// TODO: 1.20.1 Port - GPU APIs don't exist in 1.20.1
// import com.mojang.blaze3d.buffers.GpuBuffer;
// import com.mojang.blaze3d.buffers.GpuBufferSlice;
// import com.mojang.blaze3d.buffers.Std140Builder;
// import com.mojang.blaze3d.systems.GpuDevice;
// import com.mojang.blaze3d.systems.RenderSystem;
// import java.nio.ByteBuffer;
// import org.joml.Matrix4f;
// import org.lwjgl.system.MemoryStack;

/**
 * See {@link net.minecraft.client.renderer.CachedOrthoProjectionMatrixBuffer}
 *
 * TODO: 1.20.1 Port - This class is stubbed out because GPU APIs don't exist in 1.20.1
 */
public class LightMapCachedOrthoProjectionMatrixBuffer implements AutoCloseable {
    // private final GpuBuffer buffer;
    // private final GpuBufferSlice bufferSlice;

    public LightMapCachedOrthoProjectionMatrixBuffer(String string, float left, float right, float bottom, float top, float zNear, float zFar) {
        // TODO: 1.20.1 Port - RenderSystem.getDevice() doesn't exist in 1.20.1
        // GpuDevice gpuDevice = RenderSystem.getDevice();
        // this.buffer = gpuDevice.createBuffer(() -> "Projection matrix UBO " + string, GpuBuffer.USAGE_UNIFORM + GpuBuffer.USAGE_COPY_DST, RenderSystem.PROJECTION_MATRIX_UBO_SIZE);
        // this.buffer = null; // TODO: Replace with 1.20.1 compatible buffer creation
        // this.bufferSlice = this.buffer.slice(0, RenderSystem.PROJECTION_MATRIX_UBO_SIZE);
        // this.bufferSlice = null; // TODO: Replace with 1.20.1 compatible buffer slice

        // Matrix4f matrix4f = new Matrix4f().ortho(left, right, bottom, top, zNear, zFar);

        // try (MemoryStack memoryStack = MemoryStack.stackPush()) {
        //     ByteBuffer byteBuffer = Std140Builder.onStack(memoryStack, RenderSystem.PROJECTION_MATRIX_UBO_SIZE)
        //             .putMat4f(matrix4f).get();
        //     RenderSystem.getDevice().createCommandEncoder().writeToBuffer(this.buffer.slice(), byteBuffer);
        // }
    }

    public Object getBuffer() {
        // TODO: 1.20.1 Port - Return null until properly ported
        return null;
    }

    @Override
    public void close() {
        // TODO: 1.20.1 Port - No-op until properly ported
        // this.buffer.close();
    }
}
