package com.lyuurain.teaparty.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class TooltipItem extends Item {
    private final TooltipLine[] tooltipLines;

    public TooltipItem(Properties properties, TooltipLine... tooltipLines) {
        super(properties);
        this.tooltipLines = tooltipLines;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        for (TooltipLine tooltipLine : tooltipLines) {
            tooltipComponents.add(Component.translatable(tooltipLine.key()).withStyle(tooltipLine.styles()));
        }
    }

    public record TooltipLine(String key, ChatFormatting... styles) {
    }
}
