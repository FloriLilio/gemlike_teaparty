package com.lyuurain.teaparty.effect;

import com.lyuurain.teaparty.registry.ModEffects;
import com.lyuurain.teaparty.registry.ModSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class PerfectFrozenEffect extends GelidEffect {
    public PerfectFrozenEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x99EEFF);
    }

    @Override
    public void onEffectStarted(LivingEntity livingEntity, int amplifier) {
        livingEntity.removeEffect(ModEffects.GELID);
        super.onEffectStarted(livingEntity, amplifier);
        playBakaSound(livingEntity);
    }

    public static void playBakaSound(LivingEntity livingEntity) {
        if (!livingEntity.level().isClientSide()) {
            livingEntity.level().playSound(null, livingEntity.blockPosition(), ModSounds.BAKA.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        }
    }
}
