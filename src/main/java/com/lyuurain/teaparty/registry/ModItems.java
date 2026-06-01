package com.lyuurain.teaparty.registry;

import com.lyuurain.teaparty.GemlikeTeaParty;
import com.lyuurain.teaparty.item.DreamySkyItem;
import com.lyuurain.teaparty.item.GlacierItem;
import com.lyuurain.teaparty.item.TooltipItem;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(GemlikeTeaParty.MODID);

    public static final DeferredItem<Item> MIXING_CUP = ITEMS.registerSimpleItem("mixing_cup");

    public static final DeferredItem<Item> GLACIER = ITEMS.registerItem("glacier", properties -> new GlacierItem(properties,
            tooltip("tooltip.gemlike_teaparty.glacier.0", ChatFormatting.GRAY)));
    public static final DeferredItem<Item> END_VISION = ITEMS.registerItem("end_vision", properties -> tooltipItem(properties,
            tooltip("tooltip.gemlike_teaparty.end_vision.0", ChatFormatting.GRAY)));
    public static final DeferredItem<Item> DREAMY_SKY = ITEMS.registerItem("dreamy_sky", properties -> new DreamySkyItem(properties,
            tooltip("tooltip.gemlike_teaparty.dreamy_sky.0", ChatFormatting.LIGHT_PURPLE),
            tooltip("tooltip.gemlike_teaparty.dreamy_sky.1", ChatFormatting.DARK_RED)));
    public static final DeferredItem<Item> SIRENS_DEW = ITEMS.registerItem("sirens_dew", properties -> tooltipItem(properties,
            tooltip("tooltip.gemlike_teaparty.sirens_dew.0", ChatFormatting.GRAY)));
    public static final DeferredItem<Item> UNDERGROUND_SUN = ITEMS.registerItem("underground_sun", properties -> tooltipItem(properties,
            tooltip("tooltip.gemlike_teaparty.underground_sun.0", ChatFormatting.RED)));

    public static final DeferredItem<Item> BLUEBERRY_JUICE = ITEMS.registerSimpleItem("blueberry_juice");
    public static final DeferredItem<Item> CHERRY_TEA = ITEMS.registerSimpleItem("cherry_tea");
    public static final DeferredItem<Item> LEMONADE = ITEMS.registerSimpleItem("lemonade");

    public static final DeferredItem<Item> BLUEBERRY = ITEMS.registerSimpleItem("blueberry");
    public static final DeferredItem<Item> ICE_CUBE = ITEMS.registerSimpleItem("ice_cube");
    public static final DeferredItem<Item> LEMON = ITEMS.registerSimpleItem("lemon");
    public static final DeferredItem<Item> RED_GRAPE = ITEMS.registerSimpleItem("red_grape");
    public static final DeferredItem<Item> SOUL = ITEMS.registerSimpleItem("soul");

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    private static TooltipItem tooltipItem(Item.Properties properties, TooltipItem.TooltipLine... tooltipLines) {
        return new TooltipItem(properties, tooltipLines);
    }

    private static TooltipItem.TooltipLine tooltip(String key, ChatFormatting color) {
        return new TooltipItem.TooltipLine(key, color);
    }
}
