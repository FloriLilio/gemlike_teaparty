package com.lyuurain.teaparty.client;

import com.lyuurain.teaparty.config.EndVisionConfigValues;
import com.lyuurain.teaparty.config.ModConfig;
import com.lyuurain.teaparty.registry.ModEffects;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.util.ArrayList;
import java.util.List;

public class EndVisionRenderEvents {
    private static final float ALPHA = 1.0F;
    private static final int BLOCK_CACHE_REFRESH_INTERVAL = 20;
    private static final int BLOCK_CACHE_MOVE_REFRESH_DISTANCE_SQR = 16;
    private static final int BLOCK_SCAN_STEPS_PER_FRAME = 4096;
    private static final List<BlockPos> TREASURE_BLOCK_CACHE = new ArrayList<>();
    private static final List<BlockPos> PENDING_TREASURE_BLOCK_CACHE = new ArrayList<>();
    private static int lastCacheRefreshTick = -BLOCK_CACHE_REFRESH_INTERVAL;
    private static BlockPos lastCacheCenter = BlockPos.ZERO;
    private static BlockPos scanningCenter = BlockPos.ZERO;
    private static int scanningRadius;
    private static int scanningIndex;
    private static boolean scanning;

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        ClientLevel level = minecraft.level;

        if (player == null || level == null || !player.hasEffect(ModEffects.END_VISION)) {
            clearTreasureBlockCache();
            return;
        }

        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
        VertexConsumer vertexConsumer = bufferSource.getBuffer(EndVisionRenderTypes.END_VISION_LINES);
        double cameraX = event.getCamera().getPosition().x();
        double cameraY = event.getCamera().getPosition().y();
        double cameraZ = event.getCamera().getPosition().z();

        int radius = EndVisionConfigValues.syncedConfig().radius();
        int color = outlineColor();
        float red = ((color >> 16) & 0xFF) / 255.0F;
        float green = ((color >> 8) & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;

        updateTreasureBlockCache(level, player.blockPosition(), player.tickCount, radius);

        poseStack.pushPose();
        poseStack.translate(-cameraX, -cameraY, -cameraZ);
        renderEntities(level, player, event.getPartialTick().getGameTimeDeltaPartialTick(false), poseStack, vertexConsumer, radius, red, green, blue);
        renderTreasureBlockCache(poseStack, vertexConsumer, red, green, blue);
        poseStack.popPose();
        bufferSource.endBatch(EndVisionRenderTypes.END_VISION_LINES);
    }

    private static void renderEntities(ClientLevel level, LocalPlayer player, float partialTick, PoseStack poseStack, VertexConsumer vertexConsumer, int radius, float red, float green, float blue) {
        if (!EndVisionConfigValues.syncedConfig().showEntities()) {
            return;
        }

        AABB area = player.getBoundingBox().inflate(radius);

        for (Entity entity : level.entitiesForRendering()) {
            if (entity instanceof LivingEntity && entity != player && area.intersects(entity.getBoundingBox()) && EndVisionConfigValues.isEntityVisible(entity.getType())) {
                double x = entity.xo + (entity.getX() - entity.xo) * partialTick;
                double y = entity.yo + (entity.getY() - entity.yo) * partialTick;
                double z = entity.zo + (entity.getZ() - entity.zo) * partialTick;
                AABB box = entity.getBoundingBox().move(x - entity.getX(), y - entity.getY(), z - entity.getZ()).inflate(0.05D);
                LevelRenderer.renderLineBox(poseStack, vertexConsumer, box, red, green, blue, ALPHA);
            }
        }
    }

    private static void updateTreasureBlockCache(ClientLevel level, BlockPos center, int tickCount, int radius) {
        int xDistance = center.getX() - lastCacheCenter.getX();
        int yDistance = center.getY() - lastCacheCenter.getY();
        int zDistance = center.getZ() - lastCacheCenter.getZ();
        int distanceSqr = xDistance * xDistance + yDistance * yDistance + zDistance * zDistance;

        if (!scanning && (tickCount - lastCacheRefreshTick >= BLOCK_CACHE_REFRESH_INTERVAL || distanceSqr >= BLOCK_CACHE_MOVE_REFRESH_DISTANCE_SQR || radius != scanningRadius)) {
            startTreasureBlockScan(center, tickCount, radius);
        }

        if (scanning) {
            continueTreasureBlockScan(level);
        }
    }

    private static void startTreasureBlockScan(BlockPos center, int tickCount, int radius) {
        lastCacheRefreshTick = tickCount;
        lastCacheCenter = center.immutable();
        scanningCenter = center.immutable();
        scanningRadius = radius;
        scanningIndex = 0;
        scanning = true;
        PENDING_TREASURE_BLOCK_CACHE.clear();
    }

    private static void continueTreasureBlockScan(ClientLevel level) {
        int diameter = scanningRadius * 2 + 1;
        long total = (long) diameter * diameter * diameter;
        int limit = (int) Math.min(scanningIndex + BLOCK_SCAN_STEPS_PER_FRAME, total);
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        while (scanningIndex < limit) {
            int index = scanningIndex;
            int x = index / (diameter * diameter) - scanningRadius;
            int y = index / diameter % diameter - scanningRadius;
            int z = index % diameter - scanningRadius;

            mutablePos.set(scanningCenter.getX() + x, scanningCenter.getY() + y, scanningCenter.getZ() + z);
            if (level.isLoaded(mutablePos)) {
                BlockState blockState = level.getBlockState(mutablePos);

                if (EndVisionConfigValues.isBlockVisible(blockState)) {
                    PENDING_TREASURE_BLOCK_CACHE.add(mutablePos.immutable());
                }
            }

            scanningIndex++;
        }

        if (scanningIndex >= total) {
            TREASURE_BLOCK_CACHE.clear();
            TREASURE_BLOCK_CACHE.addAll(PENDING_TREASURE_BLOCK_CACHE);
            PENDING_TREASURE_BLOCK_CACHE.clear();
            scanning = false;
        }
    }

    private static void renderTreasureBlockCache(PoseStack poseStack, VertexConsumer vertexConsumer, float red, float green, float blue) {
        for (BlockPos pos : TREASURE_BLOCK_CACHE) {
            LevelRenderer.renderLineBox(poseStack, vertexConsumer, new AABB(pos).inflate(0.01D), red, green, blue, ALPHA);
        }
    }

    private static void clearTreasureBlockCache() {
        TREASURE_BLOCK_CACHE.clear();
        PENDING_TREASURE_BLOCK_CACHE.clear();
        lastCacheRefreshTick = -BLOCK_CACHE_REFRESH_INTERVAL;
        lastCacheCenter = BlockPos.ZERO;
        scanningCenter = BlockPos.ZERO;
        scanningRadius = 0;
        scanningIndex = 0;
        scanning = false;
    }

    private static int outlineColor() {
        try {
            String color = ModConfig.CLIENT.endVisionOutlineColor.startsWith("#") ? ModConfig.CLIENT.endVisionOutlineColor.substring(1) : ModConfig.CLIENT.endVisionOutlineColor;
            return Integer.parseInt(color, 16) & 0xFFFFFF;
        } catch (NumberFormatException ignored) {
            return 0xFCC9FF;
        }
    }
}
