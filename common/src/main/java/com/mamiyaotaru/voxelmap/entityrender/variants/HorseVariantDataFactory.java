package com.mamiyaotaru.voxelmap.entityrender.variants;

import com.google.common.collect.Maps;
import com.mamiyaotaru.voxelmap.entityrender.EntityVariantData;
import java.util.Map;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
// EntityRenderState doesn't exist in 1.20.1
// LivingEntityRenderState doesn't exist in 1.20.1
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.equine.Horse;
import net.minecraft.world.entity.animal.equine.Markings;

public class HorseVariantDataFactory extends DefaultEntityVariantDataFactory {
    private static final ResourceLocation INVISIBLE_TEXTURE = new ResourceLocation("minecraft", "invisible");
    private static final Map<Markings, ResourceLocation> LOCATION_BY_MARKINGS = Maps.newEnumMap(
            Map.of(
                    Markings.NONE,
                    INVISIBLE_TEXTURE,
                    Markings.WHITE,
                    new ResourceLocation("minecraft", "textures/entity/horse/horse_markings_white.png"),
                    Markings.WHITE_FIELD,
                    new ResourceLocation("minecraft", "textures/entity/horse/horse_markings_whitefield.png"),
                    Markings.WHITE_DOTS,
                    new ResourceLocation("minecraft", "textures/entity/horse/horse_markings_whitedots.png"),
                    Markings.BLACK_DOTS,
                    new ResourceLocation("minecraft", "textures/entity/horse/horse_markings_blackdots.png")));

    public HorseVariantDataFactory(EntityType<?> type) {
        super(type);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public EntityVariantData createVariantData(Entity entity, EntityRenderer renderer, int size, boolean addBorder) {
        Horse horse = (Horse) entity;
        Markings markings = horse.getMarkings();
        ResourceLocation secondaryTexture = LOCATION_BY_MARKINGS.get(markings);
        // 1.20.1: getTextureLocation() takes Entity, not LivingEntityRenderState
        return new DefaultEntityVariantData(getType(), ((LivingEntityRenderer) renderer).getTextureLocation(entity), secondaryTexture == INVISIBLE_TEXTURE ? null : secondaryTexture, size, addBorder);
    }

}
