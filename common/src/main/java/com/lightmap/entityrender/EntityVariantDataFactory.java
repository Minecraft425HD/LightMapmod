package com.lightmap.entityrender;

import net.minecraft.client.renderer.entity.EntityRenderer;
// EntityRenderState doesn't exist in 1.20.1
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

public interface EntityVariantDataFactory {

    EntityType<?> getType();

    // 1.20.1: Removed EntityRenderState parameter - doesn't exist in 1.20.1
    EntityVariantData createVariantData(Entity entity, @SuppressWarnings("rawtypes") EntityRenderer renderer, int size, boolean addBorder);

}