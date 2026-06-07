package com.lyuurain.teaparty.effect;

import com.lyuurain.teaparty.config.ConfigValues;
import com.lyuurain.teaparty.config.ModConfig;
import com.lyuurain.teaparty.registry.ModEffects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.EffectCure;
import net.neoforged.neoforge.common.EffectCures;

import java.util.List;
import java.util.Set;

public class GelidEffect extends MobEffect {
    public GelidEffect() {
        this(MobEffectCategory.BENEFICIAL, 0x66CCFF);
    }

    protected GelidEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    public static boolean canBeFrozen(LivingEntity livingEntity) {
        return !ConfigValues.isEntityListed(livingEntity.getType(), ModConfig.COMMON.frozenImmuneEntities);
    }

    @Override
    public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance effectInstance) {
        super.fillEffectCures(cures, effectInstance);
        cures.remove(EffectCures.MILK);
    }

    @Override
    public void onEffectStarted(LivingEntity livingEntity, int amplifier) {
        if (!(this instanceof PerfectFrozenEffect)) {
            if (livingEntity.hasEffect(ModEffects.PERFECT_FROZEN)) {
                livingEntity.removeEffect(ModEffects.PERFECT_FROZEN);
            }
        }

        if (livingEntity.level() instanceof ServerLevel serverLevel) {
            double radius = ModConfig.COMMON.glacierFreezeRadius;
            AABB area = livingEntity.getBoundingBox().inflate(radius);
            List<LivingEntity> entities = serverLevel.getEntities(EntityTypeTest.forClass(LivingEntity.class), area, entity -> shouldFreeze(livingEntity, entity, radius * radius));

            for (LivingEntity entity : entities) {
                entity.addEffect(new MobEffectInstance(ModEffects.FROZEN, ModConfig.COMMON.gelidFrozenDuration, 0, false, true, true));
            }
        }
    }

    private boolean shouldFreeze(LivingEntity source, LivingEntity target, double radiusSqr) {
        if (target == source || !target.isAlive() || !canBeFrozen(target)) {
            return false;
        }

        double xDistance = target.getX() - source.getX();
        double zDistance = target.getZ() - source.getZ();
        return xDistance * xDistance + zDistance * zDistance <= radiusSqr;
    }
}
