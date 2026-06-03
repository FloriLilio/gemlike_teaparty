package com.lyuurain.teaparty.item;

import com.lyuurain.teaparty.registry.ModDamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class LemonItem extends TooltipItem {
    public LemonItem(Properties properties, TooltipLine... tooltipLines) {
        super(properties, tooltipLines);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        ItemStack result = super.finishUsingItem(stack, level, livingEntity);

        if (!level.isClientSide) {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 400, 1, false, true, true));
            livingEntity.hurt(livingEntity.damageSources().source(ModDamageTypes.LEMON), 1.0F);
        }

        return result;
    }
}
