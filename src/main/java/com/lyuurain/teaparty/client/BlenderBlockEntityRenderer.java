package com.lyuurain.teaparty.client;

import com.lyuurain.teaparty.block.BlenderBlock;
import com.lyuurain.teaparty.block.entity.BlenderBlockEntity;
import com.lyuurain.teaparty.recipe.LiquidDefinition;
import com.lyuurain.teaparty.recipe.LiquidManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

public class BlenderBlockEntityRenderer implements BlockEntityRenderer<BlenderBlockEntity> {
    private final BlockRenderDispatcher blockRenderer;

    public BlenderBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.blockRenderer = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(BlenderBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        BlockState blockState = blockEntity.getBlockState();
        boolean isPowered = blockState.hasProperty(BlenderBlock.POWERED) && blockState.getValue(BlenderBlock.POWERED);

        poseStack.pushPose();

        // Horizontal shake if powered
        if (isPowered && blockEntity.getLevel() != null) {
            float time = (blockEntity.getLevel().getGameTime() + partialTick) * 1.5F;
            float offsetX = Mth.sin(time) * 0.025F;
            float offsetZ = Mth.cos(time * 1.3F) * 0.025F;
            poseStack.translate(offsetX, 0.0F, offsetZ);
        }

        // Render lower part using ModelBlockRenderer directly to bypass ENTITYBLOCK_ANIMATED check
        net.minecraft.client.renderer.block.ModelBlockRenderer modelRenderer = this.blockRenderer.getModelRenderer();
        net.minecraft.client.resources.model.BakedModel lowerModel = this.blockRenderer.getBlockModel(blockState);
        VertexConsumer cutoutConsumer = bufferSource.getBuffer(RenderType.cutout());

        modelRenderer.renderModel(
            poseStack.last(),
            cutoutConsumer,
            blockState,
            lowerModel,
            1.0F, 1.0F, 1.0F,
            combinedLight,
            combinedOverlay,
            net.neoforged.neoforge.client.model.data.ModelData.EMPTY,
            RenderType.cutout()
        );

        // Render upper part
        poseStack.pushPose();
        poseStack.translate(0.0D, 1.0D, 0.0D);
        BlockState upperState = blockState.setValue(BlenderBlock.HALF, DoubleBlockHalf.UPPER);
        net.minecraft.client.resources.model.BakedModel upperModel = this.blockRenderer.getBlockModel(upperState);

        modelRenderer.renderModel(
            poseStack.last(),
            cutoutConsumer,
            upperState,
            upperModel,
            1.0F, 1.0F, 1.0F,
            combinedLight,
            combinedOverlay,
            net.neoforged.neoforge.client.model.data.ModelData.EMPTY,
            RenderType.cutout()
        );
        poseStack.popPose();

        // Render liquid if present
        ResourceLocation liquidId = blockEntity.getLiquidId();
        if (liquidId != null && blockEntity.getLiquidCount() > 0) {
            LiquidDefinition liquidDef = LiquidManager.INSTANCE.getLiquids().get(liquidId);
            if (liquidDef != null) {
                float prevHeight = blockEntity.getPrevLiquidHeight();
                float currentHeight = blockEntity.getLiquidHeight();
                float lerpHeight = Mth.lerp(partialTick, prevHeight, currentHeight);
                if (lerpHeight > 0.01F) {
                    ResourceLocation textureLoc = liquidDef.texture();
                    TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(textureLoc);

                    int color = liquidDef.getColorValue();
                    int r = (color >> 16) & 0xFF;
                    int g = (color >> 8) & 0xFF;
                    int b = color & 0xFF;
                    int a = 255;

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
            }
        }

        poseStack.popPose();
    }

    private void addVertex(VertexConsumer consumer, PoseStack.Pose poseEntry, float x, float y, float z, float u, float v, int r, int g, int b, int a, int light, float nx, float ny, float nz) {
        consumer.addVertex(poseEntry.pose(), x, y, z)
                .setColor(r, g, b, a)
                .setUv(u, v)
                .setUv2(light & 0xFFFF, (light >> 16) & 0xFFFF)
                .setNormal(poseEntry, nx, ny, nz);
    }
}