package com.lyuurain.teaparty.event;

import com.lyuurain.teaparty.registry.ModEffects;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RebornEffectEvents {
    private static final int LANDING_CHECK_GRACE_TICKS = 20;
    private static final Map<UUID, Integer> landingCheckGraceTicks = new HashMap<>();

    public static void startLandingCheckGrace(LivingEntity livingEntity) {
        landingCheckGraceTicks.put(livingEntity.getUUID(), LANDING_CHECK_GRACE_TICKS);
    }

    @SubscribeEvent
    public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        if (event.getEntity().hasEffect(ModEffects.REBORN)) {
            event.setAmount(0.0F);
            event.setInvulnerabilityTicks(20);
        }
    }

    @SubscribeEvent
    public static void onEntityTickPost(EntityTickEvent.Post event) {
        if (event.getEntity() instanceof LivingEntity livingEntity) {
            UUID entityId = livingEntity.getUUID();

            if (!livingEntity.hasEffect(ModEffects.REBORN)) {
                landingCheckGraceTicks.remove(entityId);
                return;
            }

            livingEntity.fallDistance = 0.0F;

            int graceTicks = landingCheckGraceTicks.getOrDefault(entityId, 0);
            if (graceTicks > 0) {
                landingCheckGraceTicks.put(entityId, graceTicks - 1);
                return;
            }

            landingCheckGraceTicks.remove(entityId);

            if (!livingEntity.level().isClientSide() && (livingEntity.onGround() || livingEntity.isInWater())) {
                livingEntity.removeEffect(ModEffects.REBORN);
                livingEntity.removeEffect(MobEffects.DARKNESS);
            }
        }
    }
}
