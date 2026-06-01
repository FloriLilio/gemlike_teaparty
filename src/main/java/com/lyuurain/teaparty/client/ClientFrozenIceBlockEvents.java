package com.lyuurain.teaparty.client;

import com.lyuurain.teaparty.registry.ModEffects;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import net.neoforged.neoforge.client.model.data.ModelData;

public class ClientFrozenIceBlockEvents {
    private static final BlockState ICE_BLOCK = Blocks.ICE.defaultBlockState();

    @SubscribeEvent
    public static void onRenderLivingPost(RenderLivingEvent.Post<?, ?> event) {
        if (event.getEntity().hasEffect(ModEffects.FROZEN) || event.getEntity().getTicksFrozen() >= event.getEntity().getTicksRequiredToFreeze()) {
            renderIceBlock(event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight());
        }
    }

    private static void renderIceBlock(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(-0.5D, 0.0D, -0.5D);
        poseStack.scale(1.0F, 0.35F, 1.0F);
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(ICE_BLOCK, poseStack, bufferSource, packedLight, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, ItemBlockRenderTypes.getRenderType(ICE_BLOCK, false));
        poseStack.popPose();
    }
}
