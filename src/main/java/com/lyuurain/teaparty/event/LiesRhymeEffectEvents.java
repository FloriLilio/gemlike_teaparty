package com.lyuurain.teaparty.event;

import com.lyuurain.teaparty.GemlikeTeaParty;
import com.lyuurain.teaparty.config.ModConfig;
import com.lyuurain.teaparty.config.SirenHealthReductionMode;
import com.lyuurain.teaparty.registry.ModDamageTypes;
import com.lyuurain.teaparty.registry.ModEffects;
import com.lyuurain.teaparty.registry.ModSounds;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.Team;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.joml.Vector3f;

import java.util.UUID;

public class LiesRhymeEffectEvents {
    private static final ResourceLocation MAX_HEALTH_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath(GemlikeTeaParty.MODID, "lies_rhyme_max_health");
    private static final int BUFF_DURATION = 600;
    private static final int REGENERATION_DURATION = 200;
    private static final int CONFUSION_DURATION = 600;
    private static final int WARNING_PARTICLE_INTERVAL = 4;
    private static final int PARTICLE_POINTS = 72;
    private static final DustParticleOptions WARNING_PARTICLE = new DustParticleOptions(new Vector3f(1.0F, 0.05F, 0.05F), 1.0F);

    @SubscribeEvent
    public static void onMobEffectAdded(MobEffectEvent.Added event) {
        if (event.getEffectInstance().is(ModEffects.LIES_RHYME) && event.getEntity().level() instanceof ServerLevel serverLevel) {
            LivingEntity singer = event.getEntity();
            applyMaxHealthReduction(singer);
            serverLevel.playSound(null, singer.blockPosition(), ModSounds.SIREN_SING.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
            applyAllyEffects(serverLevel, singer);
        }
    }

    @SubscribeEvent
    public static void onEntityTickPost(EntityTickEvent.Post event) {
        if (event.getEntity() instanceof LivingEntity livingEntity && livingEntity.level() instanceof ServerLevel serverLevel) {
            MobEffectInstance effect = livingEntity.getEffect(ModEffects.LIES_RHYME);

            if (effect == null) {
                return;
            }

            applyMaxHealthReduction(livingEntity);

            int displayTime = ModConfig.COMMON.liesRhymeWarningRangeDisplayTime;
            if ((displayTime == -1 || effect.getDuration() <= displayTime) && livingEntity.tickCount % WARNING_PARTICLE_INTERVAL == 0) {
                spawnParticleCircle(serverLevel, livingEntity, ModConfig.COMMON.liesRhymeEndDebuffRadius, WARNING_PARTICLE);
            }
        }
    }

    @SubscribeEvent
    public static void onMobEffectExpired(MobEffectEvent.Expired event) {
        if (event.getEffectInstance().is(ModEffects.LIES_RHYME) && event.getEntity().level() instanceof ServerLevel serverLevel) {
            LivingEntity singer = event.getEntity();
            removeMaxHealthReduction(singer);
            serverLevel.playSound(null, singer.blockPosition(), ModSounds.SIREN_SCREAM.get(), SoundSource.PLAYERS, 1.0F, 1.0F);

            for (LivingEntity target : getLivingEntitiesInRange(serverLevel, singer, ModConfig.COMMON.liesRhymeEndDebuffRadius)) {
                target.hurt(target.damageSources().source(ModDamageTypes.SIREN_SCREAM, singer), ModConfig.COMMON.liesRhymeEndDamage);

                if (!target.isDeadOrDying()) {
                    target.addEffect(new MobEffectInstance(MobEffects.CONFUSION, CONFUSION_DURATION, 0, false, true, true));
                }
            }
        }
    }

    @SubscribeEvent
    public static void onMobEffectRemove(MobEffectEvent.Remove event) {
        MobEffectInstance effectInstance = event.getEffectInstance();
        if (effectInstance != null && effectInstance.is(ModEffects.LIES_RHYME)) {
            removeMaxHealthReduction(event.getEntity());
        }
    }

    private static void applyAllyEffects(ServerLevel serverLevel, LivingEntity singer) {
        for (LivingEntity target : getLivingEntitiesInRange(serverLevel, singer, ModConfig.COMMON.sirensDewBuffRadius)) {
            if (canReceiveAllyEffects(singer, target)) {
                target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, BUFF_DURATION, 1, false, true, true));
                target.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, BUFF_DURATION, 1, false, true, true));
                target.addEffect(new MobEffectInstance(MobEffects.REGENERATION, REGENERATION_DURATION, 1, false, true, true));
                serverLevel.sendParticles(ParticleTypes.HEART, target.getX(), target.getY() + target.getBbHeight() * 0.75D, target.getZ(), 8, target.getBbWidth() * 0.5D, target.getBbHeight() * 0.35D, target.getBbWidth() * 0.5D, 0.0D);
            }
        }
    }

    private static boolean canReceiveAllyEffects(LivingEntity singer, LivingEntity target) {
        Team team = singer.getTeam();

        if (target instanceof Player) {
            return team == null || target.isAlliedTo(team);
        }

        if (target instanceof TamableAnimal tamableAnimal && tamableAnimal.isTame()) {
            if (team == null) {
                return true;
            }

            UUID ownerUuid = tamableAnimal.getOwnerUUID();
            ServerPlayer owner = ownerUuid == null || !(singer.level() instanceof ServerLevel serverLevel) ? null : serverLevel.getServer().getPlayerList().getPlayer(ownerUuid);
            return owner != null && (owner == singer || owner.isAlliedTo(team));
        }

        return false;
    }

    private static Iterable<LivingEntity> getLivingEntitiesInRange(ServerLevel serverLevel, LivingEntity center, double radius) {
        double radiusSqr = radius * radius;
        AABB area = center.getBoundingBox().inflate(radius);
        return serverLevel.getEntitiesOfClass(LivingEntity.class, area, entity -> entity != center && entity.distanceToSqr(center) <= radiusSqr);
    }

    private static void spawnParticleCircle(ServerLevel serverLevel, LivingEntity livingEntity, double radius, DustParticleOptions particle) {
        double centerX = livingEntity.getX();
        double centerY = livingEntity.getY() + 0.1D;
        double centerZ = livingEntity.getZ();

        for (int i = 0; i < PARTICLE_POINTS; i++) {
            double angle = Math.TAU * i / PARTICLE_POINTS;
            double x = centerX + Math.cos(angle) * radius;
            double z = centerZ + Math.sin(angle) * radius;
            serverLevel.sendParticles(particle, x, centerY, z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
    }

    private static void applyMaxHealthReduction(LivingEntity livingEntity) {
        AttributeInstance maxHealth = livingEntity.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth == null) {
            return;
        }

        double amount = getMaxHealthReductionAmount(maxHealth.getBaseValue());
        maxHealth.addOrUpdateTransientModifier(new AttributeModifier(MAX_HEALTH_MODIFIER_ID, -amount, AttributeModifier.Operation.ADD_VALUE));
        if (livingEntity.getHealth() > livingEntity.getMaxHealth()) {
            livingEntity.setHealth(livingEntity.getMaxHealth());
        }
    }

    private static void removeMaxHealthReduction(LivingEntity livingEntity) {
        AttributeInstance maxHealth = livingEntity.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth != null) {
            maxHealth.removeModifier(MAX_HEALTH_MODIFIER_ID);
        }
    }

    private static double getMaxHealthReductionAmount(double baseMaxHealth) {
        SirenHealthReductionMode mode = SirenHealthReductionMode.fromConfig(ModConfig.COMMON.liesRhymeHealthReductionMode);
        if (mode == SirenHealthReductionMode.PERCENTAGE) {
            return baseMaxHealth * ModConfig.COMMON.liesRhymeHealthReductionPercentage;
        }

        return ModConfig.COMMON.liesRhymeHealthReductionAmount;
    }
}
