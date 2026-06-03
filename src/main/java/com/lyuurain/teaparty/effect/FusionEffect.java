package com.lyuurain.teaparty.effect;

import com.lyuurain.teaparty.config.FusionWarningSoundMode;
import com.lyuurain.teaparty.config.ModConfig;
import com.lyuurain.teaparty.registry.ModSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class FusionEffect extends MobEffect {
    private static final int WARNING_SOUND_INTERVAL = 21;

    public FusionEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFF6A00);
    }

    @Override
    public void onEffectStarted(LivingEntity livingEntity, int amplifier) {
        if (getWarningSoundMode() != FusionWarningSoundMode.OFF) {
            playWarningSound(livingEntity);
        }
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        FusionWarningSoundMode mode = getWarningSoundMode();
        int elapsedTicks = ModConfig.COMMON.undergroundSunFusionDuration - duration;
        return mode != FusionWarningSoundMode.OFF && (elapsedTicks == WARNING_SOUND_INTERVAL || mode == FusionWarningSoundMode.LOOP && elapsedTicks > WARNING_SOUND_INTERVAL && elapsedTicks % WARNING_SOUND_INTERVAL == 0);
    }

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        playWarningSound(livingEntity);
        return true;
    }

    private FusionWarningSoundMode getWarningSoundMode() {
        return FusionWarningSoundMode.fromConfig(ModConfig.COMMON.fusionWarningSoundMode);
    }

    private void playWarningSound(LivingEntity livingEntity) {
        if (!livingEntity.level().isClientSide()) {
            livingEntity.level().playSound(null, livingEntity.blockPosition(), ModSounds.NUCLEAR_WARNING.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        }
    }
}
