package com.lyuurain.teaparty.event;

import com.lyuurain.teaparty.network.EndVisionConfigPayload;
import com.lyuurain.teaparty.registry.ModEffects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

public class EndVisionEffectEvents {
    private static final int GLOWING_DURATION = 200;

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            syncConfig(serverPlayer);
        }
    }

    @SubscribeEvent
    public static void onMobEffectAdded(MobEffectEvent.Added event) {
        if (event.getEffectInstance().is(ModEffects.END_VISION)) {
            if (event.getEntity() instanceof ServerPlayer serverPlayer) {
                syncConfig(serverPlayer);
            }

            teleportLikeChorusFruit(event.getEntity());
        }
    }

    @SubscribeEvent
    public static void onMobEffectExpired(MobEffectEvent.Expired event) {
        if (event.getEffectInstance().is(ModEffects.END_VISION)) {
            event.getEntity().addEffect(new MobEffectInstance(MobEffects.GLOWING, GLOWING_DURATION, 0, false, true, true));
        }
    }

    private static void syncConfig(ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, EndVisionConfigPayload.fromCommonConfig());
    }

    private static void teleportLikeChorusFruit(LivingEntity livingEntity) {
        Level level = livingEntity.level();

        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        for (int i = 0; i < 16; i++) {
            double x = livingEntity.getX() + (livingEntity.getRandom().nextDouble() - 0.5D) * 16.0D;
            double y = Mth.clamp(livingEntity.getY() + livingEntity.getRandom().nextInt(16) - 8.0D, level.getMinBuildHeight(), level.getMinBuildHeight() + serverLevel.getLogicalHeight() - 1);
            double z = livingEntity.getZ() + (livingEntity.getRandom().nextDouble() - 0.5D) * 16.0D;

            if (livingEntity.isPassenger()) {
                livingEntity.stopRiding();
            }

            Vec3 oldPosition = livingEntity.position();
            EntityTeleportEvent.ChorusFruit teleportEvent = EventHooks.onChorusFruitTeleport(livingEntity, x, y, z);

            if (teleportEvent.isCanceled()) {
                return;
            }

            if (livingEntity.randomTeleport(teleportEvent.getTargetX(), teleportEvent.getTargetY(), teleportEvent.getTargetZ(), true)) {
                level.gameEvent(GameEvent.TELEPORT, oldPosition, GameEvent.Context.of(livingEntity));
                SoundSource soundSource;
                SoundEvent soundEvent;

                if (livingEntity instanceof Fox) {
                    soundEvent = SoundEvents.FOX_TELEPORT;
                    soundSource = SoundSource.NEUTRAL;
                } else {
                    soundEvent = SoundEvents.CHORUS_FRUIT_TELEPORT;
                    soundSource = SoundSource.PLAYERS;
                }

                level.playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), soundEvent, soundSource);
                livingEntity.resetFallDistance();

                if (livingEntity instanceof Player player) {
                    player.resetCurrentImpulseContext();
                }

                break;
            }
        }
    }
}
