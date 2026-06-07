package com.lyuurain.teaparty.event;

import com.lyuurain.teaparty.config.ModConfig;
import com.lyuurain.teaparty.registry.ModEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public class SoftTouchEffectEvents {

    @SubscribeEvent
    public static void onLivingDamagePost(LivingDamageEvent.Post event) {
        LivingEntity target = event.getEntity();
        Entity attacker = event.getSource().getEntity();

        if (attacker instanceof LivingEntity livingAttacker && livingAttacker.hasEffect(ModEffects.SOFT_TOUCH)) {
            target.addEffect(new MobEffectInstance(MobEffects.LEVITATION, ModConfig.COMMON.softTouchLevitationDuration, 0, false, true, true));
        }

        if (target.hasEffect(ModEffects.SOFT_TOUCH) && attacker instanceof LivingEntity livingAttacker) {
            livingAttacker.addEffect(new MobEffectInstance(MobEffects.LEVITATION, ModConfig.COMMON.softTouchLevitationDuration, 0, false, true, true));
        }
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getEntity().hasEffect(ModEffects.SOFT_TOUCH) && event.getTarget() instanceof LivingEntity target) {
            if (!event.getLevel().isClientSide) {
                target.addEffect(new MobEffectInstance(MobEffects.LEVITATION, ModConfig.COMMON.softTouchLevitationDuration, 0, false, true, true));
            }
            event.setCanceled(true); // Can also be left uncanceled depending on preference, but applying levitation
        }
    }
}
