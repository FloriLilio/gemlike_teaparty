package com.lyuurain.teaparty.item;

import com.lyuurain.teaparty.config.ModConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

import java.util.Arrays;

public class DrinkItem extends TooltipItem {
    public static final String DISABLED_MESSAGE_KEY = "message.gemlike_teaparty.drink.disabled";

    public DrinkItem(Properties properties, TooltipLine... tooltipLines) {
        super(properties, tooltipLines);
    }

    public boolean isDrinkDisabled() {
        String itemId = BuiltInRegistries.ITEM.getKey(this).toString();
        return Arrays.asList(ModConfig.COMMON.disabledDrinks).contains(itemId);
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
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        if (livingEntity instanceof Player player) {
            if (player.hasInfiniteMaterials()) {
                return stack;
            }
            return ItemUtils.createFilledResult(stack, player, new ItemStack(Items.GLASS_BOTTLE));
        } else {
            stack.shrink(1);
        }

        return stack;
    }
}
