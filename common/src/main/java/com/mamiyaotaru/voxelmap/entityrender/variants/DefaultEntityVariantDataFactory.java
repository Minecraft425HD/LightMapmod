package com.mamiyaotaru.voxelmap.entityrender.variants;

import com.mamiyaotaru.voxelmap.entityrender.EntityVariantData;
import com.mamiyaotaru.voxelmap.entityrender.EntityVariantDataFactory;
// TODO: 1.20.1 Port - AccessorEnderDragonRenderer is disabled
// import com.mamiyaotaru.voxelmap.mixins.AccessorEnderDragonRenderer;
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
            return new DefaultEntityVariantData(type, AccessorEnderDragonRenderer.getTextureLocation(), secondaryTexture, size, addBorder);
        }

        // 1.20.1: getTextureLocation() takes Entity, not LivingEntityRenderState
        return new DefaultEntityVariantData(type, ((LivingEntityRenderer) renderer).getTextureLocation(entity), secondaryTexture, size, addBorder);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static EntityVariantData createSimpleVariantData(Entity entity, EntityRenderer renderer, int size, boolean addBorder) {
        if (renderer instanceof EnderDragonRenderer) {
            return new DefaultEntityVariantData(entity.getType(), AccessorEnderDragonRenderer.getTextureLocation(), null, size, addBorder);
        }

        // 1.20.1: getTextureLocation() takes Entity, not LivingEntityRenderState
        return new DefaultEntityVariantData(entity.getType(), ((LivingEntityRenderer) renderer).getTextureLocation(entity), null, size, addBorder);
    }

}
