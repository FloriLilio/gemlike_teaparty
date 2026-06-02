package com.lyuurain.teaparty.config;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class ConfigValues {
    public static boolean isDimensionListed(ResourceKey<Level> dimension, String[] dimensions) {
        ResourceLocation dimensionId = dimension.location();

        for (String configuredDimension : dimensions) {
            ResourceLocation configuredId = ResourceLocation.tryParse(configuredDimension);

            if (dimensionId.equals(configuredId)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isEntityListed(EntityType<?> entityType, String[] entityTypes) {
        ResourceLocation entityId = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);

        for (String configuredEntity : entityTypes) {
            ResourceLocation configuredId = ResourceLocation.tryParse(configuredEntity);

            if (entityId.equals(configuredId)) {
                return true;
            }
        }

        return false;
    }
}
