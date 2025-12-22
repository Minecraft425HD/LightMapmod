package com.mamiyaotaru.voxelmap.mixins;

// 1.20.1: EnderDragonModel moved from model.monster.dragon to model.dragon
import net.minecraft.client.model.dragon.EnderDragonModel;
import net.minecraft.client.renderer.entity.EnderDragonRenderer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EnderDragonRenderer.class)
public interface AccessorEnderDragonRenderer {
    // 1.20.1: Use remap = true to let Mixin handle field name mapping
    @Accessor("model")
    EnderDragonModel getModel();

    // 1.20.1: Use remap = true for constants as well
    @Accessor("DRAGON_LOCATION")
    static ResourceLocation getTextureLocation() {
        throw new AssertionError();
    }
}
