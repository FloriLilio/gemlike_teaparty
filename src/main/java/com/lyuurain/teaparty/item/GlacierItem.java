package com.lyuurain.teaparty.item;

import com.lyuurain.teaparty.registry.ModEffects;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class GlacierItem extends TooltipItem {
    private static final int GELID_DURATION = 1800;
    private static final int PARTICLE_INTERVAL = 4;
    private static final int PARTICLE_POINTS = 72;
    private static final double PARTICLE_RADIUS = 9.0D;
    private static final DustParticleOptions GLACIER_PARTICLE = new DustParticleOptions(new Vector3f(0.35F, 0.75F, 1.0F), 1.0F);

    public GlacierItem(Properties properties, TooltipLine... tooltipLines) {
        super(properties, tooltipLines);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemStack = player.getItemInHand(usedHand);
        player.startUsingItem(usedHand);
        return InteractionResultHolder.consume(itemStack);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity livingEntity) {
        return 32;
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        if (!level.isClientSide()) {
            stopHorizontalMovement(livingEntity);

            if (level instanceof ServerLevel serverLevel && remainingUseDuration % PARTICLE_INTERVAL == 0) {
                spawnParticleCircle(serverLevel, livingEntity);
            }
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        if (!level.isClientSide()) {
            livingEntity.addEffect(new MobEffectInstance(ModEffects.GELID, GELID_DURATION, 0, false, true, true));
        }

        if (!(livingEntity instanceof Player player) || !player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        return stack;
    }

    private void stopHorizontalMovement(LivingEntity livingEntity) {
        Vec3 movement = livingEntity.getDeltaMovement();
        livingEntity.setDeltaMovement(0.0D, movement.y, 0.0D);
        livingEntity.setSprinting(false);
    }

    private void spawnParticleCircle(ServerLevel serverLevel, LivingEntity livingEntity) {
        double centerX = livingEntity.getX();
        double centerY = livingEntity.getY() + 0.1D;
        double centerZ = livingEntity.getZ();

        for (int i = 0; i < PARTICLE_POINTS; i++) {
            double angle = Math.TAU * i / PARTICLE_POINTS;
            double x = centerX + Math.cos(angle) * PARTICLE_RADIUS;
            double z = centerZ + Math.sin(angle) * PARTICLE_RADIUS;
            serverLevel.sendParticles(GLACIER_PARTICLE, x, centerY, z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
    }
}
