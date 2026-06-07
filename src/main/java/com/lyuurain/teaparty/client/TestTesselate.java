package com.lyuurain.teaparty.client;

import com.lyuurain.teaparty.block.entity.BlenderBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.world.level.block.state.BlockState;

public class TestTesselate {
    public void test(BlockRenderDispatcher dispatcher, BlenderBlockEntity blockEntity, PoseStack poseStack, VertexConsumer vertexConsumer, int overlay) {
        BlockState blockState = blockEntity.getBlockState();
        net.minecraft.client.resources.model.BakedModel bakedModel = dispatcher.getBlockModel(blockState);
        dispatcher.getModelRenderer().tesselateBlock(
            blockEntity.getLevel(),
            bakedModel,
            blockState,
            blockEntity.getBlockPos(),
            poseStack,
            vertexConsumer,
            false,
            blockEntity.getLevel().getRandom(),
            blockState.getSeed(blockEntity.getBlockPos()),
            overlay,
            net.neoforged.neoforge.client.model.data.ModelData.EMPTY,
            null
        );
    }
}
