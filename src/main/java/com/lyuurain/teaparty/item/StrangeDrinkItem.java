package com.lyuurain.teaparty.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class StrangeDrinkItem extends DrinkItem {

    public StrangeDrinkItem(Properties properties, TooltipLine... tooltipLines) {
        super(properties, tooltipLines);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        if (!level.isClientSide()) {
            if (this.isDrinkDisabled()) {
                if (livingEntity instanceof Player player) {
                    player.displayClientMessage(net.minecraft.network.chat.Component.translatable(DISABLED_MESSAGE_KEY).withStyle(net.minecraft.ChatFormatting.GRAY), true);
                }
            } else {
                int randVal = level.random.nextInt(9);
                MobEffectInstance effectInstance = switch (randVal) {
                    case 0 -> new MobEffectInstance(MobEffects.SATURATION, 7, 0);
                    case 1 -> new MobEffectInstance(MobEffects.NIGHT_VISION, 100, 0);
                    case 2 -> new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 80, 0);
                    case 3 -> new MobEffectInstance(MobEffects.BLINDNESS, 160, 0);
                    case 4 -> new MobEffectInstance(MobEffects.WEAKNESS, 180, 0);
                    case 5 -> new MobEffectInstance(MobEffects.REGENERATION, 160, 0);
                    case 6 -> new MobEffectInstance(MobEffects.JUMP, 120, 0);
                    case 7 -> new MobEffectInstance(MobEffects.POISON, 240, 0);
                    case 8 -> new MobEffectInstance(MobEffects.WITHER, 160, 0);
                    default -> null;
                };

                if (effectInstance != null) {
                    livingEntity.addEffect(effectInstance);
                }
            }
        }

        return super.finishUsingItem(stack, level, livingEntity);
    }
}
