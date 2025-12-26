package com.lightmap.entityrender.variants;

import com.lightmap.entityrender.EntityVariantData;
import com.lightmap.entityrender.EntityVariantDataFactory;
// TODO: 1.20.1 Port - AccessorEnderDragonRenderer is disabled
// import com.lightmap.mixins.AccessorEnderDragonRenderer;
import net.minecraft.client.renderer.entity.EnderDragonRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
// EntityRenderState doesn't exist in 1.20.1
// LivingEntityRenderState doesn't exist in 1.20.1
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

public class DefaultEntityVariantDataFactory implements EntityVariantDataFactory {
    private final EntityType<?> type;
    private final ResourceLocation secondaryTexture;

    public DefaultEntityVariantDataFactory(EntityType<?> type) {
        this(type, null);
    }

    public DefaultEntityVariantDataFactory(EntityType<?> type, ResourceLocation secondaryTexture) {
        this.type = type;
        this.secondaryTexture = secondaryTexture;
    }

    @Override
    public EntityType<?> getType() {
        return type;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public EntityVariantData createVariantData(Entity entity, EntityRenderer renderer, int size, boolean addBorder) {
        if (renderer instanceof EnderDragonRenderer) {
            // TODO: 1.20.1 Port - AccessorEnderDragonRenderer doesn't exist, needs alternative implementation
            return null;
        }

        // 1.20.1: getTextureLocation() takes Entity, not LivingEntityRenderState
        return new DefaultEntityVariantData(type, ((LivingEntityRenderer) renderer).getTextureLocation(entity), secondaryTexture, size, addBorder);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static EntityVariantData createSimpleVariantData(Entity entity, EntityRenderer renderer, int size, boolean addBorder) {
        if (renderer instanceof EnderDragonRenderer) {
            // TODO: 1.20.1 Port - AccessorEnderDragonRenderer doesn't exist, needs alternative implementation
            return null;
        }

        // 1.20.1: getTextureLocation() takes Entity, not LivingEntityRenderState
        return new DefaultEntityVariantData(entity.getType(), ((LivingEntityRenderer) renderer).getTextureLocation(entity), null, size, addBorder);
    }

}
