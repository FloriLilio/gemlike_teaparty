package com.lyuurain.teaparty.client;

import com.lyuurain.teaparty.block.entity.BlenderBlockEntity;
import com.lyuurain.teaparty.recipe.LiquidDefinition;
import com.lyuurain.teaparty.recipe.LiquidManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class BlenderBlockEntityRenderer implements BlockEntityRenderer<BlenderBlockEntity> {

    public BlenderBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(BlenderBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        ResourceLocation liquidId = blockEntity.getLiquidId();
        if (liquidId == null || blockEntity.getLiquidCount() <= 0) {
            return;
        }

        LiquidDefinition liquidDef = LiquidManager.INSTANCE.getLiquids().get(liquidId);
        if (liquidDef == null) {
            return;
        }

        float prevHeight = blockEntity.getPrevLiquidHeight();
        float currentHeight = blockEntity.getLiquidHeight();
        float lerpHeight = Mth.lerp(partialTick, prevHeight, currentHeight);
        if (lerpHeight <= 0.01F) {
            return;
        }

        ResourceLocation textureLoc = liquidDef.texture();
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(textureLoc);

        int color = liquidDef.getColorValue();
        int a = (color >> 24) & 0xFF;
        if (a == 0) a = 255;
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;

        VertexConsumer consumer = bufferSource.getBuffer(RenderType.translucent());

        float minX = 0.188F;
        float maxX = 0.812F;
        float minZ = 0.188F;
        float maxZ = 0.812F;
        float yBottom = 0.8125F;
        float yTop = yBottom + (lerpHeight / 16.0F);

        float u0 = sprite.getU0();
        float u1 = sprite.getU1();
        float v0 = sprite.getV0();
        float v1 = sprite.getV1();

        PoseStack.Pose poseEntry = poseStack.last();

        // 1. Top face (Up, normal [0, 1, 0])
        addVertex(consumer, poseEntry, minX, yTop, maxZ, u0, v1, r, g, b, a, combinedLight, 0.0F, 1.0F, 0.0F);
        addVertex(consumer, poseEntry, maxX, yTop, maxZ, u1, v1, r, g, b, a, combinedLight, 0.0F, 1.0F, 0.0F);
        addVertex(consumer, poseEntry, maxX, yTop, minZ, u1, v0, r, g, b, a, combinedLight, 0.0F, 1.0F, 0.0F);
        addVertex(consumer, poseEntry, minX, yTop, minZ, u0, v0, r, g, b, a, combinedLight, 0.0F, 1.0F, 0.0F);

        // 2. Top face inner (Down, normal [0, -1, 0])
        addVertex(consumer, poseEntry, minX, yTop, minZ, u0, v0, r, g, b, a, combinedLight, 0.0F, -1.0F, 0.0F);
        addVertex(consumer, poseEntry, maxX, yTop, minZ, u1, v0, r, g, b, a, combinedLight, 0.0F, -1.0F, 0.0F);
        addVertex(consumer, poseEntry, maxX, yTop, maxZ, u1, v1, r, g, b, a, combinedLight, 0.0F, -1.0F, 0.0F);
        addVertex(consumer, poseEntry, minX, yTop, maxZ, u0, v1, r, g, b, a, combinedLight, 0.0F, -1.0F, 0.0F);

        // 3. North face (normal [0, 0, -1])
        addVertex(consumer, poseEntry, minX, yBottom, minZ, u0, v1, r, g, b, a, combinedLight, 0.0F, 0.0F, -1.0F);
        addVertex(consumer, poseEntry, minX, yTop, minZ, u0, v0, r, g, b, a, combinedLight, 0.0F, 0.0F, -1.0F);
        addVertex(consumer, poseEntry, maxX, yTop, minZ, u1, v0, r, g, b, a, combinedLight, 0.0F, 0.0F, -1.0F);
        addVertex(consumer, poseEntry, maxX, yBottom, minZ, u1, v1, r, g, b, a, combinedLight, 0.0F, 0.0F, -1.0F);

        // 4. South face (normal [0, 0, 1])
        addVertex(consumer, poseEntry, maxX, yBottom, maxZ, u0, v1, r, g, b, a, combinedLight, 0.0F, 0.0F, 1.0F);
        addVertex(consumer, poseEntry, maxX, yTop, maxZ, u0, v0, r, g, b, a, combinedLight, 0.0F, 0.0F, 1.0F);
        addVertex(consumer, poseEntry, minX, yTop, maxZ, u1, v0, r, g, b, a, combinedLight, 0.0F, 0.0F, 1.0F);
        addVertex(consumer, poseEntry, minX, yBottom, maxZ, u1, v1, r, g, b, a, combinedLight, 0.0F, 0.0F, 1.0F);

        // 5. West face (normal [-1, 0, 0])
        addVertex(consumer, poseEntry, minX, yBottom, maxZ, u0, v1, r, g, b, a, combinedLight, -1.0F, 0.0F, 0.0F);
        addVertex(consumer, poseEntry, minX, yTop, maxZ, u0, v0, r, g, b, a, combinedLight, -1.0F, 0.0F, 0.0F);
        addVertex(consumer, poseEntry, minX, yTop, minZ, u1, v0, r, g, b, a, combinedLight, -1.0F, 0.0F, 0.0F);
        addVertex(consumer, poseEntry, minX, yBottom, minZ, u1, v1, r, g, b, a, combinedLight, -1.0F, 0.0F, 0.0F);

        // 6. East face (normal [1, 0, 0])
        addVertex(consumer, poseEntry, maxX, yBottom, minZ, u0, v1, r, g, b, a, combinedLight, 1.0F, 0.0F, 0.0F);
        addVertex(consumer, poseEntry, maxX, yTop, minZ, u0, v0, r, g, b, a, combinedLight, 1.0F, 0.0F, 0.0F);
        addVertex(consumer, poseEntry, maxX, yTop, maxZ, u1, v0, r, g, b, a, combinedLight, 1.0F, 0.0F, 0.0F);
        addVertex(consumer, poseEntry, maxX, yBottom, maxZ, u1, v1, r, g, b, a, combinedLight, 1.0F, 0.0F, 0.0F);
    }

    private void addVertex(VertexConsumer consumer, PoseStack.Pose poseEntry, float x, float y, float z, float u, float v, int r, int g, int b, int a, int light, float nx, float ny, float nz) {
        consumer.addVertex(poseEntry.pose(), x, y, z)
                .setColor(r, g, b, a)
                .setUv(u, v)
                .setUv2(light & 0xFFFF, (light >> 16) & 0xFFFF)
                .setNormal(poseEntry, nx, ny, nz);
    }
}