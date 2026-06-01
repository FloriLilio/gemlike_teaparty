package com.lyuurain.teaparty.client;

import com.lyuurain.teaparty.registry.ModEffects;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;

public class FrozenTintLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    private static final int FROZEN_TINT = 0xDD55CCFF;

    public FrozenTintLayer(RenderLayerParent<T, M> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, T livingEntity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        if (livingEntity.hasEffect(ModEffects.FROZEN)) {
            getParentModel().renderToBuffer(poseStack, bufferSource.getBuffer(RenderType.entityTranslucent(getTextureLocation(livingEntity))), packedLight, OverlayTexture.NO_OVERLAY, FROZEN_TINT);
        }
    }
}
