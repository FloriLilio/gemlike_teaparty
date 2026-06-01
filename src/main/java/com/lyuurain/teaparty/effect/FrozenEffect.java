package com.lyuurain.teaparty.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.EffectCure;
import net.neoforged.neoforge.common.EffectCures;

import java.util.Set;

public class FrozenEffect extends MobEffect {
    private static final int FREEZE_TICKS = 200;

    public FrozenEffect() {
        super(MobEffectCategory.HARMFUL, 0x8AD8FF);
    }

    @Override
    public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance effectInstance) {
        super.fillEffectCures(cures, effectInstance);
        cures.remove(EffectCures.MILK);
    }

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        Vec3 movement = livingEntity.getDeltaMovement();
        livingEntity.setDeltaMovement(0.0D, movement.y, 0.0D);
        livingEntity.setSprinting(false);
        livingEntity.stopUsingItem();
        livingEntity.setTicksFrozen(Math.max(livingEntity.getTicksFrozen(), FREEZE_TICKS));
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}
