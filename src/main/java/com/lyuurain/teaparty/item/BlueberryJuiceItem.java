package com.lyuurain.teaparty.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BlueberryJuiceItem extends DrinkItem {
    public BlueberryJuiceItem(Properties properties, TooltipLine... tooltipLines) {
        super(properties, tooltipLines);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        ItemStack result = super.finishUsingItem(stack, level, livingEntity);

        if (!level.isClientSide && livingEntity instanceof Player player) {
            if (this.isDrinkDisabled()) {
                player.displayClientMessage(net.minecraft.network.chat.Component.translatable(DISABLED_MESSAGE_KEY).withStyle(net.minecraft.ChatFormatting.GRAY), true);
            } else {
                player.getFoodData().eat(4, 1.0F);
            }
        }

        return result;
    }
}
