package com.mamiyaotaru.voxelmap.mixins;

import net.minecraft.client.model.monster.dragon.EnderDragonModel;
import net.minecraft.client.renderer.entity.EnderDragonRenderer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EnderDragonRenderer.class)
public interface AccessorEnderDragonRenderer {
    // 1.20.1: Field names may be different - using require = 0
    @Accessor(value = "model", remap = false)
    EnderDragonModel getModel();

    // 1.20.1: DRAGON_LOCATION constant may have different name
    @Accessor(value = "DRAGON_LOCATION", remap = false)
    static ResourceLocation getTextureLocation() {
        throw new AssertionError();
    }
}
