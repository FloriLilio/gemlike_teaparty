package com.lyuurain.teaparty.event;

import com.lyuurain.teaparty.config.ConfigValues;
import com.lyuurain.teaparty.config.ModConfig;
import com.lyuurain.teaparty.effect.GelidEffect;
import com.lyuurain.teaparty.effect.PerfectFrozenEffect;
import com.lyuurain.teaparty.registry.ModEffects;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.EntityTypeTags;
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
    private static final Map<UUID, Rotation> frozenRotations = new HashMap<>();
    private static final Map<UUID, Integer> silentColdEffectRemovals = new HashMap<>();

    @SubscribeEvent
    public static void onLivingDamagePost(LivingDamageEvent.Post event) {
        LivingEntity target = event.getEntity();
        Entity attacker = event.getSource().getEntity();

        if (attacker instanceof LivingEntity livingAttacker && hasGelidEffect(livingAttacker)) {
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, ModConfig.COMMON.frozenSlownessDuration, 0));
            playBakaSoundForPerfectFrozen(livingAttacker);
        }

        if (hasGelidEffect(target) && attacker instanceof LivingEntity livingAttacker) {
            livingAttacker.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, ModConfig.COMMON.frozenSlownessDuration, 0));
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

            if (!ConfigValues.isDimensionListed(event.getEntity().level().dimension(), ModConfig.COMMON.disabledGlacierDimensions) && GelidEffect.canBeFrozen(event.getEntity())) {
                event.getEntity().addEffect(new MobEffectInstance(ModEffects.FROZEN, ModConfig.COMMON.gelidFrozenDuration, 0, false, true, true));
            }
        } else if (event.getEffectInstance().is(ModEffects.FROZEN)) {
            LivingEntity livingEntity = event.getEntity();
            playFrozenBreakSound(livingEntity);
            clearFrozenState(livingEntity, livingEntity.getUUID(), true);

            if (ModConfig.COMMON.allowSkeletonToStrayConversion && livingEntity.getType() == EntityType.SKELETON && !ConfigValues.isDimensionListed(livingEntity.level().dimension(), ModConfig.COMMON.disabledGlacierDimensions) && livingEntity instanceof Mob mob && mob.convertTo(EntityType.STRAY, true) != null) {
                return;
            }

            livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, ModConfig.COMMON.frozenSlownessDuration, 0));
        }
    }

    @SubscribeEvent
    public static void onMobEffectRemove(MobEffectEvent.Remove event) {
        if (consumeSilentColdEffectRemoval(event.getEntity().getUUID())) {
            if (event.getEffect() == ModEffects.FROZEN) {
                clearFrozenState(event.getEntity(), event.getEntity().getUUID(), true);
            }
            return;
        }

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
                if (ConfigValues.isDimensionListed(livingEntity.level().dimension(), ModConfig.COMMON.disabledGlacierDimensions)) {
                    silentlyRemoveColdEffects(livingEntity, entityId, true);
                    return;
                }

                livingEntity.extinguishFire();

                if (livingEntity.isInLava()) {
                    silentlyRemoveColdEffects(livingEntity, entityId, false);
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
                livingEntity.hurt(livingEntity.damageSources().freeze(), ModConfig.COMMON.frozenDamage);
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
        if (livingEntity.hasEffect(ModEffects.PERFECT_FROZEN) && canPlayBakaSound(livingEntity)) {
            PerfectFrozenEffect.playBakaSound(livingEntity);
        }
    }

    private static boolean canPlayBakaSound(LivingEntity livingEntity) {
        return !ConfigValues.isDimensionListed(livingEntity.level().dimension(), ModConfig.COMMON.disabledGlacierDimensions);
    }

    private static void playFrozenBreakSound(LivingEntity livingEntity) {
        if (!livingEntity.level().isClientSide()) {
            livingEntity.level().playSound(null, livingEntity.blockPosition(), SoundEvents.GLASS_BREAK, SoundSource.PLAYERS, 1.0F, 1.0F);
        }
    }

    private static boolean isFireDamage(LivingIncomingDamageEvent event) {
        return event.getSource().is(DamageTypes.IN_FIRE) || event.getSource().is(DamageTypes.ON_FIRE) || event.getSource().is(DamageTypes.HOT_FLOOR) || event.getSource().is(DamageTypes.CAMPFIRE);
    }

    private static boolean takesFrozenFreezeDamage(LivingEntity livingEntity) {
        return livingEntity.getType().is(EntityTypeTags.FREEZE_HURTS_EXTRA_TYPES);
    }

    private static void silentlyRemoveColdEffects(LivingEntity livingEntity, UUID entityId, boolean playBreakSound) {
        int removalCount = 0;

        boolean hasFrozen = livingEntity.hasEffect(ModEffects.FROZEN);
        boolean hasGelid = livingEntity.hasEffect(ModEffects.GELID);
        boolean hasPerfectFrozen = livingEntity.hasEffect(ModEffects.PERFECT_FROZEN);

        if (hasFrozen) {
            removalCount++;
        }

        if (hasGelid) {
            removalCount++;
        }

        if (hasPerfectFrozen) {
            removalCount++;
        }

        if (removalCount > 0) {
            silentColdEffectRemovals.merge(entityId, removalCount, Integer::sum);

            if (playBreakSound) {
                playFrozenBreakSound(livingEntity);
            }

            if (hasFrozen) livingEntity.removeEffect(ModEffects.FROZEN);
            if (hasGelid) livingEntity.removeEffect(ModEffects.GELID);
            if (hasPerfectFrozen) livingEntity.removeEffect(ModEffects.PERFECT_FROZEN);
        }

        clearFrozenState(livingEntity, entityId, true);
    }

    private static boolean consumeSilentColdEffectRemoval(UUID entityId) {
        Integer removalCount = silentColdEffectRemovals.get(entityId);

        if (removalCount == null) {
            return false;
        }

        if (removalCount <= 1) {
            silentColdEffectRemovals.remove(entityId);
        } else {
            silentColdEffectRemovals.put(entityId, removalCount - 1);
        }

        return true;
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
