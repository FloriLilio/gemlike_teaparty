package com.lyuurain.teaparty.effect;

import com.lyuurain.teaparty.registry.ModEffects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.EffectCure;
import net.neoforged.neoforge.common.EffectCures;

import java.util.List;
import java.util.Set;

public class GelidEffect extends MobEffect {
    private static final double FREEZE_RADIUS = 9.0D;
    private static final double FREEZE_RADIUS_SQR = FREEZE_RADIUS * FREEZE_RADIUS;
    private static final int FROZEN_DURATION = 180;

    public GelidEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x66CCFF);
    }

    public static boolean canBeFrozen(LivingEntity livingEntity) {
        EntityType<?> entityType = livingEntity.getType();
        return entityType != EntityType.STRAY && entityType != EntityType.POLAR_BEAR && entityType != EntityType.SNOW_GOLEM;
    }

    @Override
    public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance effectInstance) {
        super.fillEffectCures(cures, effectInstance);
        cures.remove(EffectCures.MILK);
    }

    @Override
    public void onEffectStarted(LivingEntity livingEntity, int amplifier) {
        if (livingEntity.level() instanceof ServerLevel serverLevel) {
            AABB area = livingEntity.getBoundingBox().inflate(FREEZE_RADIUS);
            List<LivingEntity> entities = serverLevel.getEntities(EntityTypeTest.forClass(LivingEntity.class), area, entity -> shouldFreeze(livingEntity, entity));

            for (LivingEntity entity : entities) {
                entity.addEffect(new MobEffectInstance(ModEffects.FROZEN, FROZEN_DURATION, 0, false, true, true));
            }
        }
    }

    private boolean shouldFreeze(LivingEntity source, LivingEntity target) {
        if (target == source || !target.isAlive() || !canBeFrozen(target)) {
            return false;
        }

        double xDistance = target.getX() - source.getX();
        double zDistance = target.getZ() - source.getZ();
        return xDistance * xDistance + zDistance * zDistance <= FREEZE_RADIUS_SQR;
    }
}
