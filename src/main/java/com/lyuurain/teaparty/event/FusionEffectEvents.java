package com.lyuurain.teaparty.event;

import com.lyuurain.teaparty.config.ModConfig;
import com.lyuurain.teaparty.registry.ModDamageTypes;
import com.lyuurain.teaparty.registry.ModEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.Optional;

public class FusionEffectEvents {
    private static final ExplosionDamageCalculator BLOCK_BREAKING_EXPLOSION = new FusionBlockExplosionDamageCalculator(false);
    private static final ExplosionDamageCalculator BLOCK_ONLY_EXPLOSION = new FusionBlockExplosionDamageCalculator(true);

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();

        if (!player.level().isClientSide() && player.hasEffect(ModEffects.FUSION)) {
            event.setCanceled(true);
            explodeDestroyingBlocksWithoutDropDecay(player.level(), player, event.getPos().getX() + 0.5D, event.getPos().getY() + 0.5D, event.getPos().getZ() + 0.5D, ModConfig.COMMON.fusionBlockExplosionRadius, BLOCK_ONLY_EXPLOSION);
            player.removeEffect(ModEffects.FUSION);
        }
    }

    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        Player player = event.getEntity();
        Entity target = event.getTarget();

        if (!player.level().isClientSide() && target instanceof LivingEntity livingTarget && player.hasEffect(ModEffects.FUSION)) {
            event.setCanceled(true);
            explode(player.level(), player, livingTarget.getX(), livingTarget.getY(), livingTarget.getZ(), ModConfig.COMMON.fusionAttackExplosionRadius, Level.ExplosionInteraction.NONE, new FusionAttackExplosionDamageCalculator(player));
            spawnAttackExplosionParticles(player.level(), livingTarget.getX(), livingTarget.getY(), livingTarget.getZ());
            player.removeEffect(ModEffects.FUSION);
        }
    }

    @SubscribeEvent
    public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        LivingEntity livingEntity = event.getEntity();

        if (livingEntity.hasEffect(ModEffects.FUSION) && event.getSource().is(DamageTypeTags.IS_FIRE)) {
            livingEntity.extinguishFire();
            event.setAmount(0.0F);
        }
    }

    @SubscribeEvent
    public static void onMobEffectExpired(MobEffectEvent.Expired event) {
        if (event.getEffectInstance().is(ModEffects.FUSION)) {
            LivingEntity livingEntity = event.getEntity();
            explodeDestroyingBlocksWithoutDropDecay(livingEntity.level(), livingEntity, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), ModConfig.COMMON.fusionSelfExplosionRadius, BLOCK_BREAKING_EXPLOSION);
            livingEntity.hurt(livingEntity.damageSources().source(ModDamageTypes.FUSION_SUICIDE), Float.MAX_VALUE);
        }
    }

    private static void explode(Level level, Entity source, double x, double y, double z, float radius, Level.ExplosionInteraction interaction, ExplosionDamageCalculator damageCalculator) {
        level.explode(source, null, damageCalculator, x, y, z, radius, false, interaction, ParticleTypes.EXPLOSION_EMITTER, ParticleTypes.EXPLOSION_EMITTER, SoundEvents.GENERIC_EXPLODE);
    }

    private static void explodeDestroyingBlocksWithoutDropDecay(Level level, Entity source, double x, double y, double z, float radius, ExplosionDamageCalculator damageCalculator) {
        GameRules.BooleanValue dropDecay = level.getGameRules().getRule(GameRules.RULE_BLOCK_EXPLOSION_DROP_DECAY);
        boolean previousDropDecay = dropDecay.get();
        dropDecay.set(false, level.getServer());

        try {
            explode(level, source, x, y, z, radius, Level.ExplosionInteraction.BLOCK, damageCalculator);
        } finally {
            dropDecay.set(previousDropDecay, level.getServer());
        }
    }

    private static void spawnAttackExplosionParticles(Level level, double x, double y, double z) {
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER, x, y, z, 6, 1.5D, 1.5D, 1.5D, 0.0D);
        }
    }

    private static class FusionBlockExplosionDamageCalculator extends ExplosionDamageCalculator {
        private final boolean blockOnly;

        private FusionBlockExplosionDamageCalculator(boolean blockOnly) {
            this.blockOnly = blockOnly;
        }

        @Override
        public Optional<Float> getBlockExplosionResistance(Explosion explosion, BlockGetter level, BlockPos pos, BlockState blockState, FluidState fluidState) {
            if (isExplosionProof(level, pos, blockState)) {
                return super.getBlockExplosionResistance(explosion, level, pos, blockState, fluidState);
            }

            return Optional.of(0.0F);
        }

        @Override
        public boolean shouldBlockExplode(Explosion explosion, BlockGetter level, BlockPos pos, BlockState blockState, float explosionPower) {
            return !isExplosionProof(level, pos, blockState) && super.shouldBlockExplode(explosion, level, pos, blockState, explosionPower);
        }

        @Override
        public boolean shouldDamageEntity(Explosion explosion, Entity entity) {
            return !this.blockOnly;
        }

        @Override
        public float getKnockbackMultiplier(Entity entity) {
            return this.blockOnly ? 0.0F : super.getKnockbackMultiplier(entity);
        }
    }

    private static boolean isExplosionProof(BlockGetter level, BlockPos pos, BlockState blockState) {
        return blockState.getDestroySpeed(level, pos) < 0.0F || blockState.is(Blocks.OBSIDIAN) || blockState.is(Blocks.CRYING_OBSIDIAN) || blockState.is(Blocks.RESPAWN_ANCHOR) || blockState.is(Blocks.END_PORTAL_FRAME) || blockState.is(Blocks.ANCIENT_DEBRIS) || blockState.is(Blocks.NETHERITE_BLOCK);
    }

    private static class FusionAttackExplosionDamageCalculator extends ExplosionDamageCalculator {
        private final Entity source;

        private FusionAttackExplosionDamageCalculator(Entity source) {
            this.source = source;
        }

        @Override
        public boolean shouldDamageEntity(Explosion explosion, Entity entity) {
            return entity != this.source;
        }
    }
}
