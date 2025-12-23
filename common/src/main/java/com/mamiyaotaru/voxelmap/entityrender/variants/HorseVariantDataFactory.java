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
// 1.20.1: Horse moved from animal.equine to animal.horse package
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.animal.horse.Variant;

public class HorseVariantDataFactory extends DefaultEntityVariantDataFactory {
    private static final ResourceLocation INVISIBLE_TEXTURE = new ResourceLocation("minecraft", "invisible");
    // 1.20.1: Markings renamed to Variant in 1.20.1
    private static final Map<Variant, ResourceLocation> LOCATION_BY_VARIANT = Maps.newEnumMap(
            Map.of(
                    Variant.values()[0],
                    INVISIBLE_TEXTURE,
                    Variant.values()[1],
                    new ResourceLocation("minecraft", "textures/entity/horse/horse_markings_white.png"),
                    Variant.values()[2],
                    new ResourceLocation("minecraft", "textures/entity/horse/horse_markings_whitefield.png"),
                    Variant.values()[3],
                    new ResourceLocation("minecraft", "textures/entity/horse/horse_markings_whitedots.png"),
                    Variant.values()[4],
                    new ResourceLocation("minecraft", "textures/entity/horse/horse_markings_blackdots.png")));

    public HorseVariantDataFactory(EntityType<?> type) {
        super(type);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public EntityVariantData createVariantData(Entity entity, EntityRenderer renderer, int size, boolean addBorder) {
        Horse horse = (Horse) entity;
        // 1.20.1: getMarkings() renamed to getVariant()
        Variant variant = horse.getVariant();
        ResourceLocation secondaryTexture = LOCATION_BY_VARIANT.get(variant);
        // 1.20.1: getTextureLocation() takes Entity, not LivingEntityRenderState
        return new DefaultEntityVariantData(getType(), ((LivingEntityRenderer) renderer).getTextureLocation(entity), secondaryTexture == INVISIBLE_TEXTURE ? null : secondaryTexture, size, addBorder);
    }

}
