package com.lyuurain.teaparty.event;

import com.lyuurain.teaparty.effect.GelidEffect;
import com.lyuurain.teaparty.registry.ModEffects;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GlacierEffectEvents {
    private static final int SLOWNESS_DURATION = 180;
    private static final int FROZEN_DURATION = 180;
    private static final Map<UUID, Rotation> frozenRotations = new HashMap<>();

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
    public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        LivingEntity livingEntity = event.getEntity();

        if (hasColdEffect(livingEntity) && isFireDamage(event)) {
            livingEntity.extinguishFire();
            event.setAmount(0.0F);
        }

        if (livingEntity.hasEffect(ModEffects.FROZEN) && event.getSource().is(DamageTypes.FREEZE) && !takesFrozenFreezeDamage(livingEntity)) {
            event.setAmount(0.0F);
        }
    }

    @SubscribeEvent
    public static void onMobEffectExpired(MobEffectEvent.Expired event) {
        if (event.getEffectInstance().is(ModEffects.GELID)) {
            if (GelidEffect.canBeFrozen(event.getEntity())) {
                event.getEntity().addEffect(new MobEffectInstance(ModEffects.FROZEN, FROZEN_DURATION, 0, false, true, true));
            }
        } else if (event.getEffectInstance().is(ModEffects.FROZEN)) {
            LivingEntity livingEntity = event.getEntity();
            clearFrozenState(livingEntity, livingEntity.getUUID(), true);

            if (livingEntity.getType() == EntityType.SKELETON && livingEntity instanceof Mob mob && mob.convertTo(EntityType.STRAY, true) != null) {
                return;
            }

            livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, SLOWNESS_DURATION, 0));
        }
    }

    @SubscribeEvent
    public static void onMobEffectRemove(MobEffectEvent.Remove event) {
        if (event.getEffect() == ModEffects.FROZEN) {
            clearFrozenState(event.getEntity(), event.getEntity().getUUID(), true);
        }
    }

    @SubscribeEvent
    public static void onEntityTickPost(EntityTickEvent.Post event) {
        if (event.getEntity() instanceof LivingEntity livingEntity) {
            UUID entityId = livingEntity.getUUID();
            boolean hasFrozen = livingEntity.hasEffect(ModEffects.FROZEN);
            boolean hasColdEffect = hasFrozen || livingEntity.hasEffect(ModEffects.GELID);

            if (hasColdEffect) {
                livingEntity.extinguishFire();
            }

            if (!hasFrozen) {
                clearFrozenState(livingEntity, entityId, false);
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

            if (!livingEntity.level().isClientSide() && takesFrozenFreezeDamage(livingEntity) && livingEntity.tickCount % 20 == 0) {
                livingEntity.hurt(livingEntity.damageSources().freeze(), 2.0F);
            }
        }
    }

    private static boolean hasColdEffect(LivingEntity livingEntity) {
        return livingEntity.hasEffect(ModEffects.FROZEN) || livingEntity.hasEffect(ModEffects.GELID);
    }

    private static boolean isFireDamage(LivingIncomingDamageEvent event) {
        return event.getSource().is(DamageTypes.IN_FIRE) || event.getSource().is(DamageTypes.ON_FIRE) || event.getSource().is(DamageTypes.LAVA);
    }

    private static boolean takesFrozenFreezeDamage(LivingEntity livingEntity) {
        EntityType<?> entityType = livingEntity.getType();
        return entityType == EntityType.BLAZE || entityType == EntityType.MAGMA_CUBE;
    }

    private static void clearFrozenState(LivingEntity livingEntity, UUID entityId, boolean clearFrozenTicks) {
        frozenRotations.remove(entityId);

        if (clearFrozenTicks) {
            livingEntity.setTicksFrozen(0);
        }
    }

    private record Rotation(float yRot, float xRot) {
    }
}
