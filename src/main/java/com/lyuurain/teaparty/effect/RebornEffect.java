package com.lyuurain.teaparty.effect;

import com.lyuurain.teaparty.config.ModConfig;
import com.lyuurain.teaparty.event.RebornEffectEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

import java.util.Set;

public class RebornEffect extends MobEffect {
    public RebornEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x6D5CA8);
    }

    @Override
    public void onEffectStarted(LivingEntity livingEntity, int amplifier) {
        if (livingEntity.level() instanceof ServerLevel serverLevel) {
            livingEntity.teleportTo(serverLevel, livingEntity.getX(), ModConfig.COMMON.rebornTeleportHeight, livingEntity.getZ(), Set.of(), livingEntity.getYRot(), livingEntity.getXRot());
            livingEntity.fallDistance = 0.0F;
            RebornEffectEvents.startLandingCheckGrace(livingEntity);
        }
    }
}
