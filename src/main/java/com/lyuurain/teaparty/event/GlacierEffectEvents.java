package com.lyuurain.teaparty.event;

import com.lyuurain.teaparty.registry.ModEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GlacierEffectEvents {
    private static final int SLOWNESS_DURATION = 180;
    private static final int FROZEN_DURATION = 180;
    private static final Map<UUID, Rotation> frozenRotations = new HashMap<>();
    private static final Map<UUID, Boolean> frozenMobNoAiStates = new HashMap<>();

    @SubscribeEvent
    public static void onLivingDamagePost(LivingDamageEvent.Post event) {
        LivingEntity target = event.getEntity();
        Entity attacker = event.getSource().getEntity();

        if (attacker instanceof LivingEntity livingAttacker && livingAttacker.hasEffect(ModEffects.GELID)) {
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, SLOWNESS_DURATION, 0));
        }

        if (target.hasEffect(ModEffects.GELID) && attacker instanceof LivingEntity livingAttacker) {
            livingAttacker.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, SLOWNESS_DURATION, 0));
        }
    }

    @SubscribeEvent
    public static void onMobEffectExpired(MobEffectEvent.Expired event) {
        if (event.getEffectInstance().is(ModEffects.GELID)) {
            event.getEntity().addEffect(new MobEffectInstance(ModEffects.FROZEN, FROZEN_DURATION, 0, false, true, true));
        } else if (event.getEffectInstance().is(ModEffects.FROZEN)) {
            clearFrozenState(event.getEntity(), event.getEntity().getUUID());
            event.getEntity().addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, SLOWNESS_DURATION, 0));
        }
    }

    @SubscribeEvent
    public static void onMobEffectRemove(MobEffectEvent.Remove event) {
        if (event.getEffectInstance() != null && event.getEffectInstance().is(ModEffects.FROZEN)) {
            clearFrozenState(event.getEntity(), event.getEntity().getUUID());
        }
    }

    @SubscribeEvent
    public static void onEntityTickPost(EntityTickEvent.Post event) {
        if (event.getEntity() instanceof LivingEntity livingEntity) {
            UUID entityId = livingEntity.getUUID();

            if (!livingEntity.hasEffect(ModEffects.FROZEN)) {
                clearFrozenState(livingEntity, entityId);
                return;
            }

            Rotation rotation = frozenRotations.computeIfAbsent(entityId, id -> new Rotation(livingEntity.getYRot(), livingEntity.getXRot()));
            Vec3 movement = livingEntity.getDeltaMovement();
            livingEntity.setDeltaMovement(0.0D, movement.y, 0.0D);
            livingEntity.setYRot(rotation.yRot());
            livingEntity.setXRot(rotation.xRot());
            livingEntity.setYHeadRot(rotation.yRot());
            livingEntity.setSprinting(false);
            livingEntity.stopUsingItem();

            if (livingEntity instanceof Mob mob) {
                frozenMobNoAiStates.computeIfAbsent(entityId, id -> mob.isNoAi());
                mob.getNavigation().stop();
                mob.setTarget(null);
                mob.setNoAi(true);
            }
        }
    }

    private static void clearFrozenState(LivingEntity livingEntity, UUID entityId) {
        frozenRotations.remove(entityId);

        if (livingEntity instanceof Mob mob) {
            Boolean wasNoAi = frozenMobNoAiStates.remove(entityId);

            if (wasNoAi != null) {
                mob.setNoAi(wasNoAi);
            }
        } else {
            frozenMobNoAiStates.remove(entityId);
        }
    }

    private record Rotation(float yRot, float xRot) {
    }
}
