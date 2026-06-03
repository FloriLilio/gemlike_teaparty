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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import com.mojang.math.Axis;
import java.util.List;
import java.util.ArrayList;

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

        // Render stored items
        renderStoredItems(blockEntity.getItems(), blockEntity, partialTick, poseStack, bufferSource, combinedLight, combinedOverlay);

        poseStack.popPose();
    }

    private void renderStoredItems(net.minecraft.core.NonNullList<ItemStack> items, BlenderBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        List<ItemStack> nonEmptyItems = new ArrayList<>();
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                nonEmptyItems.add(stack);
            }
        }
        if (nonEmptyItems.isEmpty()) {
            return;
        }

        net.minecraft.client.renderer.entity.ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        boolean isPowered = blockEntity.getBlockState().hasProperty(BlenderBlock.POWERED) && blockEntity.getBlockState().getValue(BlenderBlock.POWERED);

        float gameTime = 0.0F;
        if (blockEntity.getLevel() != null) {
            gameTime = blockEntity.getLevel().getGameTime() + partialTick;
        }

        float[][] baseOffsets = {
            { 0.38F, 0.38F },
            { 0.62F, 0.38F },
            { 0.38F, 0.62F },
            { 0.62F, 0.62F }
        };

        for (int i = 0; i < nonEmptyItems.size(); i++) {
            ItemStack stack = nonEmptyItems.get(i);
            float baseX = baseOffsets[i][0];
            float baseZ = baseOffsets[i][1];
            float baseY = 0.9F;

            float posX = baseX;
            float posY = baseY;
            float posZ = baseZ;

            float rotX = 0.0F;
            float rotY = 0.0F;
            float rotZ = 0.0F;

            if (isPowered) {
                float t = gameTime * 0.8F + i * 1.5F;
                posY = baseY + Math.abs(Mth.sin(t)) * 0.7F;
                posX = baseX + Mth.sin(t * 0.7F) * 0.08F;
                posZ = baseZ + Mth.cos(t * 0.6F) * 0.08F;

                posX = Mth.clamp(posX, 0.22F, 0.78F);
                posZ = Mth.clamp(posZ, 0.22F, 0.78F);
                posY = Mth.clamp(posY, 0.85F, 1.85F);

                rotY = gameTime * 18.0F + i * 45.0F;
                rotX = Mth.sin(gameTime * 0.5F + i) * 35.0F;
                rotZ = Mth.cos(gameTime * 0.4F + i) * 35.0F;
            } else {
                rotY = i * 45.0F;
            }

            int count = stack.getCount();
            int renderCount = getRenderCount(count);

            for (int j = 0; j < renderCount; j++) {
                poseStack.pushPose();

                float xOffset = 0.0F;
                float yOffset = j * 0.04F;
                float zOffset = 0.0F;

                if (j > 0 && !isPowered) {
                    float angle = j * 120.0F;
                    xOffset = Mth.sin(angle * (float)Math.PI / 180.0F) * 0.02F;
                    zOffset = Mth.cos(angle * (float)Math.PI / 180.0F) * 0.02F;
                }

                poseStack.translate(posX + xOffset, posY + yOffset, posZ + zOffset);

                poseStack.mulPose(Axis.YP.rotationDegrees(rotY + (j * 15.0F)));
                if (rotX != 0.0F || rotZ != 0.0F) {
                    poseStack.mulPose(Axis.XP.rotationDegrees(rotX));
                    poseStack.mulPose(Axis.ZP.rotationDegrees(rotZ));
                } else if (!isPowered) {
                    poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
                }

                poseStack.scale(0.35F, 0.35F, 0.35F);

                itemRenderer.renderStatic(
                    stack,
                    ItemDisplayContext.GROUND,
                    combinedLight,
                    combinedOverlay,
                    poseStack,
                    bufferSource,
                    blockEntity.getLevel(),
                    i + j * 31
                );

                poseStack.popPose();
            }
        }
    }

    private int getRenderCount(int count) {
        if (count <= 1) return 1;
        if (count <= 16) return 2;
        if (count <= 32) return 3;
        if (count <= 48) return 4;
        return 5;
    }

    private void addVertex(VertexConsumer consumer, PoseStack.Pose poseEntry, float x, float y, float z, float u, float v, int r, int g, int b, int a, int light, float nx, float ny, float nz) {
        consumer.addVertex(poseEntry.pose(), x, y, z)
                .setColor(r, g, b, a)
                .setUv(u, v)
                .setUv2(light & 0xFFFF, (light >> 16) & 0xFFFF)
                .setNormal(poseEntry, nx, ny, nz);
    }
}