package com.lyuurain.teaparty.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;

public class EffectDrinkItem extends DrinkItem {
    private final Supplier<MobEffectInstance> effectSupplier;

    public EffectDrinkItem(Properties properties, Supplier<MobEffectInstance> effectSupplier, TooltipLine... tooltipLines) {
        super(properties, tooltipLines);
        this.effectSupplier = effectSupplier;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        ItemStack result = super.finishUsingItem(stack, level, livingEntity);

        if (!level.isClientSide) {
            if (this.isDrinkDisabled()) {
                if (livingEntity instanceof Player player) {
                    player.displayClientMessage(net.minecraft.network.chat.Component.translatable(DISABLED_MESSAGE_KEY).withStyle(net.minecraft.ChatFormatting.GRAY), true);
                }
            } else {
                livingEntity.addEffect(this.effectSupplier.get());
            }
        }

        return result;
    }
}
