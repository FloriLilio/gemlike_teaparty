package com.lyuurain.teaparty.mixin;

import com.lyuurain.teaparty.config.ModConfig;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {
    @Inject(method = "renderTextureOverlay", at = @At("HEAD"), cancellable = true)
    private void gemlikeTeaParty$hideFrozenFirstPersonOverlay(GuiGraphics guiGraphics, ResourceLocation shaderLocation, float alpha, CallbackInfo callbackInfo) {
        if (!ModConfig.CLIENT.showFrozenFirstPersonOverlay && shaderLocation.getPath().equals("textures/misc/powder_snow_outline.png")) {
            callbackInfo.cancel();
        }
    }
}
