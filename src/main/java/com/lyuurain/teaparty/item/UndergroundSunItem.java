package com.lyuurain.teaparty.item;

import java.util.Arrays;

import com.lyuurain.teaparty.config.ModConfig;
import com.lyuurain.teaparty.registry.ModEffects;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public class UndergroundSunItem extends TooltipItem {
    private static final String DISABLED_MESSAGE_KEY = DrinkItem.DISABLED_MESSAGE_KEY;

    public UndergroundSunItem(Properties properties, TooltipLine... tooltipLines) {
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
            livingEntity.igniteForTicks(40);
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
                livingEntity.igniteForTicks(100);
                livingEntity.addEffect(new MobEffectInstance(ModEffects.FUSION, ModConfig.COMMON.undergroundSunFusionDuration, 0, false, true, true));
            }
            }

        if (!(livingEntity instanceof Player player) || !player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        return stack;
    }
}
