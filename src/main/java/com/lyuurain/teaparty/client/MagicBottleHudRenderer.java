package com.lyuurain.teaparty.client;

import com.lyuurain.teaparty.GemlikeTeaParty;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class MagicBottleHudRenderer implements LayeredDraw.Layer {
    private static final ResourceLocation ICON = ResourceLocation.fromNamespaceAndPath(GemlikeTeaParty.MODID, "textures/item/magic_bottle.png");

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.options.hideGui || minecraft.player == null) {
            return;
        }

        ClientMagicBottleCache.tick();
        int state = ClientMagicBottleCache.animState;
        if (state == 0) {
            return;
        }

        long now = System.currentTimeMillis();
        long elapsed = now - ClientMagicBottleCache.stateStartTime;
        float alpha;
        float offsetY;

        if (state == 1) {
            float progress = Math.min(1.0F, elapsed / 300.0F);
            alpha = progress;
            offsetY = (1.0F - progress) * 20.0F;
        } else if (state == 2) {
            alpha = 1.0F;
            offsetY = 0.0F;
        } else {
            float progress = Math.min(1.0F, elapsed / 300.0F);
            alpha = 1.0F - progress;
            offsetY = progress * 20.0F;
        }

        Font font = minecraft.font;
        String countStr = String.valueOf(ClientMagicBottleCache.count);
        int textWidth = font.width(countStr);

        int width = guiGraphics.guiWidth();
        int height = guiGraphics.guiHeight();
        String position = com.lyuurain.teaparty.config.ModConfig.CLIENT.magicBottleHudPosition;
        int x;
        float y;

        if ("right".equalsIgnoreCase(position)) {
            x = width / 2 + 91 + 6;
            y = height - 20 + offsetY;
        } else if ("top".equalsIgnoreCase(position)) {
            int totalWidth = 12 + textWidth;
            x = width / 2 - totalWidth / 2;
            y = height - 65 + offsetY;
        } else {
            int rightBoundary = width / 2 - 91 - 6;
            x = rightBoundary - 12 - textWidth;
            y = height - 20 + offsetY;
        }

        RenderSystem.enableBlend();
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, alpha);
        guiGraphics.blit(ICON, x, (int) y, 0, 0, 16, 16, 16, 16);
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);

        float scale = 1.0F;
        long elapsedScale = now - ClientMagicBottleCache.lastChangeTime;
        if (elapsedScale < 200) {
            scale = 1.0F + 0.3F * Mth.sin((elapsedScale / 200.0F) * (float) Math.PI);
        }

        float textX = x + 12;
        float textY = y + 10;

        int alphaInt = (int) (alpha * 255.0F);
        if (alphaInt > 0) {
            int colorVal = (ClientMagicBottleCache.count == 0) ? 0xFF5555 : 0xFFFFFF;
            int color = (alphaInt << 24) | colorVal;
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(textX + textWidth / 2.0F, textY + 4.0F, 0.0F);
            guiGraphics.pose().scale(scale, scale, 1.0F);
            guiGraphics.pose().translate(-(textX + textWidth / 2.0F), -(textY + 4.0F), 0.0F);
            guiGraphics.drawString(font, countStr, (int) textX, (int) textY, color, true);
            guiGraphics.pose().popPose();
        }
        RenderSystem.disableBlend();
    }
}
