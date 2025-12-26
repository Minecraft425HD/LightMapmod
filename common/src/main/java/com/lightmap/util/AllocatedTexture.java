package com.lightmap.util;

// TODO: 1.20.1 Port - GPU APIs don't exist in 1.20.1
// import com.mojang.blaze3d.systems.RenderSystem;
// import com.mojang.blaze3d.textures.GpuTexture;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.server.packs.resources.ResourceManager;

/**
 * TODO: 1.20.1 Port - This class is stubbed out because GpuTexture doesn't exist in 1.20.1
 */
public class AllocatedTexture extends AbstractTexture {
    public AllocatedTexture(Object texture) {
        // TODO: 1.20.1 Port - Stubbed out until properly ported
        // this.texture = texture;
        // this.textureView = RenderSystem.getDevice().createTextureView(texture);
    }

    @Override
    public void load(ResourceManager manager) {
        // TODO: 1.20.1 Port - Empty implementation for allocated texture
        // This would normally load the texture from resources, but since we're using
        // a pre-allocated texture object, there's nothing to load
    }
}
