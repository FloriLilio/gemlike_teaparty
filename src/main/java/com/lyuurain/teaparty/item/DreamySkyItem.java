package com.lyuurain.teaparty.item;
import java.util.Arrays;

import com.lyuurain.teaparty.config.ConfigValues;
import com.lyuurain.teaparty.config.ModConfig;
import com.lyuurain.teaparty.registry.ModEffects;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public class DreamySkyItem extends TooltipItem {
    private static final String DISABLED_MESSAGE_KEY = DrinkItem.DISABLED_MESSAGE_KEY;

    public DreamySkyItem(Properties properties, TooltipLine... tooltipLines) {
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
        return 48;
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        if (!level.isClientSide() && remainingUseDuration % 20 == 0) {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 80, 0, false, false, false));
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        if (!level.isClientSide()) {
            if (isDisabled(level, livingEntity)) {
                if (livingEntity instanceof Player player) {
                    player.displayClientMessage(Component.translatable(DISABLED_MESSAGE_KEY).withStyle(ChatFormatting.GRAY), true);
                }
            } else {
                if (ModConfig.COMMON.showDreamySkyParticles && level instanceof ServerLevel serverLevel) {
                    spawnDreamySkyParticles(serverLevel, livingEntity);
                }

                livingEntity.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 120, 0));
                livingEntity.addEffect(new MobEffectInstance(ModEffects.REBORN, MobEffectInstance.INFINITE_DURATION, 0, false, true, true));
            }
        }

        if (!(livingEntity instanceof Player player) || !player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        return stack;
    }

    private void spawnDreamySkyParticles(ServerLevel serverLevel, LivingEntity livingEntity) {
        double x = livingEntity.getX();
        double y = livingEntity.getY() + 0.2D;
        double z = livingEntity.getZ();

        for (int particle = 0; particle < 80; particle++) {
            double xOffset = (serverLevel.random.nextDouble() - 0.5D) * 1.4D;
            double yOffset = serverLevel.random.nextDouble() * 1.0D;
            double zOffset = (serverLevel.random.nextDouble() - 0.5D) * 1.4D;
            double ySpeed = 0.08D + serverLevel.random.nextDouble() * 0.12D;
            serverLevel.sendParticles(ParticleTypes.REVERSE_PORTAL, x + xOffset, y + yOffset, z + zOffset, 0, xOffset * 0.02D, ySpeed, zOffset * 0.02D, 1.0D);
        }
    }

    private boolean isDisabled(Level level, LivingEntity livingEntity) {
        if (Arrays.asList(ModConfig.COMMON.disabledDrinks).contains(net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(this).toString())) {
            return true;
        }
        return ConfigValues.isDimensionListed(level.dimension(), ModConfig.COMMON.dreamySkyDisabledDimensions) || ModConfig.COMMON.dreamySkyCheckTopBlock && hasUnbreakableBlockAtWorldTop(level, livingEntity);
    }

    private boolean hasUnbreakableBlockAtWorldTop(Level level, LivingEntity livingEntity) {
        int topY = level.getMinBuildHeight() + level.dimensionType().logicalHeight() - 1;
        BlockPos topPos = BlockPos.containing(livingEntity.getX(), topY, livingEntity.getZ());
        return level.getBlockState(topPos).getDestroySpeed(level, topPos) < 0.0F;
    }
}
