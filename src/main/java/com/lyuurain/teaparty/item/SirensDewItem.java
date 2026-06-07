package com.lyuurain.teaparty.item;

import java.util.Arrays;

import com.lyuurain.teaparty.config.ModConfig;
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
import org.joml.Vector3f;

public class SirensDewItem extends TooltipItem {
    private static final String DISABLED_MESSAGE_KEY = DrinkItem.DISABLED_MESSAGE_KEY;
    private static final int USE_DURATION = 32;
    private static final int DRINK_DAMAGE_INTERVAL = 10;
    private static final int PARTICLE_INTERVAL = 4;
    private static final int PARTICLE_POINTS = 72;
    private static final DustParticleOptions SIRENS_DEW_PARTICLE = new DustParticleOptions(new Vector3f(1.0F, 0.35F, 0.68F), 1.0F);

    public SirensDewItem(Properties properties, TooltipLine... tooltipLines) {
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
        return USE_DURATION;
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        if (level instanceof ServerLevel serverLevel) {
            if (remainingUseDuration % DRINK_DAMAGE_INTERVAL == 0) {
                livingEntity.hurt(livingEntity.damageSources().magic(), 1.0F);
            }

            if (remainingUseDuration % PARTICLE_INTERVAL == 0) {
                spawnParticleCircle(serverLevel, livingEntity, ModConfig.COMMON.sirensDewBuffRadius);
            }
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        if (!level.isClientSide()) {
            if (this.isDrinkDisabled()) {
                if (livingEntity instanceof Player player) {
                    player.displayClientMessage(net.minecraft.network.chat.Component.translatable(DISABLED_MESSAGE_KEY).withStyle(net.minecraft.ChatFormatting.GRAY), true);
                }
            } else {
                livingEntity.addEffect(new MobEffectInstance(ModEffects.LIES_RHYME, ModConfig.COMMON.liesRhymeDuration, 0, false, true, true));
            }
            }

        if (!(livingEntity instanceof Player player) || !player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        return stack;
    }

    private void spawnParticleCircle(ServerLevel serverLevel, LivingEntity livingEntity, double radius) {
        double centerX = livingEntity.getX();
        double centerY = livingEntity.getY() + 0.1D;
        double centerZ = livingEntity.getZ();

        for (int i = 0; i < PARTICLE_POINTS; i++) {
            double angle = Math.TAU * i / PARTICLE_POINTS;
            double x = centerX + Math.cos(angle) * radius;
            double z = centerZ + Math.sin(angle) * radius;
            serverLevel.sendParticles(SIRENS_DEW_PARTICLE, x, centerY, z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
    }
}
