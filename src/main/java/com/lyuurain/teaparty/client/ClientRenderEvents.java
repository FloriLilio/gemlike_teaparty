package com.lyuurain.teaparty.client;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public class ClientRenderEvents {
    public static void onAddLayers(EntityRenderersEvent.AddLayers event) {
        for (EntityType<?> entityType : event.getEntityTypes()) {
            addLayer(event.getRenderer(entityType));
        }

        for (var skin : event.getSkins()) {
            addPlayerLayer(event.getSkin(skin));
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <T extends Entity> void addLayer(EntityRenderer<T> renderer) {
        if (renderer instanceof LivingEntityRenderer livingRenderer) {
            livingRenderer.addLayer(new FrozenTintLayer(livingRenderer));
            livingRenderer.addLayer(new FrozenIceBlockLayer(livingRenderer));
        }
    }

    private static void addPlayerLayer(PlayerRenderer renderer) {
        addPlayerLayerTyped(renderer);
    }

    private static void addPlayerLayerTyped(LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer) {
        renderer.addLayer(new FrozenTintLayer<>(renderer));
        renderer.addLayer(new FrozenIceBlockLayer<>(renderer));
    }
}
