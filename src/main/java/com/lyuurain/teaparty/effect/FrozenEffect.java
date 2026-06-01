package com.lyuurain.teaparty.effect;

import com.lyuurain.teaparty.registry.ModSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.EffectCure;
import net.neoforged.neoforge.common.EffectCures;

import java.util.Set;

public class FrozenEffect extends MobEffect {
    public FrozenEffect() {
        super(MobEffectCategory.HARMFUL, 0x8AD8FF);
    }

    @Override
    public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance effectInstance) {
        super.fillEffectCures(cures, effectInstance);
        cures.remove(EffectCures.MILK);
    }

    @Override
    public void onEffectStarted(LivingEntity livingEntity, int amplifier) {
        if (!livingEntity.level().isClientSide()) {
            livingEntity.level().playSound(null, livingEntity.blockPosition(), ModSounds.FROZEN.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        }
    }

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        Vec3 movement = livingEntity.getDeltaMovement();
        livingEntity.setDeltaMovement(0.0D, movement.y, 0.0D);
        livingEntity.setSprinting(false);
        livingEntity.stopUsingItem();
        int freezeTicks = livingEntity.getTicksRequiredToFreeze();
        livingEntity.setTicksFrozen(freezeTicks);
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}
