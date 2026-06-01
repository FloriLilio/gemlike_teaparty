package com.lyuurain.teaparty.client;

import com.lyuurain.teaparty.registry.ModEffects;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderLivingEvent;

public class ClientFrozenRenderEvents {
    private static final int FROZEN_TINT = 0xDD55CCFF;

    @SubscribeEvent
    public static void onRenderLivingPost(RenderLivingEvent.Post<?, ?> event) {
        renderTint(event);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <T extends LivingEntity, M extends EntityModel<T>> void renderTint(RenderLivingEvent.Post<?, ?> event) {
        T livingEntity = (T) event.getEntity();

        if (livingEntity.hasEffect(ModEffects.FROZEN)) {
            LivingEntityRenderer<T, M> renderer = (LivingEntityRenderer<T, M>) event.getRenderer();
            PoseStack poseStack = event.getPoseStack();
            MultiBufferSource bufferSource = event.getMultiBufferSource();
            renderer.getModel().renderToBuffer(poseStack, bufferSource.getBuffer(RenderType.entityTranslucent(renderer.getTextureLocation(livingEntity))), event.getPackedLight(), OverlayTexture.NO_OVERLAY, FROZEN_TINT);
        }
    }
}
