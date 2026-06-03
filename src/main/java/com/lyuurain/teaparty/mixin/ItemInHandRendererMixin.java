package com.lyuurain.teaparty.mixin;

import com.lyuurain.teaparty.item.MixingCupItem;
import com.lyuurain.teaparty.registry.ModDataComponents;
import com.lyuurain.teaparty.registry.ModItems;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {

    @Inject(method = "renderArmWithItem", at = @At("HEAD"))
    private void gemlikeTeaParty$renderCustomAnimations(
            AbstractClientPlayer player, float partialTicks, float interpolatedPitch,
            InteractionHand hand, float swingProgress, ItemStack stack, float equipProgress,
            PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, CallbackInfo ci
    ) {
        if (player.isUsingItem() && player.getUseItem().getItem() instanceof MixingCupItem) {
            ItemStack cupStack = player.getUseItem();
            boolean opened = cupStack.getOrDefault(ModDataComponents.OPENED.get(), false);

            if (opened) {
                boolean hasStirrer = player.getItemInHand(InteractionHand.OFF_HAND).is(ModItems.STIRRER.get());
                if (hasStirrer) {
                    float ticks = player.getTicksUsingItem() + partialTicks;
                    float progress = Math.min(1.0F, ticks / 10.0F);
                    float yOffset = progress * 2.0F;
                    poseStack.translate(0.0, -yOffset, 0.0);
                }
            } else {
                InteractionHand usedHand = player.getUsedItemHand();
                if (hand == usedHand) {
                    float ticks = player.getTicksUsingItem() + partialTicks;
                    boolean isRightHand = (hand == InteractionHand.MAIN_HAND && player.getMainArm() == HumanoidArm.RIGHT)
                            || (hand == InteractionHand.OFF_HAND && player.getMainArm() == HumanoidArm.LEFT);

                    float xOffset = isRightHand ? -0.25F : 0.25F;
                    poseStack.translate(xOffset, 0.15F, -0.2F);

                    float shakeY = Mth.sin(ticks * 1.8F) * 0.15F;
                    float shakeX = Mth.cos(ticks * 1.2F) * 0.03F;
                    poseStack.translate(shakeX, shakeY, 0.0F);

                    poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-15.0F));
                    poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(isRightHand ? 10.0F : -10.0F));
                }
            }
        }
    }
}
