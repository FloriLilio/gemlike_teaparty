package com.lyuurain.teaparty.event;

import com.lyuurain.teaparty.effect.GelidEffect;
import com.lyuurain.teaparty.effect.PerfectFrozenEffect;
import com.lyuurain.teaparty.registry.ModEffects;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
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

        if (attacker instanceof LivingEntity livingAttacker && hasGelidEffect(livingAttacker)) {
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, SLOWNESS_DURATION, 0));
            playBakaSoundForPerfectFrozen(livingAttacker);
        }

        if (hasGelidEffect(target) && attacker instanceof LivingEntity livingAttacker) {
            livingAttacker.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, SLOWNESS_DURATION, 0));
            playBakaSoundForPerfectFrozen(target);
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
        if (isGelidEffect(event.getEffectInstance())) {
            if (event.getEffectInstance().is(ModEffects.PERFECT_FROZEN)) {
                PerfectFrozenEffect.playBakaSound(event.getEntity());
            }

            if (event.getEntity().level().dimension() != Level.NETHER && GelidEffect.canBeFrozen(event.getEntity())) {
                event.getEntity().addEffect(new MobEffectInstance(ModEffects.FROZEN, FROZEN_DURATION, 0, false, true, true));
            }
        } else if (event.getEffectInstance().is(ModEffects.FROZEN)) {
            LivingEntity livingEntity = event.getEntity();
            playFrozenBreakSound(livingEntity);
            clearFrozenState(livingEntity, livingEntity.getUUID(), true);

            if (livingEntity.getType() == EntityType.SKELETON && livingEntity.level().dimension() != Level.NETHER && livingEntity instanceof Mob mob && mob.convertTo(EntityType.STRAY, true) != null) {
                return;
            }

            livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, SLOWNESS_DURATION, 0));
        }
    }

    @SubscribeEvent
    public static void onMobEffectRemove(MobEffectEvent.Remove event) {
        if (event.getEffect() == ModEffects.FROZEN) {
            playFrozenBreakSound(event.getEntity());
            clearFrozenState(event.getEntity(), event.getEntity().getUUID(), true);
        } else if (event.getEffect() == ModEffects.PERFECT_FROZEN && !event.getEntity().hasEffect(ModEffects.GELID)) {
            PerfectFrozenEffect.playBakaSound(event.getEntity());
        }
    }

    @SubscribeEvent
    public static void onEntityTickPost(EntityTickEvent.Post event) {
        if (event.getEntity() instanceof LivingEntity livingEntity) {
            UUID entityId = livingEntity.getUUID();
            boolean hasFrozen = livingEntity.hasEffect(ModEffects.FROZEN);
            boolean hasColdEffect = hasFrozen || hasGelidEffect(livingEntity);

            if (hasColdEffect) {
                if (livingEntity.level().dimension() == Level.NETHER) {
                    livingEntity.removeEffect(ModEffects.FROZEN);
                    livingEntity.removeEffect(ModEffects.GELID);
                    livingEntity.removeEffect(ModEffects.PERFECT_FROZEN);
                    clearFrozenState(livingEntity, entityId, true);
                    return;
                }

                livingEntity.extinguishFire();

                if (livingEntity.isInLava()) {
                    livingEntity.removeEffect(ModEffects.FROZEN);
                    livingEntity.removeEffect(ModEffects.GELID);
                    livingEntity.removeEffect(ModEffects.PERFECT_FROZEN);
                    clearFrozenState(livingEntity, entityId, true);
                    return;
                }
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
                livingEntity.hurt(livingEntity.damageSources().freeze(), 0.4F);
            }
        }
    }

    private static boolean hasColdEffect(LivingEntity livingEntity) {
        return livingEntity.hasEffect(ModEffects.FROZEN) || hasGelidEffect(livingEntity);
    }

    private static boolean hasGelidEffect(LivingEntity livingEntity) {
        return livingEntity.hasEffect(ModEffects.GELID) || livingEntity.hasEffect(ModEffects.PERFECT_FROZEN);
    }

    private static boolean isGelidEffect(MobEffectInstance effectInstance) {
        return effectInstance.is(ModEffects.GELID) || effectInstance.is(ModEffects.PERFECT_FROZEN);
    }

    private static void playBakaSoundForPerfectFrozen(LivingEntity livingEntity) {
        if (livingEntity.hasEffect(ModEffects.PERFECT_FROZEN)) {
            PerfectFrozenEffect.playBakaSound(livingEntity);
        }
    }

    private static void playFrozenBreakSound(LivingEntity livingEntity) {
        if (!livingEntity.level().isClientSide()) {
            livingEntity.level().playSound(null, livingEntity.blockPosition(), SoundEvents.GLASS_BREAK, SoundSource.PLAYERS, 1.0F, 1.0F);
        }
    }

    private static boolean isFireDamage(LivingIncomingDamageEvent event) {
        return event.getSource().is(DamageTypes.IN_FIRE) || event.getSource().is(DamageTypes.ON_FIRE);
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
