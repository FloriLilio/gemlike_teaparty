package com.lyuurain.teaparty.mixin;

import com.lyuurain.teaparty.registry.ModEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(method = "travel", at = @At("HEAD"), cancellable = true)
    private void gemlikeTeaParty$stopFrozenTravel(Vec3 travelVector, CallbackInfo callbackInfo) {
        LivingEntity livingEntity = (LivingEntity) (Object) this;

        if (livingEntity.hasEffect(ModEffects.FROZEN)) {
            Vec3 movement = livingEntity.getDeltaMovement();
            livingEntity.setDeltaMovement(0.0D, movement.y, 0.0D);
            callbackInfo.cancel();
        }
    }
}
